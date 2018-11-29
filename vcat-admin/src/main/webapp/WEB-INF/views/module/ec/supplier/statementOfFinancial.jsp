<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>财务对账单</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function(){
            function getTitleSpan(value){
                var title = value.replace(/[|][ ]/g,'\r\n');
                return '<span title="' + title + '">' + value + '</span>'
            }
            $('.statementTable').datagrid(getDatagridOption({
                showFooter: true,
                frozenColumns:[[            // 锁定列
                    {field:'orderId',checkbox:true},
                    {field:'paymentNo',title:'商户订单号'},
                    {field:'orderNumber',title:'订单号',
                        formatter: function(value,row){
                            return '<a href="javascript:void(0)" onclick="stopBubble();showDetail(\''+row.orderId+'\');">'+row.orderNumber+'</a>';
                        }
                    },
                ]],
                columns:[[                  // 常规显示列
                    {field:'supplierName',title:'供应商名称',width:200,formatter: function(value,row){return getTitleSpan(value)}},
                    {field:'brandName',title:'品牌名称',width:200,formatter: function(value,row){return getTitleSpan(value)}},
                    {field:'productName',title:'商品名称',width:300,formatter: function(value,row){return getTitleSpan(value)}},
                    {field:'productQuantity',title:'购买数量',width:60,formatter: function(value,row){return getTitleSpan(value)}},
                    {field:'orderType',title:'订单类型',width:80,formatter: function(value,row){return getTitleSpan(value)}},
                    {field:'sender',title:'发货人',width:80},
                    {field:'paymentTime',title:'支付时间',width:120},
                    {field:'paymentType',title:'支付方式',width:80},
                    {field:'paymentAmount',title:'支付金额',width:80},
                    {field:'freightPrice',title:'快递费',width:80},
                    {field:'saleEarning',title:'销售佣金',width:80},
                    {field:'bonusEarning',title:'销售分红',width:80},
                    {field:'firstBonusEarning',title:'一级团队分红',width:80},
                    {field:'secondBonusEarning',title:'二级团队分红',width:80},
                    {field:'supplierBalance',title:'供应商结算费',width:80},
                    {field:'terraceBalance',title:'平台结算费',width:80},
                    {field:'refundAmount',title:'退款金额',width:80},
                    {field:'refundOperDate',title:'退款执行时间',width:120},
                    {field:'accountingStatus',title:'记账标识',width:60,
                        styler: function(value,row){return 'color:' + row.accountingStatusColor + ';';}
                    },
                    {field:'reconciliationFreightStatus',title:'邮费结算状态',width:80,
                        styler: function(value,row){return 'color:' + row.reconciliationFreightStatusColor + ';'}
                    },
                    {field:'reconciliationStatus',title:'供应商结算状态',width:90,
                        styler: function(value,row){return 'color:' + row.reconciliationStatusColor + ';'}
                    },
                    {field:'status',title:'操作',width:60,
                        formatter: function(value,row){
                            var html = "";
                            if (row.accountingStatus.indexOf('未') >= 0){
                                html += '<a href="javascript:void(0)" onclick="stopBubble();showAccounting(\''+row.orderId+'\',\''+row.orderNumber+'\')">记账</a>';
                            }
                            if (row.reconciliationStatus != '\\' && (row.reconciliationStatus.indexOf('已') < 0 || row.reconciliationFreightStatus.indexOf('未') >= 0)){
                                html += ' <a href="javascript:void(0)" onclick="stopBubble();showReconciliation(\''+row.orderId+'\',\''+row.orderNumber+'\',true)">结算</a> ';
                            }
                            if (row.reconciliationStatus.indexOf('已') >= 0 && row.reconciliationFreightStatus.indexOf('已') >= 0){
                                html += ' <a href="javascript:void(0)" onclick="stopBubble();showReconciliation(\''+row.orderId+'\',\''+row.orderNumber+'\')">查看</a>';
                            }
                            return html;
                        }
                    }
                ]]
            }));
        });
        function exportTable(){
            top.$.jBox.confirm("确认导出？","系统提示",function(v){
                if(v=="ok"){
                    $("#searchForm").attr("action",ctx+"/ec/supplier/exportStatementOfFinancial");
                    $("#searchForm").submit();
                    $("#searchForm").attr("action",ctx+"/ec/supplier/statementOfFinancial");
                }
            });
        }
        function showDetail(id){
            top.$.jBox.open("iframe:${ctx}/ec/order/form?id="+id, "订单详情",$(top.document).width()-220,$(top.document).height()-180,{
                buttons: {"确定": true},
                loaded: function () {
                    $(".jbox-content", top.document).css("overflow-y", "hidden");
                }
            });
        }
        function accountingByQueryConditions(){
            confirmx("确认按照当前查询条件<span style='color:red'>记账订单</span>？",function(){
                $.getJSON(ctx + "/ec/supplier/accountingByQueryConditions?" + $("#searchForm").serialize(), {}, function (count) {
                    alertx("记账当前查询条件订单成功，共结算["+count+"]个订单");
                    reloadForm();
                });
            });
        }
        function accountingOrder(orderId,orderNumber){
            confirmx("确认记账订单[" + orderNumber + "]？",function() {
                $.getJSON(ctx + "/ec/supplier/accountingOrder", {
                    "order.id": orderId,
                    "order.orderNumber": orderNumber
                }, function () {
                    alertx("记账订单[" + orderNumber + "]成功");
                    reloadForm();
                });
            });
        }
        function batchAccountingOrder(){
            var orderIdArray = getCheckIdArrayParamString('.statementTable','orderId','orderIdArray');
            confirmx("确认批量记账订单？",function() {
                $.getJSON(ctx + "/ec/supplier/batchAccountingOrder?"+orderIdArray, function (count) {
                    alertx("批量记账订单成功，共记账["+count+"]个订单");
                    reloadForm();
                });
            });
        }
        function showAccounting(orderId,orderNumber){
            $('#accountingOrderId').val(orderId);
            $('#accountingOrderNumber').val(orderNumber);
            $('#accountingOrderNumberSpan').html(orderNumber);

            updateAccountingTable(orderId);

            showDialog('accountingDialog');
        }
        function updateAccountingTable(orderId){
            $.getJSON(ctx+"/ec/order/getOrder",{"id":orderId},function(order){
                updateOrderDetailTable(order);
                var list = order.itemList;
                if(list && list.length > 0){
                    var html = "";
                    $('.accountingRow').remove();
                    for(var i = 0;i < list.length;i++){
                        var orderItem = list[i];
                        var tr = "<tr class='accountingRow'>";

                        tr += getItemTDHTML(orderItem);

                        tr += "</tr>";
                        html += tr;
                    }

                    $('.accountingTable').append(html);
                }
            });
        }
        function reconciliationByQueryConditions(){
            confirmx("确认按照当前查询条件<span style='color:red'>结算订单</span>？",function(){
                $.getJSON(ctx + "/ec/supplier/reconciliationByQueryConditions?" + $("#searchForm").serialize(), {}, function (count) {
                    alertx("结算当前查询条件订单成功，共结算["+count+"]个订单");
                    reloadForm();
                });
            });
        }
        function reconciliationOrder(orderId,orderNumber){
            confirmx("确认结算订单[" + orderNumber + "]？",function() {
                $.getJSON(ctx + "/ec/supplier/reconciliationOrder", {
                    "order.id": orderId,
                    "order.orderNumber": orderNumber
                }, function () {
                    alertx("结算订单[" + orderNumber + "]成功");
                    reloadForm();
                });
            });
        }
        function reconciliationOrderItem(orderId,orderItemId,name){
            confirmx("确认单独结算订单项[" + name + "]？",function() {
                $.getJSON(ctx + "/ec/supplier/reconciliationItem", {
                    "order.id": orderId,
                    "orderItem.id": orderItemId
                }, function () {
                    updateReconciliationDetailTable(orderId);
                    alertx("单独结算订单项[" + name + "]成功");
                });
            });
        }
        function batchReconciliationOrder(){
            var orderIdArray = getCheckIdArrayParamString('.statementTable','orderId','orderIdArray');
            confirmx("确认批量结算订单？",function() {
                $.getJSON(ctx + "/ec/supplier/batchReconciliationOrder?"+orderIdArray, function (count) {
                    alertx("批量结算订单成功，共结算["+count+"]个订单");
                    reloadForm();
                });
            });
        }

        function batchReconciliationFreight(){
            var orderIdArray = getCheckIdArrayParamString('.statementTable','orderId','orderIdArray');
            confirmx("确认批量结算订单邮费？",function() {
                $.getJSON(ctx + "/ec/supplier/batchReconciliationFreight?"+orderIdArray, function (count) {
                    alertx("批量结算订单成功，共结算["+count+"]个订单邮费");
                    reloadForm();
                });
            });
        }
        function reconciliationFreightByQueryConditions(){
            confirmx("确认按照当前查询条件<span style='color:red'>结算订单邮费</span>？",function(){
                $.getJSON(ctx + "/ec/supplier/reconciliationFreightByQueryConditions?" + $("#searchForm").serialize(), {}, function (count) {
                    alertx("结算当前查询条件订单成功，共结算["+count+"]个订单邮费");
                    reloadForm();
                });
            });
        }
        function showReconciliation(orderId,orderNumber,showBtn){
            if(showBtn){
                $('.reconciliationBtnDiv').show();
                $('.reconciliationOperating').show();
                $('#reconciliationOrderId').val(orderId);
                $('#reconciliationOrderNumber').val(orderNumber);
            }else{
                $('.reconciliationBtnDiv').hide();
                $('.reconciliationOperating').hide();
                $('#reconciliationOrderId').val('');
                $('#reconciliationOrderNumber').val('');
            }
            $('#reconciliationDetailOrderNumber').html(orderNumber);

            updateReconciliationDetailTable(orderId,showBtn);

            showDialog('reconciliationDetailDialog');
        }
        function updateReconciliationDetailTable(orderId,showBtn){
            $.getJSON(ctx+"/ec/order/getOrder",{"id":orderId},function(order){
                updateOrderDetailTable(order);
                var list = order.itemList;
//                if(order.reconciliationStatusLabel.indexOf('已') > -1){
//                    $('.reconciliationOperating').hide();
//                }
                if(list && list.length > 0){
                    var html = "";
                    $('.detailRow').remove();
                    for(var i = 0;i < list.length;i++){
                        var item = list[i];
                        var productItemName = item.productItem.name;
                        var tr = "<tr class='detailRow'>";

                        tr += getItemTDHTML(item);

                        if(item.reconciliation){
                            tr += '<td style="color:green">已结算</td>';
                            tr += '<td title="' + item.reconciliation.operator.name + '">' + abbr(item.reconciliation.operator.name) + '</td>';
                            if(showBtn){
                                tr += '<td></td>';
                            }
                        }else{
                            tr += '<td style="color:red">未结算</td>';
                            tr += '<td title="暂无">暂无</td>';
                            if(showBtn){
                                tr += '<td><a href="javascript:void(0)" onclick="reconciliationOrderItem(\''+orderId+'\',\''+item.id+'\',\''+productItemName+'\')">单独结算</a></td>';
                            }
                        }

                        tr += "</tr>";
                        html += tr;
                    }

                    $('.reconciliationDetailTable').append(html);
                }
            });
        }
        function getItemTDHTML(orderItem){
            var html = "";
            if(orderItem){
                var productName = orderItem.productItem.product.name;
                var productItemName = orderItem.productItem.name;
                html += '<td>' + orderItem.orderTypeLabel + '</td>';
                html += '<td title="' + productName + '">' + abbr(productName,24) + '</td>';
                html += '<td title="' + productItemName + '">' + abbr(productItemName,20) + '</td>';
                html += '<td>' + orderItem.quantity + '</td>';
                html += '<td>' + orderItem.promotionQuantity + '</td>';
                html += '<td>' + (orderItem.itemPrice * orderItem.quantity).toRound(2) + '</td>';
                html += '<td>' + (orderItem.saleEarning * orderItem.quantity).toRound(2) + '</td>';
                html += '<td>' + (orderItem.bonusEarning * orderItem.quantity).toRound(2) + '</td>';
                html += '<td>' + (orderItem.firstBonusEarning * orderItem.quantity).toRound(2) + '</td>';
                html += '<td>' + (orderItem.secondBonusEarning * orderItem.quantity).toRound(2) + '</td>';
                html += '<td>' + (orderItem.purchasePrice * orderItem.quantity).toRound(2) + '</td>';
                html += '<td>' + (orderItem.point * orderItem.quantity).toRound(2) + '</td>';
            }
            return html;
        }

        function updateOrderDetailTable(order){
            $('.orderDetail').html("");
            $('.orderNumber').html(order.orderNumber);
            $('.paymentStatus').html(order.paymentStatusLabel);
            $('.paymentStatus').css("color",order.paymentStatusColor);
            $('.orderStatus').html(order.orderStatusLabel);
            $('.orderStatus').css("color",order.orderStatusColor);
            $('.shippingStatus').html(order.shippingStatusLabel);
            $('.shippingStatus').css("color",order.shippingStatusColor);
            $('.reconciliationFreightStatus').html(order.reconciliationFreightStatusLabel);
            $('.reconciliationFreightStatus').css("color",order.reconciliationFreightStatusColor);
            $('.reconciliationStatus').html(order.reconciliationStatusLabel);
            $('.reconciliationStatus').css("color",order.reconciliationStatusColor);
            $('.freightPrice').html(order.freightPrice);
            $('.totalCoupon').html(order.totalCoupon);
            $('.totalPrice').html(order.totalPrice);
            $('.addDate').html(order.addDate);
            var couldReconciliationFreight = order.reconciliationFreightStatusLabel && order.reconciliationFreightStatusLabel.indexOf('未') > -1;
            var couldReconciliation = order.reconciliationStatusLabel && order.reconciliationStatusLabel.indexOf('已') < 0;
            if(couldReconciliationFreight){
                $('.reconciliationFreightBtn').show();
            }else{
                $('.reconciliationFreightBtn').hide();
            }
            if(couldReconciliation){
                $('.reconciliationOrderBtn').show();
            }else{
                $('.reconciliationOrderBtn').hide();
            }
            if(couldReconciliationFreight && couldReconciliation){
                $('.reconciliationFreightAndOrderBtn').show();
            }else{
                $('.reconciliationFreightAndOrderBtn').hide();
            }
        }
        function showDetail(id){
            top.$.jBox.open("iframe:${ctx}/ec/order/form?isView=true&id="+id, "订单详情",$(top.document).width()-220,$(top.document).height()-180,{
                buttons: {"确定": true},
                loaded: function () {
                    $(".jbox-content", top.document).css("overflow-y", "hidden");
                }
            });
        }
        function reloadForm(){
            hideLayer();
            $('.vcat-dialog').hide('slow');
            $('.statementTable').datagrid('loadData', { total: 0, rows: [] ,footer:[]});
            $('.statementTable').datagrid('reload','statementOfFinancialData?'+$('#searchForm').serialize());
        }
        function reconciliationFreight(orderId,orderNumber){
            confirmx("确认结算订单[" + orderNumber + "]邮费？",function() {
                $.getJSON(ctx + "/ec/supplier/reconciliationFreight", {
                    "order.id": orderId,
                    "order.orderNumber": orderNumber
                }, function () {
                    alertx("结算订单[" + orderNumber + "]邮费成功");
                    reloadForm();
                });
            });
        }
        function reconciliationFreightAndOrder(orderId,orderNumber){
            confirmx("确认结算订单[" + orderNumber + "]邮费及供应商费用？",function() {
                $.getJSON(ctx + "/ec/supplier/reconciliationFreightAndOrder", {
                    "id": orderId,
                    "orderNumber": orderNumber
                }, function () {
                    alertx("结算订单[" + orderNumber + "]邮费及供应商费用成功");
                    reloadForm();
                });
            });
        }
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="supplier" action="${ctx}/ec/supplier/statementOfFinancial" method="post" class="form-search">
		<div class="panel panel-info">
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					查询条件
				</div>
			</div>
			<div class="panel-body">
				<label>供应商：</label>
				<form:select path="id" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${supplierList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
				</form:select>
                <label>品牌：</label>
                <form:select path="sqlMap['brandId']" class="input-small">
                    <form:option value="" label="全部"/>
                    <form:options items="${brandList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
                </form:select>
				<label>订单类型：</label>
				<form:select id="orderType" path="sqlMap['orderType']" cssStyle="width: 80px;">
					<form:option value="" label="全部"/>
                    <c:forEach items="${fns:getDictList('ec_order_type')}" var="orderType">
                        <form:option value="${orderType.value}" label="${orderType.label}"/>
                    </c:forEach>
				</form:select>
                <label>订单状态：</label>
                <form:select id="orderStatus" path="sqlMap['orderStatus']" cssStyle="width: 80px;">
                    <form:option value="paid" label="已支付"/>
                    <form:option value="succ" label="已完成"/>
                </form:select>
                <label>支付方式：</label>
                <form:select id="gateway" path="sqlMap['gateway']" cssStyle="width: 80px;">
                    <form:option value="" label="全部"/>
                    <c:forEach items="${gatewayList}" var="gateway">
                        <form:option value="${gateway.id}" label="${gateway.name}"/>
                    </c:forEach>
                </form:select>
                <div style="margin-top: 10px;">
                    <label>支付日期：</label>
                    <input id="pst" type="text" name="sqlMap['pst']" value="${supplier.sqlMap['pst']}" readonly="readonly" maxlength="10" class="Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" style="width:100px;"/>
                    <span>&nbsp;至&nbsp;</span>
                    <input id="pet" type="text" name="sqlMap['pet']" value="${supplier.sqlMap['pet']}" readonly="readonly" maxlength="10" class="Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" style="width:100px;"/>
                    <label>完成日期：</label>
                    <input id="sst" type="text" name="sqlMap['sst']" value="${supplier.sqlMap['sst']}" readonly="readonly" maxlength="10" class="Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" style="width:100px;"/>
                    <span>&nbsp;至&nbsp;</span>
                    <input id="set" type="text" name="sqlMap['set']" value="${supplier.sqlMap['set']}" readonly="readonly" maxlength="10" class="Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" style="width:100px;"/>
                    <label>退款执行时间：</label>
                    <input id="rst" type="text" name="sqlMap['rst']" value="${supplier.sqlMap['rst']}" readonly="readonly" maxlength="10" class="Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'});" style="width:150px;"/>
                    <span>&nbsp;至&nbsp;</span>
                    <input id="ret" type="text" name="sqlMap['ret']" value="${supplier.sqlMap['ret']}" readonly="readonly" maxlength="10" class="Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'});" style="width:150px;"/>
                </div>
                <div style="margin-top: 10px;">
                    <label>邮费结算状态：</label>
                    <form:select id="reconciliationFreightStatus" path="sqlMap['reconciliationFreightStatus']" cssStyle="width: 100px;">
                        <form:option value="" label="全部"/>
                        <form:option value="false" label="未结算"/>
                        <form:option value="true" label="已结算"/>
                    </form:select>
                    <label>供应商结算状态：</label>
                    <form:select id="reconciliationStatus" path="sqlMap['reconciliationStatus']" cssStyle="width: 100px;">
                        <form:option value="" label="全部"/>
                        <c:forEach items="${fns:getDictList('ec_reconciliation_status')}" var="reconciliationStatus">
                            <form:option value="${reconciliationStatus.value}" label="${reconciliationStatus.label}"/>
                        </c:forEach>
                    </form:select>
                    <label>关键字：</label>
                    <input id=keyword type="text" name="sqlMap['keyword']" value="${supplier.sqlMap['keyword']}" maxlength="20" class="input-medium"/>
                </div>
                <div style="float:right;margin-top: 10px;">
                    <input class="btn btn-primary" type="button" value="查询" onclick="reloadForm()"/>
                    <shiro:hasPermission name="ec:finance:supplier:statementOfFinancial">
                    &nbsp;<input class="btn btn-primary" type="button" value="导出" onclick="exportTable()"/>
                    &nbsp;<input class="btn btn-primary" type="button" value="批量记账" onclick="batchAccountingOrder()"/>
                    &nbsp;<input class="btn btn-primary" type="button" value="批量结算邮费" onclick="batchReconciliationFreight()"/>
                    &nbsp;<input class="btn btn-primary" type="button" value="批量结算供应商" onclick="batchReconciliationOrder()"/>
                    &nbsp;<input class="btn btn-primary" type="button" value="全部记账" onclick="accountingByQueryConditions()"/>
                    &nbsp;<input class="btn btn-primary" type="button" value="全部结算邮费" onclick="reconciliationFreightByQueryConditions()"/>
                    &nbsp;<input class="btn btn-primary" type="button" value="全部结算供应商" onclick="reconciliationByQueryConditions()"/>
                    </shiro:hasPermission>
                </div>
			</div>
		</div>
	</form:form>
	<sys:message content="${message}"/>
    <table class="statementTable"></table>

    <div id="reconciliationDetailDialog" class="vcat-dialog" style="width: 90%;">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title">
                    订单[<span id="reconciliationDetailOrderNumber"></span>]结算单
                    <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('reconciliationDetailDialog')"></a>
                </div>
            </div>
            <div class="panel-body">
                <table class="table table-bordered table-hover orderDetailTable">
                    <tr>
                        <th>订单号</th>
                        <th>支付状态</th>
                        <th>订单状态</th>
                        <th>发货状态</th>
                        <th>邮费结算状态</th>
                        <th>供应商结算状态</th>
                        <th>订单邮费</th>
                        <th>使用V猫币</th>
                        <th>订单总金额</th>
                        <th>下单时间</th>
                    </tr>
                    <tr>
                        <td class="orderDetail orderNumber"></td>
                        <td class="orderDetail paymentStatus"></td>
                        <td class="orderDetail orderStatus"></td>
                        <td class="orderDetail shippingStatus"></td>
                        <td class="orderDetail reconciliationFreightStatus"></td>
                        <td class="orderDetail reconciliationStatus"></td>
                        <td class="orderDetail freightPrice"></td>
                        <td class="orderDetail totalCoupon"></td>
                        <td class="orderDetail totalPrice"></td>
                        <td class="orderDetail addDate"></td>
                    </tr>
                </table>
                <table class="table table-bordered table-hover reconciliationDetailTable">
                    <tr>
                        <th width="60px">类型</th>
                        <th>商品名称</th>
                        <th width="120px">规格名称</th>
                        <th width="40px">购买数量</th>
                        <th width="40px">赠送数量</th>
                        <th width="60px">销售价</th>
                        <th width="60px">销售佣金</th>
                        <th width="60px">销售分红</th>
                        <th width="60px">一级团队分红</th>
                        <th width="60px">二级团队分红</th>
                        <th width="60px">供应商结算费</th>
                        <th width="60px">平台结算费</th>
                        <th width="60px">结算状态</th>
                        <th width="70px">结算人</th>
                        <th width="60px" class="reconciliationOperating">操作</th>
                    </tr>
                </table>
                <shiro:hasPermission name="ec:finance:supplier:statementOfFinancial">
                    <div class="form-actions reconciliationBtnDiv hide" style="margin-top: 0px;margin-bottom: 0px;padding: 5px 15px 5px;">
                        <input id="reconciliationOrderId" type="hidden">
                        <input id="reconciliationOrderNumber" type="hidden">
                        <input onclick="reconciliationFreight($('#reconciliationOrderId').val(),$('#reconciliationOrderNumber').val())" class="btn btn-success reconciliationFreightBtn" type="button" value="邮费结算"/>
                        <input onclick="reconciliationOrder($('#reconciliationOrderId').val(),$('#reconciliationOrderNumber').val())" class="btn btn-success reconciliationOrderBtn" type="button" value="供应商结算"/>
                        <input onclick="reconciliationFreightAndOrder($('#reconciliationOrderId').val(),$('#reconciliationOrderNumber').val())" class="btn btn-success reconciliationFreightAndOrderBtn" type="button" value="全部结算"/>
                    </div>
                </shiro:hasPermission>
            </div>
        </div>
    </div>

    <div id="accountingDialog" class="vcat-dialog" style="width: 90%;">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title">
                    订单[<span id="accountingOrderNumberSpan"></span>]记账单
                    <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('accountingDialog')"></a>
                </div>
            </div>
            <div class="panel-body">
                <table class="table table-bordered table-hover orderDetailTable">
                    <tr>
                        <th>订单号</th>
                        <th>支付状态</th>
                        <th>订单状态</th>
                        <th>发货状态</th>
                        <th>邮费结算状态</th>
                        <th>供应商结算状态</th>
                        <th>订单邮费</th>
                        <th>使用V猫币</th>
                        <th>订单总金额</th>
                        <th>下单时间</th>
                    </tr>
                    <tr>
                        <td class="orderDetail orderNumber"></td>
                        <td class="orderDetail paymentStatus"></td>
                        <td class="orderDetail orderStatus"></td>
                        <td class="orderDetail shippingStatus"></td>
                        <td class="orderDetail reconciliationFreightStatus"></td>
                        <td class="orderDetail reconciliationStatus"></td>
                        <td class="orderDetail shippingStatus"></td>
                        <td class="orderDetail freightPrice"></td>
                        <td class="orderDetail totalCoupon"></td>
                        <td class="orderDetail totalPrice"></td>
                        <td class="orderDetail addDate"></td>
                    </tr>
                </table>
                <table class="table table-bordered table-hover accountingTable">
                    <tr>
                        <th width="60px">类型</th>
                        <th>商品名称</th>
                        <th width="120px">规格名称</th>
                        <th width="40px">购买数量</th>
                        <th width="40px">赠送数量</th>
                        <th width="60px">销售价</th>
                        <th width="60px">销售佣金</th>
                        <th width="60px">销售分红</th>
                        <th width="60px">一级团队分红</th>
                        <th width="60px">二级团队分红</th>
                        <th width="60px">供应商结算费</th>
                        <th width="60px">平台结算费</th>
                    </tr>
                </table>
                <shiro:hasPermission name="ec:finance:supplier:statementOfFinancial">
                    <div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;padding: 5px 15px 5px;">
                        <input id="accountingOrderId" type="hidden">
                        <input id="accountingOrderNumber" type="hidden">
                        <input onclick="accountingOrder($('#accountingOrderId').val(),$('#accountingOrderNumber').val())" class="btn btn-success" type="button" value="记账该订单"/>
                    </div>
                </shiro:hasPermission>
            </div>
        </div>
    </div>
</body>
</html>