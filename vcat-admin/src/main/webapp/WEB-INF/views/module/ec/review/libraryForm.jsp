<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>物流公司管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(function() {
			$("#inputForm").validate({
				submitHandler: function(form){
                    if(isNull($("#categoryId").val())){
                        alertx("请选择商品分类！",function(){$('#categoryButton').trigger('click')});
                        return;
                    }
                    if(!isNumber($('#rating').val()) || $('#rating').val() < 1 || $('#rating').val() > 5){
                        alertx("评分输入范围：1-5！",function(){$('#rating').focus()});
                        return;
                    }
                    if($('#reviewText').val().length >= 200){
                        alertx("评论只能输入200个字！",function(){$('#reviewText').focus()});
                        return;
                    }
					loading('正在提交，请稍等...');
					form.submit();
				}
			});
		});
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
        <li><a href="${ctx}/ec/review/libraryList">评论库列表</a></li>
        <shiro:hasPermission name="ec:review:library:edit"><li class="active"><a href="${ctx}/ec/review/libraryForm">${not empty reviewLibrary.id?'修改':'添加'}评论</a></li></shiro:hasPermission>
    </ul>
	<form:form id="inputForm" modelAttribute="reviewLibrary" action="${ctx}/ec/review/librarySave" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">商品分类：</label>
			<div class="controls">
                <sys:treeselect id="category" name="category.id" value="${reviewLibrary.category.id}" labelName="category.name" labelValue="${reviewLibrary.category.name}"
                                title="商品分类" url="/ec/category/treeData" notAllowSelectRoot="false" cssClass="input-small"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">评分：</label>
			<div class="controls">
				<form:input path="rating" maxlength="1" cssClass="input-min required number"/>
                <span class="help-inline">输入范围：1-5</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">评论：</label>
			<div class="controls">
				<form:textarea path="reviewText" rows="3" cssStyle="width: 90%;" cssClass="required"></form:textarea>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="ec:review:library:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>