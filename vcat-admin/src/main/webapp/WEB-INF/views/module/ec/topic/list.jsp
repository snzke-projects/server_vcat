<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>专题管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#treeTable").treeTable({expandLevel : 2});
		});
        function updateSort() {
            loading('正在提交，请稍等...');
            $("#listForm").attr("action", "${ctx}/ec/topic/updateSort");
            $("#listForm").submit();
        }
        function activate(id,name,isActivate){
            var activate = "1" == isActivate ? "0" : "1";
            var url = ctx + "/ec/topic/activate?id="+id+"&name="+name+"&isActivate="+activate;
            if('0' == activate){
                confirmx('确认取消激活'+name+'？',url);
            }else{
                confirmx('确认激活'+name+'？',url);
            }
        }
        function rebuildLR(){
            loading('正在重建树结构左右坐标，请稍等...');
            $.get(ctx+"/ec/topic/rebuild",function(){
                location.href = ctx + "/ec/topic/list";
            });
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/ec/topic/list">专题列表</a></li>
		<shiro:hasPermission name="ec:topic:edit"><li><a href="${ctx}/ec/topic/form">专题添加</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
    <form id="listForm" method="post">
        <table id="treeTable" class="table table-bordered table-condensed table-hover">
            <tr>
                <th>专题名称</th>
                <th width="60px">展示排序</th>
                <th width="250px">操作</th>
            </tr>
            <c:forEach items="${list}" var="topic">
                <tr id="${topic.id}" pId="${topic.parent.id}">
                    <td>
                        <c:if test="${!topic.isRoot}"><a href="${ctx}/ec/topic/form?id=${topic.id}"></c:if>
                            ${topic.title}
                        <c:if test="${!topic.isRoot}"></a></c:if>
                    </td>
                    <td>
                        <c:if test="${!topic.isRoot}"><input type="hidden" name="ids" value="${topic.id}"/></c:if>
                        <c:if test="${!topic.isRoot}"><input name="displayOrder" type="text" value="${topic.displayOrder}" style="width:50px;margin:0;padding:0;text-align:center;"></c:if>
                    </td>
                    <td>
                        <c:if test="${!topic.isRoot}">
                            <shiro:hasPermission name="ec:topic:edit">
                                <a href="javascript:void(0);" onclick="activate('${topic.id}','${topic.title}','${topic.isActivate}')">${topic.isActivate == 1 ? '取消' : ''}激活</a>
                            </shiro:hasPermission>
                            <shiro:hasPermission name="ec:topic:edit">
                                <a href="${ctx}/ec/topic/form?id=${topic.id}">修改</a>
                            </shiro:hasPermission>
                            <shiro:hasPermission name="ec:topic:edit">
                                <a href="${ctx}/ec/topic/delete?id=${topic.id}" onclick="return confirmx('要删除该专题及所有子专题吗？', this.href)">删除</a>
                            </shiro:hasPermission>
                        </c:if>
                        <c:if test="${!topic.isRoot}">
                            <a href="javascript:void(0);" onclick="showViewPage(ctx + '/ec/topic/selectedProductList?sqlMap[\'topicId\']=${topic.id}','${topic.title} 已选商品')">已选商品</a>
                        </c:if>
                        <shiro:hasPermission name="ec:topic:edit">
                            <a href="${ctx}/ec/topic/form?parent.id=${topic.id}&parent.title=${topic.title}">添加下级专题</a>
                        </shiro:hasPermission>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </form>
    <shiro:hasPermission name="ec:topic:edit">
        <div class="form-actions pagination-left">
            <input id="btnSubmit" class="btn btn-primary" type="button" value="保存排序" onclick="updateSort();"/>
            <shiro:isUser name="admin">
                <input id="btnRebuild" class="btn btn-primary" type="button" value="重建坐标" onclick="rebuildLR();"/>
            </shiro:isUser>
        </div>
    </shiro:hasPermission>
</body>
</html>