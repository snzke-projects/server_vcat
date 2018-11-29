<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>品牌管理</title>
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
    <table id="treeTable" class="table table-bordered table-hover">
        <tr>
            <th width="100px">用户名称</th>
            <th>回答</th>
            <th width="120px">回答时间</th>
        </tr>
        <c:forEach items="${page.list}" var="entity">
            <tr>
                <td>${entity.name}</td>
                <td><pre title="${entity.answer}">${fns:abbr(entity.answer,200)}</pre></td>
                <td>${entity.time}</td>
            </tr>
        </c:forEach>
    </table>
    <div class="pagination">${page}</div>
    <form:form id="searchForm" action="${ctx}/ec/customer/viewShortAnswerList" method="post">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <input name="id" type="hidden" value="${question.id}"/>
        <input name="title" type="hidden" value="${question.title}"/>
        <input name="sqlMap['activityId']" type="hidden" value="${question.sqlMap.activityId}"/>
    </form:form>
</body>
</html>