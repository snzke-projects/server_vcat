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
					descriptionEditor.sync();
                    if(isNull($('#supplierLogo').val())){
                        alertx("请上传一张Logo图片！",function(){$('#supplierLogo_image_upload_button').trigger("click")});
                        return false;
                    }
                    if(isNull($('#description').val())){
                        alertx("请填写品牌故事！",function(){descriptionEditor.focus()});
                        return false;
                    }
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs<c:if test="${supplier.sqlMap.onlyAdd eq 'true'}"> hide</c:if>">
		<li><a href="${ctx}/ec/supplier/list">供货商列表</a></li>
		<li class="active"><a href="${ctx}/ec/supplier/form?id=${supplier.id}">供货商<shiro:hasPermission name="ec:supplier:edit">${not empty supplier.id?'修改':'添加'}</shiro:hasPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="supplier" action="${ctx}/ec/supplier/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">供货商名称：</label>
			<div class="controls">
				<form:input path="name" maxlength="200" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">供货商官网：</label>
			<div class="controls">
				<label for="siteUrl">http://</label>
				<form:input path="siteUrl" maxlength="200" class="input-xlarge" placeholder="www.v-cat.cn"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">座机：</label>
			<div class="controls">
				<form:input path="phone" maxlength="20" class="input-small"/>
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
				<form:input path="mobilePhone" maxlength="20" class="input-small"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">联系人地址：</label>
			<div class="controls">
				<form:input path="address" maxlength="150" class="input-xxlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">LOGO图片：</label>
			<div class="controls">
				<sys:imageUpload id="supplierLogo" name="logoUrl" value="${supplier.logoUrlPath}" ></sys:imageUpload>
                <span class="help-inline">推荐上传分辨率为100*100的图片</span>
			</div>
		</div>
        <div class="control-group">
            <label class="control-label">分销说明：</label>
            <div class="controls">
                <form:textarea id="description" path="description" rows="4" class="input-xxlarge" />
                <sys:kindeditor replace="description" width="100%" height="300px"/>
            </div>
        </div>
		<div class="form-actions" >
			<shiro:hasPermission name="ec:supplier:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn<c:if test="${supplier.sqlMap.onlyAdd eq 'true'}"> hide</c:if>" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>