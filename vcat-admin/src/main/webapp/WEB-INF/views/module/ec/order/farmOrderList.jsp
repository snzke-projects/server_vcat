<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>水果订单列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
            $("#btnExport").click(function(){
                top.$.jBox.confirm("确认导出？","系统提示",function(v){
                    $("#searchForm").attr("action",ctx + "/ec/order/exportFarmOrderList");
                    $("#searchForm").submit();
                    $("#searchForm").attr("action",ctx + "/ec/order/farmOrderList");
                });
            });
            $("#btnSubmit").click(function(){
                $('#farmOrderTable').datagrid('loadData', { total: 0, rows: []});
                $('#farmOrderTable').datagrid('reload',ctx + '/ec/order/farmOrderListData?'+$('#searchForm').serialize());
            });
            $('#farmOrderTable').datagrid(getDatagridOption({
                frozenColumns:[[
                    {field:'shopName',title:'店铺名称',width:100},
                    {field:'farmName',title:'发件人',width:100},
                    {field:'orderNumber',title:'订单号',width:140,formatter: function(value,row){
                        return "<a href='javascript:void(0)' onclick='stopBubble();showViewPage(\"" + ctx + "/ec/order/form?isView=true&banBack=true&orderNumber=" + value + "\",\"[" + value + "]订单详情\")' title='" + value + "'>" + value + "</a>";
                    }},
                ]],
                columns:[[
                    {field:'productName',title:'商品名称',width:300,formatter: function(value,row){
                        return "<span title='" + value + "'>" + value + "</span>";
                    }},
                    {field:'spec',title:'规格名称',width:100},
                    {field:'paymentTime',title:'支付时间',width:140},
                    {field:'deliveryName',title:'收件人姓名',width:80},
                    {field:'deliveryPhone',title:'收件电话',width:100},
                    {field:'detailAddress',title:'收件地址',width:300},
                    {field:'quantity',title:'购买数量',width:60}
                ]]
            }));
        });
	</script>
</head>
<body>
<form:form id="searchForm" modelAttribute="farmOrder" action="${ctx}/ec/order/farmOrderList" method="post" class="form-search">
	<div class="panel panel-info">
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				查询条件
			</div>
		</div>
		<div class="panel-body">
            <label>选择商品：</label>
            <sys:productSelect id="productIds" name="productIds" radio="false" showClearButton="true"></sys:productSelect>
            <br>
            <div style="margin-top: 10px;">
                <label>支付时间：</label>
                <input id="pst" type="text" name="sqlMap['pst']" value="${farmOrder.sqlMap['pst']}" readonly="readonly" class="input-medium Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
                <span>&nbsp;至&nbsp;</span>
                <input id="pet" type="text" name="sqlMap['pet']" value="${farmOrder.sqlMap['pet']}" readonly="readonly" class="input-medium Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
                <label>订单号：</label>
                <input id="orderNumber" type="text" name="orderNumber" value="${farmOrder.orderNumber}" class="input-medium"/>
                &nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
                &nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
            </div>
		</div>
	</div>
</form:form>
<sys:message content="${message}"/>
<table id="farmOrderTable"></table>
</body>
</html>