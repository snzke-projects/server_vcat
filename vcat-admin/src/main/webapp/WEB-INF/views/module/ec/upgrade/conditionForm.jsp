<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>升级规则管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
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
		<li><a href="${ctx}/ec/upgrade/conditionList">升级规则列表</a></li>
		<li class="active"><a href="${ctx}/ec/upgrade/conditionForm?id=${upgradeCondition.id}"><shiro:hasPermission name="ec:upgradeCondition:edit">${not empty upgradeCondition.id?'修改':'设置'}升级规则</shiro:hasPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="upgradeCondition" action="${ctx}/ec/upgrade/conditionSave" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">选择商品：</label>
			<div class="controls">
                <sys:productSelect id="product" name="sqlMap['productIds']" radio="false" value="${upgradeCondition.productIdArray}"></sys:productSelect>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">升级额定销售额：</label>
			<div class="controls">
				<form:input path="amount" maxlength="20" min="0" class="required number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">销售额累积时限：</label>
			<div class="controls">
				<span>${upgradeCondition.periodLabel}</span>
                <input type="hidden" name="period" value="${upgradeCondition.period}"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="ec:upgradeCondition:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>