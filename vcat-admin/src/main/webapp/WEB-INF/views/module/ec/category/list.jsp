<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品分类管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#treeTable").treeTable({expandLevel : 2});
		});
        function updateSort() {
            loading('正在提交，请稍等...');
            $("#listForm").attr("action", "${ctx}/ec/category/updateSort");
            $("#listForm").submit();
        }
        function activate(id,name,isActivate){
            var activate = "1" == isActivate ? "0" : "1";
            var url = ctx + "/ec/category/activate?id="+id+"&name="+name+"&isActivate="+activate;
            if('0' == activate){
                confirmx('取消激活会自动下架该分类下所有商品，确认取消激活'+name+'？',url);
            }else{
                confirmx('确认激活'+name+'？',url);
            }
        }
        function rebuildLR(){
            loading('正在重建树结构左右坐标，请稍等...');
            $.get(ctx+"/ec/category/rebuild",function(){
                location.href = ctx + "/ec/category/list";
            });
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/ec/category/list">分类列表</a></li>
		<shiro:hasPermission name="ec:category:add"><li><a href="${ctx}/ec/category/form">分类添加</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
    <form id="listForm" method="post">
        <table id="treeTable" class="table table-bordered table-condensed table-hover">
            <tr>
                <th width="60%">分类名称</th>
                <th width="20%">展示排序</th>
                <th width="20%">操作</th>
            </tr>
            <c:forEach items="${list}" var="cat">
                <tr id="${cat.id}" pId="${cat.parent.id}">
                    <td>
                        <c:if test="${!cat.isRoot}"><a href="${ctx}/ec/category/form?id=${cat.id}"></c:if>
                            ${cat.name}
                        <c:if test="${!cat.isRoot}"></a></c:if>
                    </td>
                    <td>
                        <c:if test="${!cat.isRoot}"><input type="hidden" name="ids" value="${cat.id}"/></c:if>
                        <c:if test="${!cat.isRoot}"><input name="displayOrder" type="text" value="${cat.displayOrder}" style="width:50px;margin:0;padding:0;text-align:center;"></c:if>
                    </td>
                    <td>
                        <c:if test="${!cat.isRoot}">
                            <shiro:hasPermission name="ec:category:edit">
                                <a href="javascript:void(0);" onclick="activate('${cat.id}','${cat.name}','${cat.isActivate}')">${cat.isActivate == 1 ? '取消' : ''}激活</a>
                            </shiro:hasPermission>
                            <shiro:hasPermission name="ec:category:edit">
                                <a href="${ctx}/ec/category/form?id=${cat.id}">修改</a>
                            </shiro:hasPermission>
                            <shiro:hasPermission name="ec:category:delete">
                                <a href="${ctx}/ec/category/delete?id=${cat.id}" onclick="return confirmx('要删除该栏目及所有子栏目项吗？', this.href)">删除</a>
                            </shiro:hasPermission>
                        </c:if>
                        <shiro:hasPermission name="ec:category:add">
                            <a href="${ctx}/ec/category/form?parent.id=${cat.id}&parent.name=${cat.name}">添加下级栏目</a>
                        </shiro:hasPermission>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </form>
    <shiro:hasPermission name="ec:category:edit">
        <div class="form-actions pagination-left">
            <input id="btnSubmit" class="btn btn-primary" type="button" value="保存排序" onclick="updateSort();"/>
            <shiro:isUser name="admin">
                <input id="btnRebuild" class="btn btn-primary" type="button" value="重建坐标" onclick="rebuildLR();"/>
            </shiro:isUser>
        </div>
    </shiro:hasPermission>
</body>
</html>