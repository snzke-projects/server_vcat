<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="id" type="java.lang.String" required="true" description="上传inputID"%>
<%@ attribute name="name" type="java.lang.String" required="false" description="上传的Name"%>
<%@ attribute name="value" type="java.lang.String" required="false" description="已上传的图片地址集合(每张图片ID以|分割)"%>
<%@ attribute name="widthScale" type="java.lang.Integer" required="false" description="图片宽度比例" %>
<%@ attribute name="heightScale" type="java.lang.Integer" required="false" description="图片高度度比例 如同时传入高宽比例，则限制图片高宽比例" %>
<script type="text/javascript">include('kindeditor_lib','${ctxStatic}/kindeditor/',['kindeditor-all-min.js']);</script>
<script type="text/javascript">include('kindeditor_lang','${ctxStatic}/kindeditor/lang/',['zh_CN.js']);</script>
<link href="${ctxStatic}/kindeditor/themes/default/default.css" type="text/css" rel="stylesheet" />
<script type="text/javascript">
	var ${id}ViewImageWidth = 200;
	var ${id}ViewImageHeight = ${id}BuildWidthAndHeight('${widthScale}','${heightScale}');
	KindEditor.ready(function(K) {
        K.DEBUG = true;         // 打开DEBUG模式，可显示错误提示
        if(""!="${widthScale}"){
            $('#${id}_img').css("width",${id}ViewImageWidth+"px");
        }else{
            $('#${id}_img').css("maxWidth","200px");
        }
        if(""!="${heightScale}"){
            $('#${id}_img').css("height",${id}ViewImageHeight+"px");
        }else{
            $('#${id}_img').css("maxHeight","500px");
        }
        ${id}ReadyImg('${value}');
		var editor = K.editor({
			allowFileManager : false,
            imageSizeLimit : getSizeLabel('${fns:getDictSingleValue('ec_image_size_limit', 500)}'),
			uploadJson : '${ctx}/kindeditor/upload?widthScale=${widthScale}&heightScale=${heightScale}'
		});
		K('#${id}_image_upload_button').click(function() {
			editor.loadPlugin('image', function() {
				editor.plugin.imageDialog({
					clickFn : function(url, title, width, height, border, align) {
                        ${id}ReadyImg(url);
						editor.hideDialog();
					}
				});
			});
		});
	});
    function ${id}ReadyImg(url){
        $('#${id}_img').attr('src',url);
        $('#${id}').val(${id}GetFileId(url));
        $('#${id}_url').val(url);
        if(!isNull(url)){
            $('#${id}_image_clear_button').show();
        }
    }
	function ${id}GetFileId(url) {
		var location = url.split("/");
		if(location.length > 5){
			return location[5];
		}
		return null;
	}
	function ${id}BuildWidthAndHeight(width,height){
		if(!isNull(width) && !isNull(width)){
			return parseInt(height) / (parseInt(width) / ${id}ViewImageWidth);
		}
		return 200;
	}
    function ${id}ClearImg(){
        $('#${id}_img').attr('src','');
        $('#${id}').val('');
        $('#${id}_url').val('');
        $('#${id}_image_clear_button').hide();
    }
</script>
<div>
    <ul id="${id}_image_preview" style="list-style: none;overflow: hidden;margin: 0 auto;">
        <li style="width: 210px;height: 220px;text-align: center;line-height: 110px;position: relative;">
            <img id="${id}_img" src="${value}" alt="请上传一张图片" style="width:200px;height:200px;border:0;box-shadow: 0px 0px 10px #505050;margin:10px;">
        </li>
    </ul>
    <input type="hidden" id="${id}" name="${name}" />
    <input type="hidden" id="${id}_url" value="${value}" />
    <input type="button" id="${id}_image_upload_button" class="btn" value="上传图片" />
    <input type="button" id="${id}_image_clear_button" class="btn hide" value="清除图片" onclick="${id}ClearImg()" />
    <span class="help-inline${widthScale!=""?"":" hide"}">图片宽高比例必须为${widthScale}：${heightScale}</span>
</div>