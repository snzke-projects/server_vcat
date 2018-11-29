<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>文案管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function() {
			$("#title").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
                    if(isNull($('#productId').val())){
                        alertx("请选择素材所属商品！",function(){$('#productIdCallbackButton').trigger("click")});
                        return false;
                    }
                    if(isNull($('#productImages').val())){
                        alertx("请上传公有图片素材！",function(){$('#productImages_image_upload_button').trigger("click")});
                        return false;
                    }
                    if(checkShopImage()){
                        return false;
                    }
					loading('正在提交，请稍等...');
					form.submit();
				}
			});
            function checkShopImage() {
                if(shopUrlData){
                    var shopCountData = {};
                    for(var fieldName in shopUrlData){
                        var data = shopUrlData[fieldName];
                        if(shopCountData[data.shopId]){
                            shopCountData[data.shopId] = shopCountData[data.shopId] + 1;
                        }else{
                            shopCountData[data.shopId] = 1;
                        }
                        if(shopCountData[data.shopId] > 6){
                            alertx("<span style='color:red'>店铺【" + data.shopName + "】手机号【" + data.phoneNumber + "】的专属图片素材不能大于6张！</span>",null,500);
                            return true;
                        }
                    }
                }
                return false;
            }
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/ec/copywrite/list">文案列表</a></li>
		<li class="active"><a href="${ctx}/ec/copywrite/form?id=${brand.id}">文案<shiro:hasPermission name="ec:copywrite:edit">${not empty brand.id?'修改':'添加'}</shiro:hasPermission></a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="copywrite" action="${ctx}/ec/copywrite/save" method="post" cssClass="form-horizontal" cssStyle="margin-left: 0px;">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    文案详情
                </div>
            </div>
            <div class="panel-body">
                <div class="control-group">
                    <label class="control-label">选择商品：</label>
                    <div class="controls">
                        <sys:productSelect id="productId" name="product.id" value="${copywrite.product.id}"></sys:productSelect>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">文案标题：</label>
                    <div class="controls">
                        <form:input path="title" maxlength="100" cssClass="required input-xxlarge"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">文案内容：</label>
                    <div class="controls">
                        <form:textarea path="content" rows="4" cssClass="required" cssStyle="width: 90%" />
                    </div>
                </div>
            </div>
        </div>
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    公有图片素材
                </div>
            </div>
            <div class="panel-body">
                <sys:multiImageUpload id="productImages" name="sqlMap['productImageUrls']" value="${copywrite.productImagesPath}" size="6" widthScale="1" heightScale="1" sortable="true"></sys:multiImageUpload>
            </div>
        </div>
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    专属图片素材
                </div>
            </div>
            <div class="panel-body">
                <style>
                    #shopImages_image_preview li{float: left;}
                </style>
                <script type="text/javascript">
                    var shopImagesImageSize = '1000';
                    var shopImagesViewImageWidth = 100;
                    var shopImagesViewImageHeight = shopImagesGetViewHeight('1','1');
                    var shopUrlData = {};
                    function getUrlData(url){
                        var data = {};
                        data.url = url;
                        data.fileId = shopImagesGetFileId(url);
                        data.shopId = getUrlParam(url,"shopId");
                        data.shopName = getUrlParam(url,"shopName");
                        data.phoneNumber = getUrlParam(url,"phoneNumber");
                        return data;
                    }
                    KindEditor.ready(function(K) {
                        K.DEBUG = true;         // 打开DEBUG模式，可显示错误提示
                        var urls = '${copywrite.shopImagesPath}';
                        if('' != urls){
                            var urlArray = urls.split("|");
                            for(var i in urlArray){
                                shopUrlData["data_"+i] = getUrlData(urlArray[i]);
                            }
                            shopImagesImagePreview();
                        }
                        $('#shopImages_image_preview').sortable().bind("sortupdate",function(){
                            var newData = {}
                            $('#shopImages_image_preview').find("img").each(function(i){
                                newData["data_"+i] = shopUrlData["data_"+$(this).attr("index")];
                            });
                            shopUrlData = newData;
                            shopImagesImagePreview();
                        });
                        K('#shopImages_image_upload_button').click(function() {
                            if(!checkShopImagesImageSize()){
                                alertx("图片数量不能大于" + shopImagesImageSize + "张！");
                                return;
                            }
                            var ids = $('#shopImages').val();
                            var imageUploadLimit = ids == "" ? shopImagesImageSize : shopImagesImageSize - Object.getOwnPropertyNames(shopUrlData).length;
                            var editor = K.editor({
                                allowFileManager : false,
                                imageUploadLimit : imageUploadLimit,
                                imageSizeLimit : getSizeLabel('${fns:getDictSingleValue('ec_image_size_limit', 500)}'),
                                uploadJson : ctx + '/kindeditor/upload?widthScale=1&heightScale=1&checkShop=true'
                            });
                            editor.loadPlugin('multiimage', function() {
                                editor.plugin.multiImageDialog({
                                    clickFn : function(urlList) {
                                        var index = Object.getOwnPropertyNames(shopUrlData).length;
                                        K.each(urlList, function(i, data) {
                                            shopUrlData["data_"+index++] = getUrlData(data.url);
                                        });
                                        shopImagesImagePreview();
                                        editor.hideDialog();
                                    }
                                });
                            });
                        });
                    });
                    function checkShopImagesImageSize(){
                        var fileId = $('#shopImages').val();
                        if("" != fileId){
                            var fileIds = fileId.split("|");
                            if(null != fileIds && fileIds.length >= shopImagesImageSize){
                                return false;
                            }
                        }
                        return true;
                    }
                    function shopImagesGetFileId(url) {
                        var location = url.split("/");
                        if(location.length > 5){
                            return location[5];
                        }
                        return null;
                    }
                    function shopImagesDelImage(fieldName){
                        delete shopUrlData[fieldName];
                        shopImagesImagePreview();
                    }
                    function shopImagesImagePreview(){
                        $("#shopImages_image_preview").children().remove();
                        var li;
                        var index = 0;
                        var shopIds = "";
                        var fileIds = "";
                        var urls = "";
                        for(var fieldName in shopUrlData){
                            var data = shopUrlData[fieldName];
                            shopIds += (index == 0 ? data.shopId : "|" + data.shopId);
                            fileIds += (index == 0 ? data.fileId : "|" + data.fileId);
                            urls += (index == 0 ? data.url : "|" + data.url);
                            li = '<li style="width:'+(shopImagesViewImageWidth+10)+'px;height:'+(shopImagesViewImageHeight+20)+'px;text-align: center;line-height: 110px;position: relative;">';
                            li += '<img index="'+index+'" src="'+data.url+'" title="店铺名：'+data.shopName+'\r\n手机号：'+data.phoneNumber+'" style="width:'+shopImagesViewImageWidth+'px;height:'+shopImagesViewImageHeight+'px;border:0;box-shadow: 0px 0px 10px #505050;margin:10px;\">';
                            li += '&nbsp;&nbsp;<a href="javascript:" onclick="shopImagesDelImage(\'' + fieldName + '\');" class="icon-remove" style="position: absolute;top: 15px;right: 5px;"></a></li>';
                            $("#shopImages_image_preview").append(li);
                            index++;
                        }
                        if ($("#shopImages_image_preview").text() == ""){
                            $("#shopImages_image_preview").html("<li style='list-style:none;padding-top:5px;'>无</li>");
                        }

                        $('#shopIds').val(shopIds);
                        $('#shopImages').val(fileIds);
                        $('#shopImages_url').val(urls);
                    }

                    function shopImagesGetViewHeight(width,height){
                        if(!isNull(width) && !isNull(width)){
                            return parseInt(height) / (parseInt(width) / shopImagesViewImageWidth);
                        }
                        return 200;
                    }
                </script>
                <ul id="shopImages_image_preview" style="list-style: none;overflow: hidden;margin: 0 auto;"></ul>
                <input type="hidden" id="shopImages" name="sqlMap['shopImageUrls']" value="" />
                <input type="hidden" id="shopIds" name="sqlMap['shopIds']" value="" />
                <input type="hidden" id="shopImages_url" value="" />
                <input type="button" id="shopImages_image_upload_button" class="btn" value="上传图片" />
                <span class="help-inline" style="color: red">图片命名规则：店主注册手机号(_上传排序).(png | jpg)</span>
                <span class="help-inline">拖动图片可变更排序</span>
                <span class="help-inline">图片宽高比例必须为1：1</span>
                <span class="help-inline">鼠标悬停可查看图片所属店铺基本信息</span>
            </div>
        </div>
		<div class="form-actions" >
			<shiro:hasPermission name="ec:copywrite:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>