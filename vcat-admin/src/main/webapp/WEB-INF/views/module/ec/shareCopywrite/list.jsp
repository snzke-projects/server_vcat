<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分享文案模板管理</title>
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
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/ec/shareCopywrite/list">任务列表</a></li>
		<%--<shiro:hasPermission name="ec:shareCopywrite:edit"><li><a href="${ctx}/ec/shareCopywrite/form">修改文案</a></li></shiro:hasPermission>--%>
	</ul>
	<sys:message content="${message}"/>
    <table id="treeTable" class="table table-bordered">
        <thead>
        <tr>
            <th width="10%">文案类型</th>
            <th width="20%">标题</th>
            <th width="60%">内容</th>
            <th width="10%">操作</th>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="entity">
            <tr>
                <td>${fns:getDictLabel(entity.type, "ec_share_copywrite_type","未知类型")}</td>
                <td><a href="${ctx}/ec/shareCopywrite/form?id=${entity.id}">${entity.title}</a></td>
                <td>${entity.content}</td>
                <td>
                    <a href="${ctx}/ec/shareCopywrite/form?id=${entity.id}">修改</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</body>
</html>