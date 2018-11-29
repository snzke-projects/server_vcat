<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>供货商管理</title>
	<meta name="decorator" content="default"/>
    <script type="application/javascript">
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
        function clearForm(){
            $('#name').val("");
        }
    </script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/ec/supplier/list">供货商列表</a></li>
		<shiro:hasPermission name="ec:supplier:add"><li><a href="${ctx}/ec/supplier/form">供货商添加</a></li></shiro:hasPermission>
	</ul>
    <form:form id="searchForm" modelAttribute="supplier" action="${ctx}/ec/supplier/list" method="post" class="breadcrumb form-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <label>名称：</label>
        <form:input path="name" htmlEscape="false" maxlength="50" class="input-large" placeholder="供应商名称"/>&nbsp;
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
        <input id="btnClear" class="btn btn-default" type="button" value="清空" onclick="clearForm()"/>
    </form:form>
	<sys:message content="${message}"/>
	<table id="treeTable" class="table table-bordered table-condensed table-hover">
		<tr>
			<th>供货商名称</th>
			<th>座机</th>
			<th>联系人</th>
			<th>联系人电话</th>
			<%--<th>LOGO图片</th>--%>
			<th>操作</th>
		</tr>
		<c:forEach items="${page.list}" var="entity">
			<tr>
				<td><a href="${ctx}/ec/supplier/form?id=${entity.id}">${entity.name}</a></td>
				<td>${entity.phone}</td>
				<td>${entity.contact}</td>
				<td>${entity.mobilePhone}</td>
				<%--<td><img alt="${entity.name}" src="${entity.logoUrlPath }" style="height: 50px"></td>--%>
				<shiro:hasPermission name="ec:supplier:edit">
					<td>
						<a href="${ctx}/ec/supplier/form?id=${entity.id}">修改</a>
					</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
	</table>
    <div class="pagination">${page}</div>
</body>
</html>