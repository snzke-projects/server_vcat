<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
            $('#itemTable').datagrid(getDatagridOption({
                url: ctx + "/ec/order/itemListData?order.id=${order.id}",
                frozenColumns:[[            // 锁定列
                    {field:'orderTypeLabel',title:'订单类型'},
                    <shiro:hasPermission name="ec:showEarning">
                    {field:'shopName',title:'所属店铺',formatter:function(value,row){return row.shop ? row.shop.name : "无";}},
                    {field:'phoneNumber',title:'店铺手机',formatter:function(value,row){return row.shop ? row.shop.customer.phoneNumber : "无";}},
                    {field:'farmName',title:'庄园名称',formatter:function(value,row){return row.shop ? row.shop.farmName : "无";}}
                    </shiro:hasPermission>
                ]],
                columns:[[
                    {field:'brandName',title:'品牌',formatter:function(value,row){return row.productItem.product.brand.name;}},
                    {field:'productName',title:'商品名称',formatter:function(value,row){
                        return '<a href="javascript:void(0)" onclick="stopBubble();showViewPage(ctx + \'/ec/product/form?isView=true&id=' + row.productItem.product.id + '\',\'' + row.productItem.product.name + '商品详情\')" title="' + row.productItem.product.name + '">' + row.productItem.product.name + '</a>';
                    }},
                    {field:'productItemName',title:'规格名称',formatter:function(value,row){return row.productItem.name;}},
                    {field:'itemPrice',title:'售价'},
                    {field:'saleEarning',title:'销售佣金'},
                    {field:'bonusEarning',title:'分红奖励'},
                    {field:'firstBonusEarning',title:'一级团队分红'},
                    {field:'secondBonusEarning',title:'二级团队分红'},
                    {field:'point',title:'平台扣点'},
                    {field:'quantity',title:'购买数量'},
                    {field:'promotionQuantity',title:'赠送数量'},
                    {field:'productInventory',title:'商品库存',formatter:function(value,row){return row.productItem.inventory;}},
                    {field:'itemTotalPrice',title:'商品总价',formatter:function(value,row){return parseFloat(row.quantity).mul(row.itemPrice);}},
                    {field:'refundInfo',title:'退款信息',formatter: function(value,row){
                        var html = "";
                        <c:if test="${!isView}">
                        <shiro:hasPermission name="ec:order:verifyReturn">
                        if(row.refund && row.refund.isActivate == 1
                                && row.refund.returnStatus == 0
                                && row.refund.refundStatus == 0){
                            html += '<a href="javascript:void(0);" onclick="stopBubble();showVerifyReturn(\'' + row.refund.id + '\')">审核</a>';
                        }
                        </shiro:hasPermission>
                        <shiro:hasPermission name="ec:order:confirmReturn">
                        if(row.refund && row.refund.canConfirmReturn){
                            html += ' <a href="javascript:void(0);" onclick="stopBubble();showConfirmReturn(\'' + row.refund.id + '\')">确认收到退货</a>';
                        }
                        </shiro:hasPermission>
                        if(row.refund && row.refund.isActivate == 1 && row.refund.returnStatus != 4 && '23'.indexOf(row.refund.refundStatus) < 0){
                            html += ' <a href="javascript:void(0);" onclick="stopBubble();revocationRefund(\'' + row.refund.id + '\')">撤销退款单</a>';
                        }
                        if(null == row.refund || row.refund.isActivate == 0){
                            html += ' <a href="javascript:void(0);" onclick="stopBubble();showCreateRefund(\'' + row.id + '\',\'' + row.productItem.product.name + '\',\'' + row.itemPrice + '\',\'' + row.quantity + '\',\'' + parseFloat(row.quantity).mul(row.itemPrice) + '\',\'' + row.refundCount + '\')">创建退款单</a>';
                        }
                        </c:if>
                        if(row.refund){
                            html += ' <a href="javascript:void(0);" onclick="stopBubble();showReturnView(\'' + row.refund.id + '\')" class="showRefundLink">查看</a>';
                        }
                        if(row.refundCount && row.refundCount > 1){
                            html += ' <a href="javascript:void(0);" onclick="stopBubble();showReturnHistory(\'' + row.id + '\')">历史</a>';
                        }
                        return html;
                    }}
                ]]
            }));
        })
        function showShipping(){
            $('#receiverName').html($('#receiverNameTD').html());
            $('#receiverPhone').html($('#receiverPhoneTD').html());
            $('#receiverAddress').html($('#receiverAddressTD').html());
            $('#receiverAddress a').hide();
            $('#deliveryDate').val(new Date().pattern('yyyy-MM-dd HH:mm'));
            $('#freightCharge').val(0);
            if($('.showRefundLink').length > 0){
                confirmx('<span style="color:red;">此订单包含退款信息，确认继续发货？</span>',function(){
                    showDialog('shippingDialog');
                });
                return;
            }
            showDialog('shippingDialog');
        }
        function delivery(){
            var expressId = $('#expressId').val();
            var deliveryDate = $('#deliveryDate').val();
            var shippingNumber = $('#shippingNumber').val();
            var freightCharge = $("#freightCharge").val();
            var shippingNote = $("#shippingNote").val();

            if(isNull(expressId)){
                alertx("请选择物流公司！",function (){
                    $("#expressId").select2("open");
                });
                return;
            }

            if(isNull(deliveryDate)){
                alertx("请输入发货日期！",function (){
                    $("#deliveryDate").trigger("click");
                });
                return;
            }

            if(isNull(shippingNumber)){
                alertx("请输入物流单号！",function (){
                    $("#shippingNumber").focus();
                });
                return;
            }
            $('#shippingNumber').val(shippingNumber.replace(/ /g,''));

            if(isNull(freightCharge) || !isMoney(freightCharge)){
                alertx("运费输入格式不正确！",function(){
                    $("#freightCharge").focus();
                });
                return;
            }

            confirmx("请确认物流信息填写无误！",function (){
                loading('正在提交，请稍等...');
                $("#shippingForm").submit();
            });
        }

		function showReturnView(refundId){
			$('.viewReturn').hide();
			showRefundDetail(refundId);
			showDialog('returnDialog');
		}
		function showReturn(id){
			showRefundDetail(id,function (){
				$('#returnDialog :input[name=id]').val(this.id);
				$('#returnPhone').val(this.phone);
				$('#refundStatus').val(this.refundStatus);
                $('#isReceipt').val(this.isReceipt);
			});
			showDialog('returnDialog');
		}
		function showVerifyReturn(id){
			$('.viewReturn').show();
			$('.returnBtn').hide();
			$('.verify').show();
			showReturn(id);
		}
		function showConfirmReturn(id){
			$('.viewReturn').show();
			$('.returnBtn').hide();
			$('.confirm').show();
			showReturn(id);
		}
		function verifyReturn(status){
			if("" == $('#returnDialog :input[name=note]').val()){
				alertx("请填写备注！",function(){
					$('#returnDialog :input[name=note]').focus();
				});
				return;
			}
			$.ajax({
				type : 'POST',
				url : "${ctx}/ec/order/verifyReturn",
				data : {
					id : $('#returnDialog :input[name=id]').val(),
					note : $('#returnDialog :input[name=note]').val(),
					returnStatus : status,
					refundStatus : $('#refundStatus').val(),
					phone : $('#returnPhone').val(),
					"orderItem.order.orderNumber" : $("#orderNumber").html(),
					shippingStatus : $('#shippingStatus').val(),
                    isReceipt : $('#isReceipt').val()
				},
				success : function(){
                    alertx("处理退货申请成功！",function(){
                        location.href = location.href;
                    });
				}
			});
		}
		function confirmReturn(status){
			if("" == $('#returnDialog :input[name=note]').val()){
				alertx("请填写备注！",function(){
					$('#returnDialog :input[name=note]').focus();
				});
				return;
			}
			$.ajax({
				type : 'POST',
				url : "${ctx}/ec/order/confirmReturn",
				data : {
					id : $('#returnDialog :input[name=id]').val(),
					note : $('#returnDialog :input[name=note]').val(),
					returnStatus : status,
					refundStatus : $('#refundStatus').val(),
					phone : $('#returnPhone').val(),
					"orderItem.order.orderNumber" : $("#orderNumber").html(),
                    isReceipt : $('#isReceipt').val()
				},
				success : function(){
                    alertx("处理确认收到退货成功！",function(){
                        location.href = location.href;
                    });
				}
			});
		}
        function showUpdateShipping(){
            $('#receiverName').html($('#receiverNameTD').html());
            $('#receiverPhone').html($('#receiverPhoneTD').html());
            $('#receiverAddress').html($('#receiverAddressTD').html());
            $('#freightCharge').attr("readonly",true);
            $('#shippingNote').attr("readonly",true);
            $('#deliveryDate').attr("disabled",true);
            $('.deliveryBtn').hide();
            showDialog('shippingDialog');
        }
        function updateShipping(){
            $.getJSON(ctx+"/ec/order/updateShipping?"+$('#shippingForm').serialize(),function(){
                alertx('保存成功！',function (){location.href = location.href});
            });
        }
        $(function (){
            $("#addressForm").validate({
                submitHandler: function(form){
                    form.submit();
                },
            });
        });
        function showReturnHistory(itemId){
            showViewPage(ctx+"/ec/refund/showReturnHistory?orderItem.id="+itemId,"退款历史");
        }
        function showCreateRefund(itemId,productName,singlePrice,purchaseQuantity,commodityPrice,createRefundCount){
            $("#orderItemId").val(itemId);
            $("#productName").html(productName);
            $("#singlePrice").html(singlePrice);
            $("#purchaseQuantity").html(purchaseQuantity);
            $("#commodityPrice").html(commodityPrice);
            $("#createRefundCount").val(createRefundCount);
            $("#createRefundQuantity").val(purchaseQuantity);
            showDialog('createRefundDialog');
        }
        function createRefund(){
            var refundQuantity = $("#createRefundQuantity").val();
            var purchaseQuantity = $("#purchaseQuantity").html();
            var refundNote = $("#createRefundNote").val();
            if(isNull(refundQuantity)
                    || !isInteger(refundQuantity)
                    || (isInteger(purchaseQuantity) && parseInt(purchaseQuantity) < parseInt(refundQuantity))){
                alertx("退款数量格式不正确！",function (){$("#createRefundQuantity").focus()});
                return;
            }
            if(isNull(refundNote)){
                alertx("请输入退款原因！",function (){$("#createRefundNote").focus()});
                return;
            }

            confirmx("确认创建退款单？",function (){
                loading();
                $.post(ctx + "/ec/order/createRefund?" + $('#createRefundForm').serialize(),
                    function (){
                        alertx("创建退款单成功！",function (){location.href = location.href});
                    }
                );
            });
        }
        function revocationRefund(refundId){
            confirmx("确认撤销该退款单？",function (){
                loading();
                $.post(ctx + "/ec/order/revocationRefund?id=" + refundId,
                    function (){
                        alertx("撤销退款单成功！",function (){location.href = location.href});
                    }
                );
            });
        }
        function updateNote(){
            confirmx("确认修改订单备注？",function (){
                loading();
                $.post(ctx + "/ec/order/updateNote?id=${order.id}&note=" + $('#orderNote').val() + "&sqlMap['oldNote']=" + $('#oldOrderNote').val(),
                    function (){
                        alertx("修改订单备注成功！",function (){location.href = location.href});
                    }
                );
            });
        }
	</script>
