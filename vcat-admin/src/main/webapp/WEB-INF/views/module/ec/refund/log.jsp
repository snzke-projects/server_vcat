<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>退款管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}
		function showRefund(refundId){
			showRefundDetail(refundId);
			showDialog('refundDialog');
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
<form:form id="searchForm" modelAttribute="refund" action="${ctx}/ec/customer/refundLog" method="post" class="form-search">
	<div class="panel panel-info">
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				查询条件
			</div>
		</div>
		<div class="panel-body">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<input id="pageSize" name="customer.id" type="hidden" value="${refund.customer.id}"/>
			<label>分类：</label>
			<sys:treeselect id="category" name="product.category.id" value="${refund.product.category.id}" labelName="product.category.name" labelValue="${refund.product.category.name}"
							title="栏目" url="/ec/category/treeData" module="product" notAllowSelectRoot="false" cssClass="input-small"/>
			<label>所属供货商：</label>
			<form:select id="supplier" path="product.brand.supplier.id" class="input-medium">
				<form:option value="" label=""/>
				<form:options items="${supplierList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
			</form:select>
			<label>退款状态：</label>
			<form:select id="refundStatus" path="refundStatus" class="input-small">
				<form:option value="" label=""/>
				<form:options items="${fns:getDictList('ec_refund_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</form:select>
			<label>关键字：</label>
			<form:input id="keyWord" path="sqlMap['keyWord']" htmlEscape="false" maxlength="50" class="input-large"/>
			<label>退款申请时间：</label>
			<input id="st" name="sqlMap['st']" type="text" readonly="readonly" maxlength="16" class="input-medium Wdate"
				   value="${refund.sqlMap['st']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
			<label>至</label>
			<input id="et" name="sqlMap['et']" type="text" readonly="readonly" maxlength="16" class="input-medium Wdate"
				   value="${refund.sqlMap['et']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			<input id="btnClear" class="btn btn-default" type="button" value="清空" onclick="clearForm()"/>
		</div>
	</div>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-bordered table-hover">
	<thead>
	<tr>
		<th width="8%">订单号</th>
		<th width="5%">商品分类</th>
		<th width="8%">所属供货商</th>
		<th width="6%">所属店铺名称</th>
		<th width="25%">商品名称</th>
		<th width="5%">规格名称</th>
		<th width="10%">申请时间</th>
		<th width="5%">退款金额</th>
		<th width="8%">买家账号</th>
		<th width="6%">退货状态</th>
		<th width="6%">退款状态</th>
		<th width="8%">操作</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${page.list}" var="refund">
		<tr>
			<td><a href="javascript:void(0)" onclick="$('#keyWord').val('${refund.orderItem.order.orderNumber}');$('#searchForm').submit();">${refund.orderItem.order.orderNumber}</a></td>
			<td><a href="javascript:void(0)" onclick="$('#categoryId').val('${refund.product.category.id}');$('#categoryName').val('${refund.product.category.name}');$('#searchForm').submit();">${refund.product.category.name}</a></td>
			<td><a href="javascript:void(0)" onclick="$('#supplier').val('${refund.product.brand.supplier.id}');$('#searchForm').submit();">${refund.product.brand.supplier.name}</a></td>
			<td>${refund.shop.customer.userName}</td>
			<td>${refund.product.name}</td>
			<td>${refund.orderItem.productItem.name}</td>
			<td><fmt:formatDate value="${refund.applyTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			<td>${refund.amount}</td>
			<td>${refund.customer.userName}</td>
			<td style="color: ${refund.returnStatusColor}">${refund.returnStatusLabel}</td>
			<td style="color: ${refund.refundStatusColor}">${refund.refundStatusLabel}</td>
			<td><a href="javascript:void(0);" onclick="showRefund('${refund.id}')">查看</a></td>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>

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