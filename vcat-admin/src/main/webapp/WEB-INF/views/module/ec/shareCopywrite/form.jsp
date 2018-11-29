<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分享文案模板管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(function() {
			$("#inputForm").validate({
				submitHandler: function(form){
					if(validateForm()){
						loading('正在提交，请稍等...');
						form.submit();
					}
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
		function validateForm(){
			if("" == $('#type').val()){
				alertx("请选择文案类型！", function (){
                    $("#type").select2("open");
                });
				return false;
			}
			return true;
		}
	</script>
</head>
<body>
	<form:form id="inputForm" modelAttribute="shareCopywrite" action="${ctx}/ec/shareCopywrite/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="panel panel-info">
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
                    分享文案模板
				</div>
			</div>
			<div class="panel-body">
				<div class="tab-content">
					<div class="control-group">
						<label class="control-label">标题：</label>
						<div class="controls">
							<input type="text" id="title" name="title" maxlength="100" class="input-xxlarge required" value="${shareCopywrite.title}"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">内容：</label>
						<div class="controls">
							<textarea id="content" name="content" class="required" rows="4" maxlength="500" style="width: 95%">${shareCopywrite.content}</textarea>
						</div>
					</div>
                    <div class="control-group">
                        <label class="control-label">文案类型：</label>
                        <div class="controls">
                            <%--<form:select id="type" path="type" items="${fns:getDictList('ec_share_copywrite_type')}" itemValue="value" itemLabel="label" cssClass="input-medium"></form:select>--%>
                            <span>${fns:getDictLabel(shareCopywrite.type,"ec_share_copywrite_type","未知类型")}</span>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">备注：</label>
                        <div class="controls">
                            <span class="help-inline">
                                标题和内容支持通配符替换</br>
                                    ${'${'}shop_name${'}'}：店铺名称</br>
                                    ${'${'}product_name${'}'}：商品名称
                            </span>
                        </div>
                    </div>
				</div>
				<div class="form-actions">
					<shiro:hasPermission name="ec:shareCopywrite:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
			</div>
		</div>
	</form:form>
</body>
</html>