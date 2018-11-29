<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>待发货订单列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        var selectedRowIds = "";
        $(function (){
            $("#btnExport").click(function(){
                top.$.jBox.confirm("确认导出？","系统提示",function(v){
                        $("#searchForm").attr("action",ctx + "/ec/order/exportShipList");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action",ctx + "/ec/order/shipList");
                });
            });
            $("#btnImport").click(function(){
                $.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true},
                    bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
            });
            $("#btnExportSelected").click(function(){
                if(isNull(selectedRowIds)){
                    alertx("请勾选要导出的数据");
                    return false;
                }
                top.$.jBox.confirm("确认导出已选数据？","系统提示",function(v){
                    $("#orderIds").val(selectedRowIds);
                    $("#searchForm").attr("action",ctx + "/ec/order/exportShipList");
                    $("#searchForm").submit();
                    $("#orderIds").val("");
                    $("#searchForm").attr("action",ctx + "/ec/order/shipList");
                });
            });
            $("#btnSubmit").click(function(){
                $('#shipTable').datagrid('loadData', { total: 0, rows: []});
                $('#shipTable').datagrid('reload',ctx + '/ec/order/getShipData?'+$('#searchForm').serialize());
            });
            $('#shipTable').datagrid(getDatagridOption({
                frozenColumns:[[            // 锁定列
                    {field:'orderIds',checkbox:true},
                    {field:'orderNumber',title:'订单号',width:140,formatter: function(value,row){
                        return "<a href='javascript:void(0)' onclick='stopBubble();showViewPage(\"" + ctx + "/ec/order/form?isView=true&banBack=true&id=" + row.orderId + "\",\"[" + value + "]订单详情\")' title='" + value + "'>" + value + "</a>";
                    }},
                    {field:'hasReturn',title:'包含退货',width:60,styler: function(value,row){return 'color:' + (value.indexOf('不') >= 0 ? 'green' : 'red') + ';';}},
                ]],
                columns:[[
                    {field:'farmName',title:'庄园名称',width:100},
                    {field:'productName',title:'商品名称',width:300,formatter: function(value,row){
                        return "<span title='" + value + "'>" + value + "</span>";
                    }},
                    {field:'spec',title:'规格名称',width:140},
                    {field:'quantity',title:'数量',width:60},
                    {field:'distributionPhone',title:'寄件电话',width:100},
                    {field:'distributionProvince',title:'寄件省份',width:60},
                    {field:'distributionCity',title:'寄件城市',width:60},
                    {field:'distributionDistrict',title:'寄件区县',width:60},
                    {field:'distributionAddress',title:'寄件详细地址',width:300},
                    {field:'deliveryName',title:'收件人',width:80},
                    {field:'deliveryPhone',title:'收件电话',width:100},
                    {field:'deliveryProvince',title:'收件省份',width:60},
                    {field:'deliveryCity',title:'收件城市',width:60},
                    {field:'deliveryDistrict',title:'收件区县',width:60},
                    {field:'detailAddress',title:'收件详细地址',width:300}
                ]],
                onSelect:function(index,row){
                    selectedRowIds += "|" + row.orderId;
                    console.log(selectedRowIds);
                },
                onUnselect:function(index,row){
                    selectedRowIds = selectedRowIds.replace("|" + row.orderId,"");
                    console.log(selectedRowIds);
                },
                onSelectAll:function(rows){
                    for(var i = 0;i < rows.length; i++){
                        selectedRowIds += "|" + rows[i].orderId;
                    }
                    console.log(selectedRowIds);
                },
                onUnselectAll:function(rows){
                    selectedRowIds = "";
                    console.log(selectedRowIds);
                }
            }));
            $('#shipTable').datagrid('reload',ctx + '/ec/order/getShipData?'+$('#searchForm').serialize());
        });
        function inputDistributionName(select){
            $('#distributionName').val(select[select.selectedIndex].innerHTML);
        }
	</script>
</head>
<body>
<form:form id="searchForm" modelAttribute="shipOrder" action="${ctx}/ec/order/shipList" method="post" class="form-search">
	<div class="panel panel-info">
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				查询条件
			</div>
		</div>
		<div class="panel-body">
            <input id="distributionName" name="distributionName" type="hidden" value="${shipOrder.distributionName}"/>
            <label>商品：</label>
            <sys:productSelect id="productIds" name="sqlMap['productIds']" value="${shipOrder.sqlMap.productIds}" radio="false"></sys:productSelect>
            <br>
            <div style="margin-top: 10px;">
                <form:hidden id="orderIds" path="sqlMap['orderIds']"></form:hidden>
                <label>退货：</label>
                <form:select path="sqlMap['hasReturn']" cssStyle="width: 80px;">
                    <form:option value="" label="全部"/>
                    <form:option value="true" label="包含"/>
                    <form:option value="false" label="不包含"/>
                </form:select>
                <label>配送方：</label>
                <form:select path="sqlMap['distributionId']" class="input-medium" onchange="inputDistributionName(this)">
                    <form:option value="" label="全部"/>
                    <form:options items="${distributionList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
                </form:select>
                <label>下单时间：</label>
                <input id="st" type="text" name="sqlMap['st']" value="${shipOrder.sqlMap['st']}" readonly="readonly" class="input-medium Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
                <span>&nbsp;至&nbsp;</span>
                <input id="et" type="text" name="sqlMap['et']" value="${shipOrder.sqlMap['et']}" readonly="readonly" class="input-medium Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
                <label>订单号：</label>
                <input id="orderNumber" type="text" name="orderNumber" value="${shipOrder.orderNumber}" class="input-medium"/>
                &nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
                &nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
                &nbsp;<input id="btnExportSelected" class="btn btn-primary" type="button" value="导出已选"/>
                <shiro:hasPermission name="ec:order:importInvoices">
                &nbsp;<input id="btnImport" class="btn btn-primary" type="button" value="导入发货单"/>
                </shiro:hasPermission>
            </div>
		</div>
	</div>
</form:form>
<sys:message content="${message}"/>
<table id="shipTable"></table>
<div id="importBox" class="hide">
    <form id="importForm" action="${ctx}/ec/order/importInvoices" method="post" enctype="multipart/form-data"
          class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在导入，请稍等...');"><br/>
        <input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
        <input id="btnImportSubmit" class="btn btn-primary" type="submit" value="　　导　　入　　"/>
        <a href="${ctx}/ec/order/importInvoices/template">下载模板</a>
    </form>
</div>
</body>
</html>