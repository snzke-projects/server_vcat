<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>退款管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
            $("#contentTable").datagrid(getDatagridOption({
                url: ctx+'/ec/refund/listData?'+$('#searchForm').serialize(),
                frozenColumns:[[
                    {field:'orderNumber',title:'订单号',width:150
                        ,formatter: function(value,row){
                            return '<a href="javascript:void(0)" onclick="stopBubble();$(\'#keyWord\').val(\'' + row.orderItem.order.orderNumber + '\');reloadGrid(\'/ec/refund/listData\')">' + row.orderItem.order.orderNumber + '</a>';
                        }
                    },
                    {field:'paymentGatewayName',title:'支付方式',width:60
                        ,formatter: function(value,row){
                            return row.payment && row.payment.gateway ? row.payment.gateway.name : "无";
                        }
                    }
                ]],
                columns:[[
                    {field:'addDate',title:'商品分类'
                        ,formatter: function(value,row){
                            return '<a href="javascript:void(0)" onclick="stopBubble();$(\'#categoryId\').val(\'' + row.product.category.id + '\');$(\'#categoryName\').val(\'' + row.product.category.name + '\');reloadGrid(\'/ec/refund/listData\')">' + row.product.category.name + '</a>';
                        }
                    },
                    {field:'supplierName',title:'供应商'
                        ,formatter: function(value,row){
                            return '<a href="javascript:void(0)" onclick="stopBubble();$(\'#supplier\').val(\'' + row.product.brand.supplier.id + '\');$(\'#supplier\').select2();reloadGrid(\'/ec/refund/listData\')">' + row.product.brand.supplier.name + '</a>';
                        }
                    },
                    {field:'shopName',title:'店铺名称'
                        ,formatter: function(value,row){
                            return row.shop.customer.userName
                        }
                    },
                    {field:'productName',title:'商品名称'
                        ,formatter: function(value,row){
                            return '<span title="' + row.product.name + '">' + row.product.name + '</span>';
                        }
                    },
                    {field:'specName',title:'规格名称'
                        ,formatter: function(value,row){
                            return '<span title="' + row.orderItem.productItem.name + '">' + row.orderItem.productItem.name + '</span>';
                        }
                    },
                    {field:'applyTime',title:'申请时间'},
                    {field:'amount',title:'退款金额'},
                    {field:'buyerUserName',title:'买家帐号'
                        ,formatter: function(value,row){
                            return row.customer.userName
                        }
                    },
                    {field:'returnStatusLabel',title:'退货状态'
                        ,styler: function(value,row){
                            return 'color:' + row.returnStatusColor + ';';
                        }
                    },
                    {field:'refundStatusLabel',title:'退款状态'
                        ,styler: function(value,row){
                            return 'color:' + row.refundStatusColor + ';';
                        }
                    },
                    {field:'refundInterfaceLabel',title:'退款执行状态'
                        ,styler: function(value,row){
                            return 'color:' + row.refundInterfaceColor + ';';
                        }
                    },
                    {field:'operBy',title:'最后操作人'
                        ,formatter: function(value,row){
                            return value ? value.name : "未知";
                        }
                    },
                    {field:'operDate',title:'最后操作时间'
                        ,formatter: function(value,row){
                            return row.finishTime ? row.finishTime : value;
                        }
                    },
                    {field:'oper',title:'操作'
                        ,formatter: function(value,row){
                            var html = '<a href="javascript:void(0);" onclick="stopBubble();showRefund(\'' + row.id + '\',' + ('historyRefundList' === '${type}') + ')">查看</a>';
                            <shiro:hasPermission name="ec:finance:refund:verify">
                            if(row.canVerify){
                                html += ' <a href="javascript:void(0)" onclick="stopBubble();showRefund(\'' + row.id + '\')">审核</a>';
                            }
                            </shiro:hasPermission>
                            <shiro:hasPermission name="ec:finance:refund:autoRefund">
                            if(row.canExecution && (!row.refundInterface || !row.refundInterface.result)){
                                var isRe = (row.refundInterface && (!row.refundInterface.requestSuccess || !row.refundInterface.result)) ? "重新" : "";
                                html += ' <a href="javascript:void(0)" onclick="stopBubble();' +
                                        'executionRefund(\'' + row.id + '\',\''
                                        + row.payment.gateway.code + '\',\''
                                        + row.orderItem.order.orderNumber + '\',\''
                                        + row.product.name + '\',\''
                                        + row.orderItem.productItem.name + '\',\''
                                        + row.amount + '\')">' + isRe + '执行退款</a>';
                            }
                            </shiro:hasPermission>
                            <shiro:hasPermission name="ec:finance:refund:confirm">
                            if(row.canConfirm){
                                html += ' <a href="javascript:void(0)" onclick="stopBubble();confirmRefund(\'' + row.id + '\',2,\'退款完成\')">退款完成</a>';
                            }
                            </shiro:hasPermission>
                            return html;
                        }
                    }
                ]]
            }));
        });
        function executionRefund(refundId,gatewayCode,orderNumber,productName,specName,amount){
            if("alipay" === gatewayCode){
                window.open(ctx + "/ec/refund/refundRequestByAlipay?id=" + refundId);
                // 十秒后刷新该页面，获取最新退款结果
                setTimeout(function(){
                    reloadGrid('/ec/refund/listData');
                },10000);
            }else if("Wechat" === gatewayCode){
                showLayer();
                loading();
                $.get(ctx + "/ec/refund/refundRequestByWechat?id=" + refundId,function (){
                    alertx('<span>订单：' + orderNumber + '<br>商品：' + productName + '<br>规格：' + specName + '<br>金额：' + amount + '<br>微信退款完成</span>');
                    hideLayer();
                    closeTip();
                    reloadGrid('/ec/refund/listData');
                });
            }else if("WechatMobile" === gatewayCode){
                showLayer();
                loading();
                $.get(ctx + "/ec/refund/refundRequestByWechatMobile?id=" + refundId,function (){
                    alertx('<span>订单：' + orderNumber + '<br>商品：' + productName + '<br>规格：' + specName + '<br>金额：' + amount + '<br>微信退款完成</span>');
                    hideLayer();
                    closeTip();
                    reloadGrid('/ec/refund/listData');
                });
            }else if("tenpay" === gatewayCode){
                alertx("暂不支持财付通退款");
            }else{
                alertx("暂不支持该支付方式退款");
            }
        }
		function showRefund(refundId,isHistory){
			if(isHistory){
				$('.no-view').hide();
			}
			showRefundDetail(refundId,function (refund){
				$('#refundId').val(refund.id);
				$('#returnPhone').val(refund.phone);
				$('#orderNumber').val(refund.orderItem.order.orderNumber);
				$('#returnStatus').val(refund.returnStatus);
				if(refund.canVerify){
					$('.verify').show();
				}
				if(refund.canConfirm){
					$('.confirm').show();
				}
			});
			showDialog('refundDialog');
		}
		function verify(id,status){
			if("" == $('#verifyNote').val()){
				alertx("请填写审核说明！",function(){
					$('#verifyNote').focus();
				});
				return;
			}
			$.ajax({
				type : 'POST',
				url : "${ctx}/ec/refund/verify",
				data : {
					id : id,
					refundStatus : status,
					returnStatus : $('#returnStatus').val(),
					verifyNote : $('#verifyNote').val(),
					phone : $('#returnPhone').val(),
					"orderItem.order.orderNumber" : $('#orderNumber').val()
				},
				success : function(){
					try{
						alertx("审核退款成功！",function(){
							$('#keyWord').val($('#refundOrderNumber').html());
							reloadGrid('/ec/refund/listData');
						});
					}catch(e){
						console.log("审核退款失败："+e.message);
						return doError(e);
					}
                    showDialog('refundDialog');
				},
				error: function(XMLHttpRequest) {
					return doError(XMLHttpRequest);
				}
			});
		}
		function confirmRefund(id,status,msg){
			msg = isNull(msg) ? "处理退款" : msg;
			confirmx("确认"+msg+"！？",function(){
				$.ajax({
					type : 'POST',
					url : "${ctx}/ec/refund/confirmRefundCompleted",
					data : {
						id : id,
						refundStatus : status,
						returnStatus : $('#returnStatus').val(),
						verifyNote : $('#verifyNote').val(),
						phone : $('#returnPhone').val(),
						"orderItem.order.orderNumber" : $('#orderNumber').val()
					},
					success : function(){
						try{
							alertx(msg+"成功！",function(){
								$('#keyWord').val($('#refundOrderNumber').html());
								reloadGrid('/ec/refund/listData');
                                if(!$('#refundDialog').is(":hidden")){
                                    showDialog('refundDialog');
                                }
							});
						}catch(e){
							console.log(msg+"失败："+e.message);
							return doError(e);
						}
					},
					error: function(XMLHttpRequest) {
						return doError(XMLHttpRequest);
					}
				});
			});
		}
		function clearForm(){
			$('#categoryId').val("");
			$('#categoryName').val("");
			$('#supplier').val("");
			$('#s2id_supplier .select2-chosen').html("");
			$('#refundStatus').val("");
			$('#s2id_refundStatus .select2-chosen').html("");
			$('#keyWord').val("");
			$('#st').val("");
			$('#et').val("");
			$(':checkbox:checked').attr("checked",false);
		}
	</script>
