<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>设置庄园关联商品</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(function() {
            $("#title").focus();
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
            var productId = $("#productId").val();
            if("" == productId){
                alertx("请选择庄园商品！",function(){
                    $('#productId').parent().find('.btn').trigger("click");
                });
                return false;
            }

            var sameProductArray;

            $.ajax({
                url: ctx + '/ec/farmProduct/checkSame?farmId=${farmProduct.farm.id}&productIds=' + encodeURI(productId),
                async: false,
                type: "POST",
                success: function (resultArray){
                    sameProductArray = resultArray;
                }
            });

            if(sameProductArray && sameProductArray.length > 0){
                var html = "<p>以下商品已被其他庄园选择：</p>";
                for(var i = 0; i < sameProductArray.length; i++){
                    var product = sameProductArray[i];
                    html += ("<p>　　" + (i + 1) + "." + product.name + "</p>");
                }
                html += "<p>请重新选择庄园商品</p>";
                alertx(html,null,500);
                return false;
            }

            return true;
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/ec/farmProduct/list">庄园关联产品列表</a></li>
    <li class="active"><a href="${ctx}/ec/farmProduct/form?farm.id=${farmProduct.farm.id}">设置庄园关联商品</a></li>
</ul>
<form:form id="inputForm" modelAttribute="farmProduct" action="${ctx}/ec/farmProduct/save" method="post" class="form-horizontal">
    <form:hidden path="farm.id"/>
    <form:hidden path="farm.name"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">庄园：</label>
        <div class="controls">
            <span title="${farmProduct.farm.name}">${farmProduct.farm.name}</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">庄园商品：</label>
        <div class="controls">
            <sys:productSelect id="productId" name="productIds" radio="false" value="${farmProduct.productIds}"></sys:productSelect>
        </div>
    </div>
    <div class="form-actions">
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>
    </div>
</form:form>
</body>
</html>