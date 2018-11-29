<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>配送方管理</title>
	<meta name="decorator" content="default"/>
    <script type="application/javascript">
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/ec/distribution/list">配送方列表</a></li>
		<shiro:hasPermission name="ec:distribution:add"><li><a href="${ctx}/ec/distribution/form">配送方添加</a></li></shiro:hasPermission>
	</ul>
    <form:form id="searchForm" modelAttribute="distribution" action="${ctx}/ec/distribution/list" method="post" class="breadcrumb form-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <label>配送方名称：</label>
        <form:input path="name" maxlength="20" class="input-large"/>
        &nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
    </form:form>
	<sys:message content="${message}"/>
	<table id="treeTable" class="table table-bordered table-hover">
		<tr>
			<th>配送方名称</th>
			<th>联系人</th>
			<th>联系人电话</th>
            <th>联系人地址</th>
			<th>操作</th>
		</tr>
		<c:forEach items="${page.list}" var="entity">
			<tr>
				<td><a href="${ctx}/ec/distribution/form?id=${entity.id}">${entity.name}</a></td>
                <td>${entity.contact}</td>
				<td>${entity.phone}</td>
				<td>${entity.address}</td>
				<shiro:hasPermission name="ec:distribution:edit">
					<td>
						<a href="${ctx}/ec/distribution/form?id=${entity.id}">修改</a>
					</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
	</table>
    <div class="pagination">${page}</div>
</body>
</html>