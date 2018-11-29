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
        function showLog(){
            top.$.jBox.open("iframe:${ctx}/ec/review/log", "商品评论设置日志",$(top.document).width()-220,$(top.document).height()-180,{
                buttons: {"确定": true},
                loaded: function(){
                    $(".jbox-content", top.document).css("overflow-y", "hidden");
                }
            });
        }
    </script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/ec/review/list">商品评论列表</a></li>
		<shiro:hasPermission name="ec:review:edit"><li><a href="${ctx}/ec/review/form">设置商品评论</a></li></shiro:hasPermission>
	</ul>
    <form:form id="searchForm" modelAttribute="reviewDetail" action="${ctx}/ec/review/list" method="get" class="breadcrumb form-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <label>商品名称：</label>
        <form:input path="product.name" cssClass="input-medium"></form:input>
        <label>评论来源：</label>
        <form:select path="from" cssClass="input-small">
            <form:option value="">全部</form:option>
            <form:option value="1">用户</form:option>
            <form:option value="2">系统</form:option>
        </form:select>
        &nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
        &nbsp;<input id="btnShowLog" class="btn btn-primary" type="button" value="查看新增评论日志" onclick="showLog()"/>
    </form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-hover">
		<tr>
            <th width="40px">评论来源</th>
			<th width="300px">商品名称</th>
            <th width="100px">评论用户</th>
            <th width="40px">评分</th>
            <th>评论内容</th>
            <th width="120px">评论时间</th>
            <th width="40px">是否显示</th>
			<th width="40px">操作</th>
		</tr>
		<c:forEach items="${page.list}" var="entity">
			<tr>
                <td>${entity.from == 2 ? '系统' : '用户'}</td>
				<td title="${entity.product.name}">${fns:abbr(entity.product.name,40)}</td>
				<td title="${entity.buyer.userName}">${fns:abbr(entity.buyer.userName,20)}</td>
				<td>${entity.rating.rating}颗星</td>
                <td title="${entity.reviewText}">${fns:abbr(entity.reviewText,100)}</td>
                <td><fmt:formatDate value="${entity.submitDate}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></td>
                <td style="color: ${entity.isDisplay == 1 ? 'green' : 'red'}">${entity.isDisplay == 1 ? '显示' : '隐藏'}</td>
                <td>
                    <shiro:hasPermission name="ec:review:edit">
                        <a href="${ctx}/ec/review/display?id=${entity.id}&isDisplay=${entity.isDisplay == 1 ? '0' : '1'}" onclick="return confirmx('确认'+this.innerHTML+'该评论？',this.href);">${entity.isDisplay == 1 ? '隐藏' : '显示'}</a>
                    </shiro:hasPermission>
                </td>
			</tr>
		</c:forEach>
	</table>
    <div class="pagination">${page}</div>
</body>
</html>