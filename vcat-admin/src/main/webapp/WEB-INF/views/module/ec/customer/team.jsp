<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>小店列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<form:form id="searchForm" action="${ctx}/ec/customer/team" method="post" class="form-search">
        <input name="id" type="hidden" value="${shop.id}"/>
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-hover">
		<thead>
			<tr>
				<th width="10%">用户名</th>
                <th width="10%">手机号</th>
				<th width="10%">共创造分红奖励</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="map">
			<tr>
				<td>${map.name}</td>
                <td>${map.phone}</td>
				<td>${map.fund}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>