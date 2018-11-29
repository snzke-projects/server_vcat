<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>优惠卷设置</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(function() {
            $("#name").focus();
            $("#inputForm").validate({
                submitHandler: function(form){
                    if(!checkForm())return;
                    loading('正在提交，请稍等...');
                    form.submit();
                },
            });
        });
        function checkForm(){
            return true;
        }
        function showProductList(productArray){
            var html = "";
            $('.productIdInput').remove();
            if(productArray){
                if(isArray(productArray)){
                    for (var i = 0; i < productArray.length; i++) {
                        html += '<input type="hidden" class="productIdInput" name="productList[' + i + '].id" value="' + productArray[i].id + '" />';
                    }
                }else{
                    html += '<input type="hidden" class="productIdInput" name="productList[0].id" value="' + productArray.id + '" />';
                }
            }
            $("#btnSubmit").before(html);
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/ec/promotion/list">优惠卷列表</a></li>
    <li class="active"><a href="${ctx}/ec/promotion/form?id=${promotion.id}"><shiro:hasPermission name="ec:promotion:edit">${not empty promotion.id?'修改':'设置'}</shiro:hasPermission>优惠卷</a></li>
</ul><br/>
<form:form id="inputForm" modelAttribute="promotion" action="${ctx}/ec/promotion/save" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">优惠卷名称：</label>
        <div class="controls">
            <form:input path="name" maxlength="50" class="required"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">优惠份数：</label>
        <div class="controls">
            <form:input path="freeCount" maxlength="10" class="required digits input-small"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">使用条件：</label>
        <div class="controls">
            <span>单笔订单中，使用指定商品中购买份数每满&nbsp;<form:input path="buyCount" maxlength="10" class="required digits input-min"/>&nbsp;份时可使用。</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">选择商品：</label>
        <div class="controls">
            <sys:productSelect id="product" name="productId" radio="false" callback="showProductList" value="${promotion.productIdArray}"></sys:productSelect>
        </div>
    </div>
    <div class="form-actions" >
        <shiro:hasPermission name="ec:promotion:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>