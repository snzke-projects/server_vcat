<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="id" type="java.lang.String" required="true" description="上传inputID"%>
<%@ attribute name="name" type="java.lang.String" required="false" description="上传的Name"%>
<%@ attribute name="value" type="java.lang.String" required="false" description="已上传的图片地址集合(每张图片ID以|分割)"%>
<%@ attribute name="size" type="java.lang.Integer" required="false" description="图片的个数上传限制" %>
<%@ attribute name="sortable" type="java.lang.Boolean" required="false" description="是否支持图片排序(为空则不包含排序功能)" %>
<%@ attribute name="widthScale" type="java.lang.Integer" required="false" description="图片宽度比例" %>
<%@ attribute name="heightScale" type="java.lang.Integer" required="false" description="图片高度度比例 如同时传入高宽比例，则限制图片高宽比例" %>
<script type="text/javascript">include('kindeditor_lib','${ctxStatic}/kindeditor/',['kindeditor-all-min.js']);</script>
<script type="text/javascript">include('kindeditor_lang','${ctxStatic}/kindeditor/lang/',['zh_CN.js']);</script>
<script type="text/javascript">include('jquery_sortable','${ctxStatic}/jquery-sortable/',['jquery-ui.min.js']);</script>
<link href="${ctxStatic}/kindeditor/themes/default/default.css" type="text/css" rel="stylesheet" />
<style>
    #${id}_image_preview li{float: left;}
</style>
<script type="text/javascript">
	var ${id}ImageSize = '${size}';
	${id}ImageSize = '' == ${id}ImageSize ? 1 : ${id}ImageSize;
	var ${id}Sortable = '' != '${sortable}' && '0' != '${sortable}' && 'false' != '${sortable}' ? true : false;
	var ${id}ViewImageWidth = 200;
	var ${id}ViewImageHeight = ${id}GetViewHeight('${widthScale}','${heightScale}');
    var ${id}UrlData = {};
    function ${id}GetUrlData(url){
        var data = {};
        data.url = url;
        data.fileId = ${id}GetFileId(url);
        return data;
    }
	KindEditor.ready(function(K) {
        K.DEBUG = true;         // 打开DEBUG模式，可显示错误提示
		var urls = '${value}';
		if('' != urls){
            var urlArray = urls.split("|");
            for(var i in urlArray){
                ${id}UrlData["data_"+i] = ${id}GetUrlData(urlArray[i]);
            }
            ${id}ImagePreview();
		}
        if(${id}Sortable){
            $('#${id}_image_preview').sortable().bind("sortupdate",function(){
                var newData = {}
                $('#${id}_image_preview').find("img").each(function(i){
                    newData["data_"+i] = ${id}UrlData["data_"+$(this).attr("index")];
                });
                ${id}UrlData = newData;
                ${id}ImagePreview();
            });
        }
		K('#${id}_image_upload_button').click(function() {
			if(!${id}CheckImageSize()){
				alertx("图片数量不能大于" + ${id}ImageSize + "张！");
				return;
			}
            var ids = $('#${id}').val();
            var imageUploadLimit = ids == "" ? ${id}ImageSize : ${id}ImageSize - Object.getOwnPropertyNames(${id}UrlData).length;
            var editor = K.editor({
                allowFileManager : false,
                imageUploadLimit : imageUploadLimit,
                imageSizeLimit : getSizeLabel('${fns:getDictSingleValue('ec_image_size_limit', 500)}'),
                uploadJson : '${ctx}/kindeditor/upload?widthScale=${widthScale}&heightScale=${heightScale}'
            });
			editor.loadPlugin('multiimage', function() {
				editor.plugin.multiImageDialog({
					clickFn : function(urlList) {
                        var index = Object.getOwnPropertyNames(${id}UrlData).length;
                        K.each(urlList, function(i, data) {
                            ${id}UrlData["data_"+index++] = ${id}GetUrlData(data.url);
                        });
                        ${id}ImagePreview();
                        editor.hideDialog();
					}
				});
			});
		});
	});
	function ${id}CheckImageSize(){
		var fileId = $('#${id}').val();
		if("" != fileId){
			var fileIds = fileId.split("|");
			if(null != fileIds && fileIds.length >= ${id}ImageSize){
				return false;
			}
		}
		return true;
	}
	function ${id}GetFileId(url) {
		var location = url.split("/");
		if(location.length > 5){
			return location[5];
		}
		return null;
	}
	function ${id}DelImage(fieldName){
        delete ${id}UrlData[fieldName];
		${id}ImagePreview();
	}
	function ${id}ImagePreview(){
        $("#${id}_image_preview").children().remove();
        var li;
        var index = 0;
        var fileIds = "";
        var urls = "";
        for(var fieldName in ${id}UrlData){
            var data = ${id}UrlData[fieldName];
            fileIds += (index == 0 ? data.fileId : "|" + data.fileId);
            urls += (index == 0 ? data.url : "|" + data.url);
            li = '<li style="width:'+(${id}ViewImageWidth+10)+'px;height:'+(${id}ViewImageHeight+20)+'px;text-align: center;line-height: 110px;position: relative;">';
            li += '<img index="'+index+'" src="'+data.url+'" style="width:'+${id}ViewImageWidth+'px;height:'+${id}ViewImageHeight+'px;border:0;box-shadow: 0px 0px 10px #505050;margin:10px;\">';
            li += '&nbsp;&nbsp;<a href="javascript:" onclick="${id}DelImage(\'' + fieldName + '\');" class="icon-remove" style="position: absolute;top: 15px;right: 5px;"></a></li>';
            $("#${id}_image_preview").append(li);
            index++;
        }
        if ($("#${id}_image_preview").text() == ""){
            $("#${id}_image_preview").html("<li style='list-style:none;padding-top:5px;'>无</li>");
        }

        $('#${id}').val(fileIds);
        $('#${id}_url').val(urls);
	}

	function ${id}GetViewHeight(width,height){
		if(!isNull(width) && !isNull(width)){
			return parseInt(height) / (parseInt(width) / ${id}ViewImageWidth);
		}
		return 200;
	}
</script>
<ul id="${id}_image_preview" style="list-style: none;overflow: hidden;margin: 0 auto;"></ul>
<input type="hidden" id="${id}" name="${name}" value="" />
<input type="hidden" id="${id}_url" value="" />
<input type="button" id="${id}_image_upload_button" class="btn" value="上传图片" />
<span class="help-inline sortable-help${sortable?"":" hide"}">拖动图片可变更排序</span>
<span class="help-inline${widthScale!=""?"":" hide"}">图片宽高比例必须为${widthScale}：${heightScale}</span>
