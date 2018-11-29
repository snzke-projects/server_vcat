<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分类管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(function() {
			$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/ec/distribution/list">配送方列表</a></li>
		<li class="active"><a href="${ctx}/ec/distribution/form?id=${distribution.id}">配送方<shiro:hasPermission name="ec:distribution:edit">${not empty distribution.id?'修改':'添加'}</shiro:hasPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="distribution" action="${ctx}/ec/distribution/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">配送方名称：</label>
			<div class="controls">
				<form:input path="name" maxlength="40" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">联系人姓名：</label>
			<div class="controls">
				<form:input path="contact" maxlength="20" class="input-small"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">联系人电话：</label>
			<div class="controls">
				<form:input path="phone" maxlength="20" class="input-small"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">联系人地址：</label>
			<div class="controls">
				<form:input path="address" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
        <div class="control-group">
            <label class="control-label">备注：</label>
            <div class="controls">
                <form:textarea path="note" class="input-xxlarge" rows="3"/>
            </div>
        </div>
		<div class="form-actions" >
			<shiro:hasPermission name="ec:distribution:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>