</head>
<body>
	<form:hidden path="order.id"/>
	<sys:message content="${message}"/>
	<input type="hidden" id="shippingStatus" name="shippingStatus" value="${order.shippingStatus}">
	<div class="panel panel-info"> <!-- 订单详情 -->
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				<span>订单详情</span>
				<div class="btn-group pull-right">
                    <c:if test="${!isView}">
					<shiro:hasPermission name="ec:order:confirm">
						<c:if test="${order.paymentStatusPaid eq order.paymentStatus && order.orderStatusToBeConfirmed eq order.orderStatus && order.shippingStatusToBeShipped eq order.shippingStatus}">
							<button class="btn btn-small" href="${ctx}/ec/order/confirm?id=${order.id}&orderNumber=${order.orderNumber}&from=form" onclick="confirmx('确认订单 ${order.orderNumber}？',$(this).attr('href'))">确认订单</button>
						</c:if>
					</shiro:hasPermission>
					<shiro:hasPermission name="ec:order:delivery">
						<c:if test="${order.paymentStatusPaid eq order.paymentStatus && order.orderStatusConfirmed eq order.orderStatus && order.shippingStatusToBeShipped eq order.shippingStatus}">
							<button class="btn btn-small" onclick="showShipping()">确认发货</button>
						</c:if>
					</shiro:hasPermission>
                    </c:if>
					<button class="btn btn-small" onclick="showDialog('orderOperLogDialog')">查看订单操作日志</button>

                    <c:if test="${isView && !banBack}">
					    <button class="btn btn-small" onclick="history.back()">返回订单列表</button>
                    </c:if>
                    <c:if test="${!isView && !banBack}">
                        <button class="btn btn-small" onclick="location.href=ctx+'/ec/order/returnList?pageNo=${listOrder.page.pageNo}&pageSize=${listOrder.page.pageSize}'">返回订单列表</button>
                    </c:if>
				</div>
			</div>
		</div>
		<div class="panel-body">
			<table class="table table-bordered table-hover">
                <tr>
                    <th>订单号</th>
                    <th>订单类型</th>
                    <th>支付状态</th>
                    <th>订单状态</th>
                    <th>发货状态</th>
                    <th>订单邮费</th>
                    <th>使用V猫币</th>
                    <th>订单总金额</th>
                    <th>下单时间</th>
                </tr>
                <tr>
                    <td><span id="orderNumber">${order.orderNumber}</span></td>
                    <td><span>${order.orderType}</span></td>
                    <td><span style="color:${order.paymentStatusColor};font-weight: bold;">${fns:getDictLabel(order.paymentStatus,'ec_payment_status',"未支付")}</span></td>
                    <td><span style="color:${order.orderStatusColor};font-weight: bold;">${fns:getDictLabel(order.orderStatus,'ec_order_status',"未确认")}</span></td>
                    <td><span style="color:${order.shippingStatusColor};font-weight: bold;">${fns:getDictLabel(order.shippingStatus,'ec_shipping_status',"未发货")}</span></td>
                    <td>${order.freightPrice}</td>
                    <td>${order.totalCoupon}</td>
                    <td>${order.totalPrice}</td>
                    <td><fmt:formatDate value="${order.addDate}" pattern="yyyy年MM月dd日 HH点mm分"/></td>
                </tr>
			</table>
			<label>订单备注</label></br>
			<pre style="font-size: 13px;">${order.note}<c:if test="${null == order.note || '' eq order.note}">暂无备注</c:if> <a href="javascript:void(0);" onclick="showDialog('noteDialog')">编辑备注</a>
            </pre>
            <label>发票信息</label>
            <table class="table table-bordered table-hover">
                <tr>
                    <c:if test="${null == order.invoice}">
                    <td>未索要发票</td>
                    </c:if>
                    <c:if test="${null != order.invoice}">
                    <td style="color: green;">发票类型：${fns:getDictLabel(order.invoice.type,'ec_invoice_type','未知类型')}</td>
                </tr>
                <tr>
                    <td>发票抬头：${order.invoice.title}</td>
                    </c:if>
                </tr>
            </table>
		</div>
	</div>
	<div class="panel panel-info"><!-- 收货人信息 -->
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				收货人信息
			</div>
		</div>
		<div class="panel-body">
			<table class="table table-bordered table-hover">
                <tr>
                    <th width="15%">买家帐号</th>
                    <th width="15%">收货人</th>
                    <th width="15%">收货手机号</th>
                    <th width="55%">详细地址</th>
                </tr>
                <tr>
                    <td>${order.buyer.userName}</td>
                    <td id="receiverNameTD">${order.address.deliveryName}</td>
                    <td id="receiverPhoneTD">${order.address.deliveryPhone}</td>
                    <td id="receiverAddressTD">
                        ${order.address.province}&nbsp;
                        ${order.address.city}&nbsp;
                        ${order.address.district}&nbsp;
                        ${order.address.detailAddress}&nbsp;
                        <shiro:hasPermission name="ec:order:modifyAddress">
                            <c:if test="${!isView && order.shippingStatus == '0'}"><a href="javascript:void(0);" onclick="showDialog('addressDialog')" class="link">修改</a></c:if>
                        </shiro:hasPermission>
                    </td>
                </tr>
			</table>
		</div>
	</div>
    <shiro:hasPermission name="ec:order:modifyAddress">
    <div id="addressDialog" class="vcat-dialog" style="width: 80%;">
        <div class="panel panel-info"><!-- 退货信息 -->
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    收货地址
                    <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('addressDialog')"></a>
                </div>
            </div>
            <div class="panel-body">
                <form id="addressForm" action="${ctx}/ec/order/modifyAddress" method="post">
                    <table class="table table-responsive table-hover">
                        <tr>
                            <td><span class="pull-right">收货人：</span></td>
                            <td><input type="text" name="deliveryName" class="required input-medium" value="${order.address.deliveryName}"/></td>
                            <td><span class="pull-right">手机号：</span></td>
                            <td><input type="text" name="deliveryPhone" class="required input-medium" value="${order.address.deliveryPhone}"/></td>
                        </tr>
                        <tr>
                            <td><span class="pull-right">省份：</span></td>
                            <td colspan="3"><input type="text" name="province" class="required input-medium" value="${order.address.province}"/></td>
                        </tr>
                        <tr>
                            <td><span class="pull-right">城市：</span></td>
                            <td colspan="3"><input type="text" name="city" class="required input-medium" value="${order.address.city}"/></td>
                        </tr>
                        <tr>
                            <td><span class="pull-right">区县：</span></td>
                            <td colspan="3"><input type="text" name="district" class="required input-medium" value="${order.address.district}"/></td>
                        </tr>
                        <tr>
                            <td><span class="pull-right">详细地址：</span></td>
                            <td colspan="3"><input type="text" name="detailAddress" class="required input-xxlarge" value="${order.address.detailAddress}"/></td>
                        </tr>
                    </table>
                    <div class="form-actions">
                        <input type="hidden" name="id" value="${order.id}">
                        <input type="hidden" name="sqlMap['oldAddress']" value="${order.address.deliveryName} ${order.address.deliveryPhone} ${order.address.province} ${order.address.city} ${order.address.district} ${order.address.detailAddress}">
                        <input class="btn btn-success" type="submit" value="修改收货地址"/>&nbsp;
                        <input class="btn" type="button" value="返 回" onclick="showDialog('addressDialog')"/>
                    </div>
                </form>
            </div>
        </div>
    </div>
    </shiro:hasPermission>
	<div class="panel panel-info"><!-- 支付配送方式 -->
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				支付配送方式
			</div>
		</div>
		<div class="panel-body">
			<table class="table table-bordered table-hover">
                <tr>
                    <td>
                        支付方式：<c:out value="${order.payment.gateway.name}" default="暂无"/>
                        <shiro:hasPermission name="ec:order:queryLogPay">
                            <c:if test="${null != order.payment && '' ne order.payment.id}">
                                <a href="javascript:void(0);" onclick="showDialog('paymentLogDialog')" class="link">查看支付日志</a>
                            </c:if>
                        </shiro:hasPermission>
                    </td>
                </tr>
                <c:if test="${order.shipping != null}">
                <tr>
                    <td>
                        <span>配送方式：<c:out value="${order.shipping.express.name}" default="暂无"/></span><br>
                        <span>物流单号：<c:out value="${order.shipping.number}" default="暂无"/></span>
                        <c:if test="${!isView}"><a href="javascript:void(0);" onclick="showUpdateShipping()" class="link">修改物流信息</a></c:if>
                        <br><span>发货时间：<fmt:formatDate value="${order.shipping.shippingDate}" pattern="yyyy年MM月dd日 HH点mm分"/></span>
                        <shiro:hasPermission name="ec:order:queryExpress">
                            <a href="javascript:void(0);" onclick="showExpressInfo('${order.shipping.express.code}','${order.shipping.number}')" class="link">查看物流信息</a>
                        </shiro:hasPermission>
                    </td>
                </tr>
                </c:if>
			</table>
		</div>
	</div>
	<div class="panel panel-info"><!-- 商品信息 -->
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				商品信息
			</div>
		</div>
		<div class="panel-body">
			<table id="itemTable" style="width: 100%"></table>
		</div>
	</div>
	<div id="orderOperLogDialog" class="vcat-dialog" style="width: 80%">
		<div class="panel panel-info"><!-- 订单操作日志 -->
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					订单操作日志
					<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('orderOperLogDialog')"></a>
				</div>
			</div>
			<div class="panel-body">
				<table class="table table-bordered table-hover">
                    <tr>
                        <th width="80%">详情</th>
                        <th width="20%">操作时间</th>
                    </tr>
                    <c:forEach items="${order.orderLogList}" var="log">
                        <tr>
                            <td>${log.note}</td>
                            <td><fmt:formatDate value="${log.operDate}" pattern="yyyy年MM月dd日 HH点mm分ss秒"/></td>
                        </tr>
                    </c:forEach>
				</table>
			</div>
		</div>
	</div>
	<div id="paymentLogDialog" class="vcat-dialog">
		<div class="panel panel-info"><!-- 买家支付日志 -->
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					买家支付日志
					<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('paymentLogDialog')"></a>
				</div>
			</div>
			<div class="panel-body">
				<table class="table table-bordered table-hover">
					<tr>
						<th width="16%">支付账户</th>
						<th width="16%">支付金额</th>
						<th width="16%">支付方式</th>
						<th width="16%">支付平台交易号</th>
						<th width="16%">支付状态</th>
						<th width="20%">支付日期</th>
					</tr>
					<c:forEach items="${order.paymentLogList}" var="log">
						<tr>
							<td>${log.customer.userName}</td>
							<td>${log.amount}</td>
							<td>${log.gateway}</td>
							<td>${log.transactionNo}</td>
							<td style="color: ${log.color}" >${log.label}</td>
							<td><fmt:formatDate value="${log.transactionDate}" pattern="yyyy年MM月dd日 HH点mm分ss秒"/></td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</div>
	</div>
	<div id="shippingDialog" class="vcat-dialog">
		<div class="panel panel-info"><!-- 发货单 -->
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					发货单
					<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('shippingDialog')"></a>
				</div>
			</div>
			<div class="panel-body">
				<form id="shippingForm" action="${ctx}/ec/order/delivery" method="POST">
					<table class="table table-responsive table-hover">
                        <tr>
                            <td>　收货人：</td>
                            <td><span id="receiverName"></span></td>
                            <td>　手机号：</td>
                            <td><span id="receiverPhone"></span></td>
                        </tr>
                        <tr>
                            <td>收货地址：</td>
                            <td colspan="3"><span id="receiverAddress"></span></td>
                        </tr>
                        <tr>
                            <td>物流公司：</td>
                            <td>
                                <select id="expressId" name="shipping.express.id" class="input-large" onchange="$('#expressName').val($(this).find(':selected').html());$('#expressCode').val($(this).find(':selected').attr('code'))">
                                    <option value=""></option>
                                    <c:forEach items="${expressList}" var="express">
                                        <c:if test="${express.code != 'other'}">
                                            <option value="${express.id}" code="${express.code}" <c:if test="${express.id == order.shipping.express.id}">selected</c:if> >${express.name}</option>
                                        </c:if>
                                    </c:forEach>
                                </select>
                            </td>
                            <td>发货日期：</td>
                            <td>
                                <input id="deliveryDate" type="text" name="shipping.shippingDate" readonly="readonly" maxlength="16" class="input-date Wdate"
                                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});" style="margin-bottom: 0px;" value="<fmt:formatDate value="${order.shipping.shippingDate}" pattern="yyyy-MM-dd HH:mm"/>"/>
                            </td>
                        </tr>
                        <tr>
                            <td>物流单号：</td>
                            <td>
                                <input id="shippingNumber" type="text" name="shipping.number" maxlength="20" class="input-large" style="margin-bottom: 0px;" value="${order.shipping.number}">
                            </td>
                            <td>运　　费：</td>
                            <td>
                                <input id="freightCharge" type="text" name="shipping.freightCharge" maxlength="13" style="margin-bottom: 0px;width: 50px;" value="${order.shipping.freightCharge}">
                            </td>
                        </tr>
                        <tr>
                            <td>备　　注：</td>
                            <td colspan="3">
                                <textarea id="shippingNote" name="shipping.note" cols="10" rows="3" style="resize:none;width:95%;margin-bottom: 0px;" value="${order.shipping.note}"></textarea>
                            </td>
                        </tr>
                    </table>
                    <div class="form-actions">
                        <input type="hidden" name="id" value="${order.id}">
                        <input type="hidden" name="orderNumber" value="${order.orderNumber}">
                        <input type="hidden" name="shipping.id" value="${order.shipping.id}">
                        <input type="hidden" name="shipping.express.name" id="expressName">
                        <input type="hidden" name="shipping.express.code" id="expressCode">
                        <shiro:hasPermission name="ec:order:delivery"><input class="btn btn-primary deliveryBtn" type="button" value="确认发货" onclick="delivery()"/>&nbsp;</shiro:hasPermission>
                        <c:if test="${order.shipping != null && order.shipping.id != null && order.shipping.id ne ''}">
                            <input class="btn btn-primary updateShippingBtn" type="button" value="保存" onclick="updateShipping()"/>&nbsp;
                        </c:if>
                        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="showDialog('shippingDialog')"/>
                    </div>
				</form>
			</div>
		</div>
	</div>
	<div id="returnDialog" class="vcat-dialog" style="width: 80%;">
		<div class="panel panel-info"><!-- 退货信息 -->
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					退货信息
					<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('returnDialog')"></a>
				</div>
			</div>
			<div class="panel-body">
				<form id="returnForm">
					<table class="table table-bordered table-hover">
                        <jsp:include page="../refund/detailTr.jsp"></jsp:include>
                        <tr class="viewReturn">
                            <td><span class="pull-right">备注：</span></td>
                            <td colspan="3"><textarea name="note" cols="10" rows="3" style="resize:none;width:95%;" placeholder="如驳回退货申请，系统将发送该说明给退款买家，请勿随意填写。"></textarea></td>
                        </tr>
					</table>
					<div class="form-actions viewReturn">
						<input type="hidden" name="id">
						<input type="hidden" id="returnPhone">
						<input type="hidden" id="refundStatus">
                        <input type="hidden" id="isReceipt">
						<shiro:hasPermission name="ec:order:verifyReturn"><input onclick="verifyReturn(1)" class="btn btn-success returnBtn verify" type="button" value="同意退货申请"/></shiro:hasPermission>
						<shiro:hasPermission name="ec:order:verifyReturn"><input onclick="verifyReturn(4)" class="btn btn-danger returnBtn verify" type="button" value="驳回退货申请"/></shiro:hasPermission>
						<shiro:hasPermission name="ec:order:confirmReturn"><input onclick="confirmReturn(3)" class="btn btn-success returnBtn confirm" type="button" value="确认收到退货"/></shiro:hasPermission>
						<shiro:hasPermission name="ec:order:confirmReturn"><input onclick="confirmReturn(4)" class="btn btn-danger returnBtn confirm" type="button" value="未收到退货"/></shiro:hasPermission>
						<input id="btnCancel" class="btn" type="button" value="返 回" onclick="showDialog('returnDialog')"/>
					</div>
				</form>
			</div>
		</div>
	</div>
    <div id="createRefundDialog" class="vcat-dialog" style="width: 800px;">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    创建退款单
                    <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('createRefundDialog')"></a>
                </div>
            </div>
            <div class="panel-body">
                <form id="createRefundForm">
                    <table class="table table-bordered table-hover">
                        <tr>
                            <td><span class="pull-right">商品名称：</span></td>
                            <td><span id="productName"></span></td>
                        </tr>
                        <tr>
                            <td><span class="pull-right">商品单价：</span></td>
                            <td><span id="singlePrice"></span></td>
                        </tr>
                        <tr>
                            <td><span class="pull-right">购买数量：</span></td>
                            <td><span id="purchaseQuantity"></span></td>
                        </tr>
                        <tr>
                            <td><span class="pull-right">商品总价：</span></td>
                            <td><span id="commodityPrice"></span></td>
                        </tr>
                        <tr>
                            <td><span class="pull-right">退款数量：</span></td>
                            <td><input id="createRefundQuantity" type="text" name="quantity" placeholder="不能大于购买数量"/></td>
                        </tr>
                        <tr>
                            <td><span class="pull-right">退款原因：</span></td>
                            <td><textarea id="createRefundNote" name="returnReason" rows="3" style="width: 95%;margin-bottom: 0px;" placeholder="必填"></textarea></td>
                        </tr>
                    </table>
                    <div class="form-actions">
                        <input type="hidden" id="orderItemId" name="orderItem.id">
                        <input type="hidden" name="customer.id" value="${order.buyer.id}">
                        <input type="hidden" id="createRefundAmount" name="amount">
                        <input type="hidden" id="createRefundCount" name="sqlMap['refundCount']">
                        <input type="hidden" name="sqlMap['freightPrice']" value="${order.freightPrice}">
                        <input onclick="createRefund()" class="btn btn-success" type="button" value="创建退款单"/>
                        <input class="btn" type="button" value="返 回" onclick="showDialog('createRefundDialog')"/>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div id="noteDialog" class="vcat-dialog" style="width: 800px;">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    修改订单备注
                    <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('noteDialog')"></a>
                </div>
            </div>
            <div class="panel-body">
                    <table class="table table-bordered table-hover">
                        <tr>
                            <td width="15%"><span class="pull-right">订单备注：</span></td>
                            <td width="85%"><textarea id="orderNote" rows="3" style="width: 97%;margin-bottom: 0px;">${order.note}</textarea></td>
                        </tr>
                    </table>
                    <div class="form-actions">
                        <input type="hidden" id="oldOrderNote" value="${order.note}"/>
                        <input onclick="updateNote()" class="btn btn-success" type="button" value="确认修改"/>
                        <input class="btn" type="button" value="返 回" onclick="showDialog('noteDialog')"/>
                    </div>
            </div>
        </div>
    </div>
	<jsp:include page="../express/dialog.jsp"></jsp:include>
</body>
</html>