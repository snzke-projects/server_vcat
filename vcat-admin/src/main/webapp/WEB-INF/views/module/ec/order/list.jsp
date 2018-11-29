<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
            var currentQueryType = '${order.sqlMap.type}';
            $("#btnExport").click(function(){
                top.$.jBox.confirm("确认导出？","系统提示",function(v){
                    if(v=="ok"){
                        $("#searchForm").attr("action",ctx + "/ec/order/exportOrderList");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action",ctx + "/ec/order/list");
                    }
                });
            });
            $("#btnExportFarm").click(function(){
                top.$.jBox.confirm("确认导出水果发货单？","系统提示",function(v){
                    $("#searchForm").attr("action",ctx + "/ec/order/exportFarmOrderList");
                    $("#searchForm").submit();
                    $("#searchForm").attr("action",ctx + "/ec/order/list");
                });
            });
            $("#contentTable").datagrid(getDatagridOption({
                <c:if test="${1 == useHisOrderParam}">url : ctx + '/ec/order/listData?'+$('#searchForm').serialize(),</c:if>
                <c:if test="${null != pageNo}">pageNumber : ${pageNo},</c:if>
                <c:if test="${null != pageSize}">pageSize : ${pageSize},</c:if>
                frozenColumns:[[            // 锁定列
                    {field:'orderId',checkbox:true},
                    {field:'orderType',title:'订单类型',width:100},
                    {field:'orderNumber',title:'订单号',width:160
                        ,formatter: function(value,row){
                            return '<a href="' + ctx + '/ec/order/form?id=' + row.id + '" onclick="stopBubble()">' + value + '</a>';
                        }
                    }
                ]],
                columns:[[
                    {field:'addDate',title:'下单时间',width:140},
                    {field:'distributionName',title:'配送方',width:200},
                    {field:'supplierName',title:'供应商',width:200},
                    {field:'shopName',title:'小店名称',width:60},
                    {field:'buyerUserName',title:'买家名称',width:100
                        ,formatter: function(value,row){
                            return row.buyer.userName
                        }
                    },
                    {field:'productName',title:'商品名称',width:400
                        ,formatter: function(value,row){
                        return '<span title="' + value + '">' + value + '</span>';
                    }
                    },
                    {field:'paymentGatewayName',title:'支付方式',width:60
                        ,formatter: function(value,row){
                            return row.payment && row.payment.gateway ? row.payment.gateway.name : "无";
                        }
                    },
                    {field:'totalPrice',title:'订单金额',width:60},
                    {field:'paymentPrice',title:'支付金额',width:60
                        ,formatter: function(value,row){
                            return row.payment ? row.payment.amount : "无";
                        }
                    },
                    {field:'paymentDate',title:'支付时间',width:140
                        ,formatter: function(value,row){
                            return row.payment ? row.payment.paymentDate : "无";
                        }
                    },
                    {field:'hasReturn',title:'包含退货',width:60
                        ,styler: function(value,row){
                            return 'color:' + (value ? 'red' : 'green') + ';';;
                        }
                        ,formatter: function(value,row){
                            return value ? '包含' : '不包含';
                        }
                    },
                    {field:'paymentStatusLabel',title:'支付状态',width:80,styler: function(value,row){return 'color:' + row.paymentStatusColor;}},
                    {field:'orderStatusLabel',title:'订单状态',width:100,styler: function(value,row){return 'color:' + row.orderStatusColor;}},
                    {field:'shippingStatusLabel',title:'发货状态',width:60,styler: function(value,row){return 'color:' + row.shippingStatusColor;}}
                    <c:if test="${null == order.sqlMap.view || '' eq order.sqlMap.view}">
                    ,{field:'oper',title:'操作',width:100
                        ,formatter: function(value,row){
                            var html = '<a href="' + ctx + '/ec/order/form?id=' + row.id + '">查看</a>';
                            <shiro:hasPermission name="ec:order:confirm">
                            if(row.paymentStatus == '1' && row.orderStatus == '0' && row.shippingStatus == '0' && !row.hasReturn){
                                html += ' <a href="' + ctx + '/ec/order/confirm?id=' + row.id + '&orderNumber=' + row.orderNumber + '" onclick="return confirmOrder(this.href)">确认</a>';
                            }
                            if(row.shippingStatus == '1'){
                                html += ' <a href="javascript:void(0)" onclick="stopBubble();showExpressInfo(\'' + row.shipping.express.code + '\',\'' + row.shipping.number + '\',false)">物流</a>';
                            }
                            if($("#type").val() === 'preSaleBalance'){
                                html += ' <a href="javascript:void(0)" onclick="stopBubble();showViewPage(ctx + \'/ec/order/toHandleReserve?id=' + row.id + '\',\'处理预售结余库存\')">处理库存</a>';
                            }
                            </shiro:hasPermission>
                            return html;
                        }
                    }
                    </c:if>
                ]]
            }));
            $('.' + currentQueryType).addClass("active");
            <c:if test="${null == useHisOrderParam || '1' ne useHisOrderParam}">reload();</c:if>
            hideButton();
        });
		function sub(paymentStatus,orderStatus,shippingStatus,type,tab){
			$("#paymentStatus").val(paymentStatus);
			$("#orderStatus").val(orderStatus);
			$("#shippingStatus").val(shippingStatus);
			$("#type").val(type);

            $('.order-tab').removeClass("active");
            $(tab).parent().addClass("active");

            reload();
			return false;
		}
        function hideButton(){
            if($('#type').val() == 'toHandler'){
                $('#btnBatchConfirmOrder').show();
                $('#btnConfirmAllOrder').show();
                $('#btnExportFarm').hide();
            }else if($('#type').val() == 'toBeShipped'){
                $('#btnBatchConfirmOrder').hide();
                $('#btnConfirmAllOrder').hide();
                $('#btnExportFarm').show();
            }else{
                $('#btnBatchConfirmOrder').hide();
                $('#btnConfirmAllOrder').hide();
                $('#btnExportFarm').hide();
            }
        }
        function reload(){
            if($('#useHisOrderParam').val() == "1")$('#useHisOrderParam').val("");
            hideButton();
            reloadGrid('/ec/order/listData');
        }
		function clearForm(){
			$('#orderType').val("");
			$('#s2id_orderType .select2-chosen').html("");
			$('#productId').val("");
			$('#ost').val("");
			$('#oet').val("");
			$('#keyWord').val("");
			$('#productIdProductList').html("");
			$(':checkbox:checked').attr("checked",false);
		}
        function confirmOrder(href){
            stopBubble();
            confirmx('确认订单 ${ord.orderNumber}？',function (){
                location.href = href;
            });
            return false;
        }
        function batchConfirmOrder(){
            var orderIds = getCheckIdArrayParamString('#contentTable','id','orderId');
            confirmx("确认批量确认订单？",function() {
                loading("正在批量确认订单，请稍候...");
                $.ajax({
                    type: 'POST',
                    url: ctx + "/ec/order/batchConfirmOrder?" + orderIds,
                    success: function () {
                        top.$.jBox.tip("批量确认订单成功！");
                        reload();
                    }
                });
            });
        }
        function confirmAllOrder(){
            confirmx("确认按照当前查询条件确认全部订单？",function() {
                loading("正在确认订单，请稍候...");
                $.ajax({
                    type: 'POST',
                    url: ctx + "/ec/order/confirmAllOrder?" + $('#searchForm').serialize(),
                    success: function () {
                        top.$.jBox.tip("批量确认订单成功！");
                        location.href = ctx + "/ec/order/list?sqlMap['type']=toBeShipped&paymentStatus=1&orderStatus=1&shippingStatus=0&useHisOrderParam=1";
                    }
                });
            });
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="order-tab all"><a href="javascript:void(0)" onclick="sub('','','','all',this)">全部订单</a></li>
		<li class="order-tab toPay"><a href="javascript:void(0)" onclick="sub(0,0,0,'toPay',this)">未支付</a></li>
        <li class="order-tab paid"><a href="javascript:void(0)" onclick="sub(1,'','','paid',this)">已支付</a></li>
		<li class="order-tab toHandler"><a href="javascript:void(0)" onclick="sub(1,0,0,'toHandler',this)">待确认</a></li>
		<li class="order-tab toBeShipped"><a href="javascript:void(0)" onclick="sub(1,1,0,'toBeShipped',this)">待发货</a></li>
		<li class="order-tab shipped"><a href="javascript:void(0)" onclick="sub(1,1,1,'shipped',this)">已发货</a></li>
		<li class="order-tab completed"><a href="javascript:void(0)" onclick="sub(1,2,'','completed',this)">已完成</a></li>
		<li class="order-tab toConfirmReturn"><a href="javascript:void(0)" onclick="sub(1,'','','toConfirmReturn',this)">退货(待确认)</a></li>
		<li class="order-tab toConfirmReceivingReturn"><a href="javascript:void(0)" onclick="sub(1,'','','toConfirmReceivingReturn',this)">退货(待收货)</a></li>
		<li class="order-tab onRefund"><a href="javascript:void(0)" onclick="sub(1,'','','onRefund',this)">退款中</a></li>
		<li class="order-tab refundCompleted"><a href="javascript:void(0)" onclick="sub(1,2,'','refundCompleted',this)">退货(退款)完成</a></li>
		<li class="order-tab cancelled"><a href="javascript:void(0)" onclick="sub('',3,'','cancelled',this)">已取消</a></li>
        <%--<li class="order-tab preSaleBalance"><a href="javascript:void(0)" onclick="sub('',2,'','preSaleBalance',this)">预售结余</a></li>--%>
	</ul>
	<form:form id="searchForm" modelAttribute="order" action="${ctx}/ec/order/list" method="POST" class="breadcrumb form-search">
		<input id="paymentStatus" name="paymentStatus" type="hidden" value="${order.paymentStatus}"/>
		<input id="orderStatus" name="orderStatus" type="hidden" value="${order.orderStatus}"/>
		<input id="shippingStatus" name="shippingStatus" type="hidden" value="${order.shippingStatus}"/>
		<input id="view" name="sqlMap['view']" type="hidden" value="${order.sqlMap.view}"/>
		<input id="type" name="sqlMap['type']" type="hidden" value="${order.sqlMap.type}"/>
        <input id="useHisOrderParam" name="useHisOrderParam" type="hidden" value="${useHisOrderParam}">
        <label>商品：</label>
        <sys:productSelect id="productIds" name="sqlMap['productIds']" value="${order.sqlMap.productIds}" radio="false"></sys:productSelect>
        <br>
        <div style="margin-top: 10px;">
            <label>订单类型：</label>
            <form:select path="orderType" class="input-small">
                <form:option value="" label="全部"/>
                <form:options items="${fns:getDictList('ec_order_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
            </form:select>
            <label>配送方：</label>
            <form:select path="sqlMap['distributionId']" class="input-medium">
                <form:option value="" label="全部"/>
                <form:options items="${distributionList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
            </form:select>
            <label>供应商：</label>
            <form:select path="sqlMap['supplierId']" class="input-medium">
                <form:option value="" label="全部"/>
                <form:options items="${supplierList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
            </form:select>
            <label>关键字：</label>
                <form:input id="keyWord" path="sqlMap['keyWord']" htmlEscape="false" maxlength="50" class="input-xlarge" placeholder="订单号|用户名称|手机号|运单号|收货人"/>&nbsp;
            <label>排序规则：</label>
            <form:select id="orderBy" path="sqlMap['orderBy']" cssClass="input-small">
            <form:option value="default">默认排序</form:option>
            <form:option value="g1u">下单时间升序</form:option>
            <form:option value="g1d">下单时间降序</form:option>
            <form:option value="g2u">供货商升序</form:option>
            <form:option value="g3u">小店名称升序</form:option>
            <form:option value="g4u">买家名称升序</form:option>
            <form:option value="g5u">订单金额升序</form:option>
            <form:option value="g5d">订单金额降序</form:option>
            <form:option value="g7u">支付状态升序</form:option>
            <form:option value="g6u">订单状态升序</form:option>
            <form:option value="g8u">发货状态升序</form:option>
            </form:select>
        </div>
        <div style="margin-top: 10px;">
            <label>退货：</label>
            <form:select path="sqlMap['hasReturn']" cssStyle="width: 80px;">
                <form:option value="" label="全部"/>
                <form:option value="true" label="包含"/>
                <form:option value="false" label="不包含"/>
            </form:select>
            <label>下单时间：</label>
            <input id="ost" name="sqlMap['ost']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width: 130px;"
                   value="${order.sqlMap['ost']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
            <label>至&nbsp;&nbsp;</label>
            <input id="oet" name="sqlMap['oet']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width: 130px;"
                   value="${order.sqlMap['oet']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
            <label>发货时间：</label>
            <input id="sst" name="sqlMap['sst']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width: 130px;"
                   value="${order.sqlMap['sst']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
            <label>至&nbsp;&nbsp;</label>
            <input id="set" name="sqlMap['set']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width: 130px;"
                   value="${order.sqlMap['set']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
            <input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="reload()"/>
            <input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
            <input id="btnExportFarm" class="btn btn-primary hide" type="button" value="导出发货单"/>
            <input id="btnBatchConfirmOrder" class="btn btn-primary hide" type="button" value="确认订单" onclick="batchConfirmOrder()"/>
            <input id="btnConfirmAllOrder" class="btn btn-primary hide" type="button" value="全部确认" onclick="confirmAllOrder()"/>
            <input id="btnClear" class="btn btn-default" type="button" value="清空" onclick="clearForm()"/>
        </div>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable"></table>
    <jsp:include page="../express/dialog.jsp"></jsp:include>
</body>
</html>