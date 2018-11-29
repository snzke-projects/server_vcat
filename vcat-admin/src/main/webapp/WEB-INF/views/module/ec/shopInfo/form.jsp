<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>庄园主信息管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        var selectedProductObject = {};
        var setInfo = '${shopInfo.isNewRecord}' == 'true';
        $(function() {
            $("#inputForm").validate({
                submitHandler: function(form){
                    if(isNull($("#product").val())){
                        alertx("请选择庄园！",function(){$('#productCallbackButton').trigger('click')});
                        return;
                    }
                    if(isNull($("#shop").val())){
                        alertx("请选择用户！",function(){$('#shopCallbackButton').trigger('click')});
                        return;
                    }
//                    if(isNull($("#qrCode").val())){
//                        alertx("请上传微信二维码！",function(){$('#qrCode_image_upload_button').trigger('click')});
//                        return;
//                    }
                    var shopInfo = null;
                    $.ajax({
                        url: ctx + "/ec/shopInfo/getSame?id=" + $('#id').val() + "&product.id=" + $('#product').val() + "&shop.id=" + $('#shop').val(),
                        async: false,
                        success: function (result){
                            shopInfo = result;
                        }
                    });
                    if(shopInfo){
                        alertx("该店铺【" + shopInfo.shop.name + "】已经拥有该庄园【" + shopInfo.product.name + "】！",null, null, 500);
                        return;
                    }
                    var sameFarmName = null;
                    $.ajax({
                        url: ctx + "/ec/shopInfo/getSameFarmName?id=" + $('#id').val() + "&product.id=" + $('#product').val() + "&farmName=" + $('#farmName').val(),
                        async: false,
                        success: function (result){
                            sameFarmName = result;
                        }
                    });
                    if(sameFarmName){
                        alertx("【" + sameFarmName + "】已被其他庄园主使用！");
                        return;
                    }
                    loading('正在提交，请稍等...');
                    form.submit();
                }
            });
        });
        function setShopInfo(shop){
            $.get(ctx + "/ec/shopInfo/getHistory?shopId=" + shop.id, function (shopInfo){
                if(shopInfo && setInfo){
                    $('#farmName').val(shopInfo.farmName);
                    $('#realName').val(shopInfo.realName);
                    $('#baseCardInventory').val(shopInfo.baseCardInventory);
                    $('#companyCardInventory').val(shopInfo.companyCardInventory);
                    $('#qrCode').val(shopInfo.qrCodeUrl);
                    $('#qrCode_img').attr("src",shopInfo.qrCodeUrlPath);
                }
                setInfo = true;
            });
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/ec/shopInfo/list">庄园主信息列表</a></li>
    <shiro:hasPermission name="ec:shopInfo:edit"><li class="active"><a href="${ctx}/ec/shopInfo/form">设置庄园主</a></li></shiro:hasPermission>
</ul>
<form:form id="inputForm" modelAttribute="shopInfo" action="${ctx}/ec/shopInfo/save" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">选择庄园：</label>
        <div class="controls">
            <sys:productSelect id="product" name="product.id" value="${shopInfo.product.id}" queryParam="isVirtualProduct=true"></sys:productSelect>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">选择用户：</label>
        <div class="controls">
            <sys:shopSelect id="shop" name="shop.id" value="${shopInfo.shop.id}" queryParam="sqlMap['type']=sellerList" callback="setShopInfo"></sys:shopSelect>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">庄园名称：</label>
        <div class="controls">
            <form:input path="farmName" maxlength="20" cssClass="input-medium required"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">真实名称：</label>
        <div class="controls">
            <form:input path="realName" maxlength="10" cssClass="input-medium required"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">基地卡片库存：</label>
        <div class="controls">
            <form:input path="baseCardInventory" maxlength="4" cssClass="input-medium required digits"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">公司卡片库存：</label>
        <div class="controls">
            <form:input path="companyCardInventory" maxlength="4" cssClass="input-medium required digits"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">微信二维码：</label>
        <div class="controls">
            <sys:imageUpload id="qrCode" name="qrCodeUrl" value="${shopInfo.qrCodeUrlPath}" widthScale="1" heightScale="1"></sys:imageUpload>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">备注：</label>
        <div class="controls">
            <form:textarea path="note" maxlength="200" rows="4" cssStyle="width: 95%"/>
        </div>
    </div>
    <div class="form-actions">
        <shiro:hasPermission name="ec:shopInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>