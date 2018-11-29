<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>评论库管理</title>
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
		<li class="active"><a href="${ctx}/ec/review/libraryList">评论库列表</a></li>
		<shiro:hasPermission name="ec:review:library:edit"><li><a href="${ctx}/ec/review/libraryForm">添加评论到评论库中</a></li></shiro:hasPermission>
	</ul>
    <form:form id="searchForm" modelAttribute="reviewLibrary" action="${ctx}/ec/review/libraryList" method="get" class="breadcrumb form-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <label>分类：</label>
        <sys:treeselect id="category" name="category.id" value="${reviewLibrary.category.id}" labelName="category.name" labelValue="${reviewLibrary.category.name}"
                        title="商品分类" url="/ec/category/treeData" notAllowSelectRoot="false" cssClass="input-small"/>
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
    </form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-hover">
		<tr>
			<th width="100px">类别</th>
			<th width="100px">评论星级</th>
			<th>评论内容</th>
			<th width="100px">操作</th>
		</tr>
		<c:forEach items="${page.list}" var="entity">
			<tr>
				<td><a href="javascript:void(0)" onclick="$('#categoryId').val('${entity.category.id}');$('#categoryName').val('${entity.category.name}');$('#searchForm').submit();">${entity.category.name}</a></td>
				<td>${entity.rating}颗星</td>
				<td title="${entity.reviewText}"><a href="${ctx}/ec/review/libraryForm?id=${entity.id}">${fns:abbr(entity.reviewText,100)}</a></td>
                <td>
                    <shiro:hasPermission name="ec:review:library:edit">
                        <a href="${ctx}/ec/review/libraryForm?id=${entity.id}">修改</a>
                        <a href="${ctx}/ec/review/libraryDelete?id=${entity.id}" onclick="return confirmx('确认删除此评论？',this.href)">删除</a>
                    </shiro:hasPermission>
                </td>
			</tr>
		</c:forEach>
	</table>
    <div class="pagination">${page}</div>
</body>
</html>