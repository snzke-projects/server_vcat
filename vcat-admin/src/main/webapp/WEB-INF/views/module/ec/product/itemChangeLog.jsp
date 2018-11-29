<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>规格变更日志</title>
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
<form:form id="searchForm" action="${ctx}/ec/product/findItemChangeLog" method="post">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <input id="associationId" name="associationId" type="hidden" value="${dataChangeLog.associationId}"/>
</form:form>
<table id="contentTable" class="table table-bordered table-hover" style="margin-top: 0px;">
    <tr>
        <th width="120px">修改日期</th>
        <th width="100px">修改人</th>
        <th>修改内容</th>
    </tr>
    <c:forEach items="${page.list}" var="log">
        <tr>
            <td><fmt:formatDate value="${log.changeDate}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></td>
            <td>${log.changeBy}</td>
            <td>${log.changeContent}</td>
        </tr>
    </c:forEach>
</table>
<div class="pagination">${page}</div>
</body>
</html>