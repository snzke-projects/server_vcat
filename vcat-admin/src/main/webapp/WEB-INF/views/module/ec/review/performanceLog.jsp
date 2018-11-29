<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设置商品数据历史记录</title>
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
	<sys:message content="${message}"/>
    <form:form id="searchForm" action="${ctx}/ec/review/log" method="get">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    </form:form>
	<table id="contentTable" class="table table-bordered table-hover">
		<tr>
			<th>商品名称</th>
            <th width="100px">新增销量</th>
            <th width="40px">新增上架数</th>
            <th width="40px">新增评论数</th>
            <th width="120px">操作人</th>
            <th width="120px">操作时间</th>
		</tr>
		<c:forEach items="${page.list}" var="entity">
			<tr>
				<td title="${entity.product.name}">${fns:abbr(entity.product.name,100)}</td>
				<td>${entity.sales}</td>
				<td>${entity.shelves}</td>
                <td>${entity.reviewCount}</td>
                <td>${entity.createBy.name}</td>
                <td><fmt:formatDate value="${entity.createDate}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></td>
			</tr>
		</c:forEach>
	</table>
    <div class="pagination">${page}</div>
</body>
</html>