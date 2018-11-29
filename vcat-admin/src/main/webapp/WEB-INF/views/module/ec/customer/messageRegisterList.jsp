<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>注册消息列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}
		function activate(id,isActivate){
            var cancelTitle = "0" == isActivate ? "取消" : "";
			confirmx("确认"+cancelTitle+"激活？",function (){
				$.ajax({
					type : 'POST',
					url : ctx + "/ec/customer/activateMessageRegister",
					data : {id : id,isActivate:isActivate},
					success : function(){
						try{
							alertx(cancelTitle+"激活成功！",function(){
								location.href = location.href;
							});
						}catch(e){
							console.log(cancelTitle+"激活失败："+e.message);
							return doError(e);
						}
					},
					error: function(XMLHttpRequest) {
						return doError(XMLHttpRequest);
					}
				});
			});
		}
	</script>
</head>
<body>
    <sys:message content="${message}"/>
    <div class="panel panel-info">
        <div class="panel-heading">
            <div class="panel-title" style="font-size: 14px;">
                注册消息
            </div>
        </div>
        <div class="panel-body">
            <table id="contentTable" class="table table-bordered table-hover">
                <thead>
                <tr>
                    <th width="70%">消息标题</th>
                    <th width="10%">发送时间</th>
                    <th width="10%">激活状态</th>
                    <th width="10%">操作</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${page.list}" var="messageRegister">
                    <tr>
                        <td><a href="${ctx}/ec/customer/messageRegisterForm?id=${messageRegister.id}">${fns:abbr(messageRegister.article.title,50)}</a></td>
                        <td>注册后${messageRegister.intervalShow}</td>
                        <td style="color:<c:if test="${0 eq messageRegister.isActivate}">red</c:if><c:if test="${0 ne messageRegister.isActivate}">green</c:if>">${fns:getDictLabel(messageRegister.isActivate,'ec_activate' , '未激活')}</td>
                        <td>
                            <shiro:hasPermission name="ec:customer:messageRegister:edit">
                                <a href="javascript:void(0);" onclick="activate('${messageRegister.id}','${0 eq messageRegister.isActivate ? '1' : '0'}')">${1 eq messageRegister.isActivate ? '取消' : ''}激活</a>
                                <a href="${ctx}/ec/customer/messageRegisterForm?id=${messageRegister.id}">修改</a>
                                <a href="${ctx}/ec/customer/deleteMessageRegister?id=${messageRegister.id}" onclick="return confirmx('确定要删除 ${messageRegister.article.title} 注册消息？',this.href);">删除</a>
                            </shiro:hasPermission>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <div class="pagination">${page}</div>
        </div>
    </div>
</body>
</html>