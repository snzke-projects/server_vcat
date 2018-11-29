<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>物流公司管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        var selectedProductObject = {};
		$(function() {
			$("#inputForm").validate({
				submitHandler: function(form){
                    if(isNull($("#product").val())){
                        alertx("请选择商品！",function(){$('#productCallbackButton').trigger('click')});
                        return;
                    }
                    if(!isNumber($('#sales').val())){
                        alertx("新增销量格式不正确！",function(){$('#sales').focus()});
                        return;
                    }
                    if(!isNumber($('#shelves').val())){
                        alertx("新增上架数格式不正确！",function(){$('#shelves').focus()});
                        return;
                    }
                    if(!isNumber($('#reviewCount').val())){
                        alertx("新增评论数量格式不正确！",function(){$('#reviewCount').focus()});
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
        <li><a href="${ctx}/ec/review/list">商品评论列表</a></li>
        <shiro:hasPermission name="ec:review:edit"><li class="active"><a href="${ctx}/ec/review/form">设置商品评论</a></li></shiro:hasPermission>
    </ul>
	<form:form id="inputForm" modelAttribute="productPerformanceLog" action="${ctx}/ec/review/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">选择商品：</label>
			<div class="controls">
                <ul id="productUL" style="list-style: none;">
                </ul>
                <sys:productSelect id="product" name="sqlMap['productIds']" radio="false"></sys:productSelect>
                <button class="btn" onclick="return productClearProductList()">清空商品</button>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">新增销量：</label>
			<div class="controls">
				<form:input path="sales" maxlength="3" cssClass="input-mini required number"/>
			</div>
		</div>
        <div class="control-group">
            <label class="control-label">新增上架数：</label>
            <div class="controls">
                <form:input path="shelves" maxlength="3" cssClass="input-mini required number"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">新增评论数量：</label>
            <div class="controls">
                <form:input path="reviewCount" maxlength="3" cssClass="input-mini required number"/>
            </div>
        </div>
		<div class="form-actions">
			<shiro:hasPermission name="ec:review:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>