<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品上架预告列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}
		function checkActivate(id){
			confirmx("激活后商品将自动上架，且激活后不能删除该上架预告，确认激活？",function (){
				$.ajax({
					type : 'POST',
					url : ctx + "/ec/product/activateForecast",
					data : {id : id},
					success : function(){
						try{
							alertx("激活成功！",function(){
								location.href = location.href;
							});
						}catch(e){
							console.log("激活失败："+e.message);
							return doError(e);
						}
					},
					error: function(XMLHttpRequest) {
						return doError(XMLHttpRequest);
					}
				});
			});
		}
	</script>
</head>
<body>
<form:form id="searchForm" modelAttribute="productForecast" action="${ctx}/ec/product/forecastList" method="post" class="breadcrumb form-search">
	<div class="panel panel-info">
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				查询条件
			</div>
		</div>
		<div class="panel-body">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<label>商品分类：</label>
			<sys:treeselect id="category" name="product.category.id" value="${productForecast.product.category.id}" labelName="product.category.name" labelValue="${productForecast.product.category.name}"
							title="栏目" url="/ec/category/treeData" module="product" notAllowSelectRoot="false" cssClass="input-small"/>
			<label>关键字：</label>
			<form:input path="title" htmlEscape="false" maxlength="50" placeholder="标题、商品名称、分类名称"/>&nbsp;
			<label>商品上架时间：</label>
			<input id="st" name="sqlMap['st']" type="text" readonly="readonly" maxlength="16" class="input-medium Wdate"
				   value="${productForecast.sqlMap['st']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
			<label>至</label>
			<input id="et" name="sqlMap['et']" type="text" readonly="readonly" maxlength="16" class="input-medium Wdate"
				   value="${productForecast.sqlMap['et']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		</div>
	</div>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-bordered table-hover">
	<thead>
	<tr>
		<th width="10%">商品所属分类</th>
		<th width="20%">商品名称</th>
		<th width="30%">商品预告标题</th>
		<th width="10%">预计上架时间</th>
		<th width="10%">激活状态</th>
		<th width="10%">激活时间</th>
		<th width="10%">操作</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${page.list}" var="productForecast">
		<tr>
			<td><a href="javascript:void(0);" onclick="$('#categoryId').val('${productForecast.product.category.id}');$('#categoryName').val('${productForecast.product.category.name}');$('#searchForm').submit();return false;">${productForecast.product.category.name}</a></td>
			<td><a href="javascript:void(0);" onclick="$('#title').val('${productForecast.product.name}');$('#searchForm').submit();return false;">${fns:abbr(productForecast.product.name,50)}</a></td>
			<td><a href="${ctx}/ec/product/forecastForm?id=${productForecast.id}">${fns:abbr(productForecast.title,50)}</a></td>
			<td><fmt:formatDate value="${productForecast.forecastDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			<td style="color:<c:if test="${0 eq productForecast.isActivate}">red</c:if><c:if test="${0 ne productForecast.isActivate}">green</c:if>">${fns:getDictLabel(productForecast.isActivate,'ec_activate' , '未激活')}</td>
			<td><fmt:formatDate value="${productForecast.activateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			<td>
				<shiro:hasPermission name="ec:product:edit">
					<c:if test="${0 eq productForecast.isActivate}"><a href="javascript:void(0);" onclick="checkActivate('${productForecast.id}')">激活</a></c:if>
					<a href="${ctx}/ec/product/forecastForm?id=${productForecast.id}">修改</a>
					<c:if test="${0 eq productForecast.isActivate}"><a href="${ctx}/ec/product/deleteForecast?id=${productForecast.id}" onclick="return confirmx('确定要删除 ${productForecast.title}？',this.href);">删除</a></c:if>
					<%--<c:if test="${1 eq productForecast.isActivate}"><a href="${ctx}/ec/product/viewShare?id=${productForecast.id}">查看</a></c:if>--%>
				</shiro:hasPermission>
			</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>