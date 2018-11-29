<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品回收站</title>
	<meta name="decorator" content="default"/>
    <script type="text/javascript">include('product_spec','${ctxStatic}/product/',['spec.js']);</script>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		function clearForm(){
			$('#categoryId').val("");
			$('#categoryName').val("");
			$('#name').val("");
			$(':radio:checked').attr("checked",false);
		}
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="product" action="${ctx}/ec/product/list?type=${type}&archived=${product.archived}&mode=${mode}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>分类：</label>
			<sys:treeselect id="category" name="category.id" value="${product.category.id}" labelName="category.name" labelValue="${product.category.name}"
				title="栏目" url="/ec/category/treeData" module="product" notAllowSelectRoot="false" cssClass="input-small"/>
		<label>名称：</label>
			<form:input path="name" htmlEscape="false" maxlength="50" class="input-small"/>&nbsp;
		<label>推荐类型：</label>
            <form:radiobuttons path="sqlMap['recommendType']" items="${fns:getDictList('ec_recommend_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		<input id="btnClear" class="btn btn-default" type="button" value="清空" onclick="clearForm()"/>
	</form:form>
	<sys:message content="${message}"/>
    <form:form id="orderForm">
	<table id="contentTable" class="table table-bordered table-condensed table-hover">
		<thead>
			<tr>
				<th style="width: 80px;">分类</th>
                <th style="width: 120px;">品牌</th>
				<th style="max-width: 200px;">名称</th>
				<th style="width: 100px;">售价</th>
				<th style="width: 100px;">佣金</th>
				<th style="width: 50px;">排序</th>
				<th style="width: 30px;">热销</th>
				<th style="width: 30px;">下架</th>
				<th style="width: 30px;">操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="product">
			<tr>
				<td><a href="javascript:" onclick="$('#categoryId').val('${product.category.id}');$('#categoryName').val('${product.category.name}');$('#searchForm').submit();return false;" title="${product.category.name}">${fns:abbr(product.category.name,12)}</a></td>
                <td><span title="${product.brand.name}">${fns:abbr(product.brand.name,20)}</span></td>
				<td><span title="${product.name}">${fns:abbr(product.name,100)}</span></td>
				<td>${product.price}</td>
				<td>${product.saleEarning}</td>
				<td>${product.displayOrder}</td>
				<td style="color: ${product.isHot eq 0 ? 'cornflowerblue':'green'};">${product.isHot eq 0 ? '否':'是'}</td>
				<td style="color: ${product.archived eq 0 ? 'green':'red'};">${product.archived eq 0 ? '否':'是'}</td>
				<td>
                    <shiro:hasPermission name="ec:product:recover">
                        <c:if test="${product.delFlag eq '1'}">
                            <a href="${ctx}/ec/product/recover?id=${product.id}&name=${product.name}" title="恢复${product.name}" onclick="return confirmx('确认要恢复 ${product.name} 吗？', this.href)">恢复</a>
                        </c:if>
                    </shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
    </form:form>
	<div class="pagination">${page}</div>
</body>
</html>