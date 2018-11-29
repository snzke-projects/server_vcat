<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>快递公司管理</title>
	<meta name="decorator" content="default"/>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/ec/express/list">快递公司列表</a></li>
		<shiro:hasPermission name="ec:express:edit"><li><a href="${ctx}/ec/express/form">快递公司添加</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
	<table id="treeTable" class="table table-bordered table-condensed table-hover">
		<tr>
			<th>快递公司名称</th>
			<th>快递公司编码</th>
			<th>快递公司LOGO</th>
			<th>操作</th>
		</tr>
		<c:forEach items="${list}" var="entity">
			<tr>
				<td><a href="${ctx}/ec/express/form?id=${entity.id}">${entity.name}</a></td>
				<td>${entity.code}</td>
				<td><img alt="${entity.name}" src="${entity.logoUrlPath }" style="height: 50px"></td>
				<shiro:hasPermission name="ec:express:edit">
					<td>
						<a href="${ctx}/ec/express/form?id=${entity.id}">修改</a>
					</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
	</table>
</body>
</html>