</head>
<body>
<form:form id="searchForm" modelAttribute="refund" action="${ctx}/ec/refund/listData?type=${type}" method="post" class="form-search">
	<div class="panel panel-info">
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				查询条件
			</div>
		</div>
		<div class="panel-body">
			<input id="type" name="type" type="hidden" value="${type}"/>
			<label>分类：</label>
			<sys:treeselect id="category" name="product.category.id" value="${refund.product.category.id}" labelName="product.category.name" labelValue="${refund.product.category.name}"
							title="栏目" url="/ec/category/treeData" module="product" notAllowSelectRoot="false" cssClass="input-small"/>
			<label>所属供货商：</label>
			<form:select id="supplier" path="product.brand.supplier.id" class="input-medium">
				<form:option value="" label="全部"/>
				<form:options items="${supplierList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
			</form:select>
			<label>退款状态：</label>
			<form:select id="refundStatus" path="refundStatus" class="input-small">
				<form:option value="" label="全部"/>
				<form:options items="${fns:getDictList('ec_refund_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</form:select>
            <label>支付方式：</label>
            <form:select id="paymentGatewayId" path="payment.gateway.id" class="input-small">
                <form:option value="" label="全部"/>
                <c:forEach items="${gatewayList}" var="gateway">
                    <form:option value="${gateway.id}" label="${gateway.name}"/>
                </c:forEach>
            </form:select>
            <div style="margin-top: 5px;">
                <label>关键字：</label>
                <form:input id="keyWord" path="sqlMap['keyWord']" htmlEscape="false" maxlength="50" class="input-large"/>
                <label>退款申请时间：</label>
                <input id="st" name="sqlMap['st']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width: 130px;"
                       value="${refund.sqlMap['st']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
                <label>至</label>
                <input id="et" name="sqlMap['et']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width: 130px;"
                       value="${refund.sqlMap['et']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
                &nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="reloadGrid('/ec/refund/listData')"/>
                <input id="btnClear" class="btn btn-default" type="button" value="清空" onclick="clearForm()"/>
            </div>
		</div>
	</div>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable"></table>

