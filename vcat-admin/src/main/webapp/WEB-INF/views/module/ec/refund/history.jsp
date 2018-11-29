<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>退款管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
            $('#contentTable').datagrid(getDatagridOption({
                singleSelect: true,
                url: ctx + "/ec/refund/returnHistoryData?orderItem.id=${refund.orderItem.id}",
                frozenColumns:[[            // 锁定列
                    {field:'orderNumber',title:'订单号',formatter:function(value,row){return row.orderItem.order.orderNumber;}}
                ]],
                columns:[[
                    {field:'shopName',title:'所属店铺名称',formatter:function(value,row){return row.shop.name;}},
                    {field:'supplierName',title:'所属供货商',formatter:function(value,row){return row.product.brand.supplier.name;}},
                    {field:'categoryName',title:'商品分类',formatter:function(value,row){return row.product.category.name;}},
                    {field:'productName',title:'商品名称',formatter:function(value,row){return row.product.name;}},
                    {field:'productItemName',title:'规格名称',formatter:function(value,row){return row.orderItem.productItem.name;}},
                    {field:'applyTime',title:'申请时间',formatter:function(value,row){return row.applyTime;}},
                    {field:'quantity',title:'退款数量'},
                    {field:'amount',title:'退款金额'},
                    {field:'buyerName',title:'买家账号',formatter:function(value,row){return row.customer.userName;}},
                    {field:'returnStatusLabel',title:'退货状态',styler:function(value,row){return "color:" + row.returnStatusColor;}},
                    {field:'refundStatusLabel',title:'退款状态',styler:function(value,row){return "color:" + row.refundStatusColor;}},
                    {field:'isActivate',title:'是否撤销',styler:function(value,row){return "color:" + (row.isActivate == 1 ? "green" : "red");},formatter:function(value,row){return row.isActivate == 1 ? "否" : "是";}},
                    {field:'oper',title:'操作',formatter:function(value,row){
                        return '<a href="javascript:void(0);" onclick="stopBubble();showRefund(\'' + row.id + '\')">查看</a>';
                    }}
                ]]
            }));
        });
		function showRefund(refundId){
			showRefundDetail(refundId);
			showDialog('refundDialog');
		}
	</script>
</head>
<body>
<sys:message content="${message}"/>
<table id="contentTable" style="width: 100%"></table>

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
                <jsp:include page="detailTr.jsp"></jsp:include>
			</table>
		</div>
	</div>
</div>
<jsp:include page="../express/dialog.jsp"></jsp:include>
</body>
</html>