<div id="refundDialog" class="vcat-dialog" style="width: 80%;">
	<div class="panel panel-info"><!-- 退货单 -->
		<div class="panel-heading">
			<div class="panel-title">
				退款单
				<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('refundDialog')"></a>
			</div>
		</div>
		<div class="panel-body">
			<table class="table table-bordered table-hover">
                <%@include file="detailTr.jsp" %>
                <tr class="no-view">
                    <td><span class="pull-right">退款审核说明：</span></td>
                    <td colspan="3">
                        <textarea id="verifyNote" cols="10" rows="3" style="resize:none;width:95%;margin-bottom: 0px;" placeholder="如驳回退款或退款失败，系统将发送该说明给退款买家，请勿随意填写。"></textarea>
                    </td>
                </tr>
			</table>
			<shiro:hasPermission name="ec:finance:refund:verify">
				<div class="form-actions verify hide" style="margin-top: 0px;margin-bottom: 0px;padding: 5px 15px 5px;">
					<input type="hidden" id="refundId">
					<input type="hidden" id="returnPhone">
					<input type="hidden" id="orderNumber">
					<input type="hidden" id="returnStatus">
					<input onclick="verify($('#refundId').val(),1)" class="btn btn-success" type="button" value="通 过"/>
					<input onclick="verify($('#refundId').val(),3)" class="btn btn-danger" type="button" value="驳 回"/>
				</div>
			</shiro:hasPermission>
			<shiro:hasPermission name="ec:finance:refund:confirm">
				<div class="form-actions confirm hide" style="margin-top: 0px;margin-bottom: 0px;padding: 5px 15px 5px;">
					<input onclick="confirmRefund($('#refundId').val(),2,'退款完成')" class="btn btn-success" type="button" value="退款完成"/>
					<%--<input onclick="confirmRefund($('#refundId').val(),3,'退款失败')" class="btn btn-danger" type="button" value="退款失败"/>--%>
				</div>
			</shiro:hasPermission>
		</div>
	</div>
</div>
<jsp:include page="../express/dialog.jsp"></jsp:include>
</body>
</html>