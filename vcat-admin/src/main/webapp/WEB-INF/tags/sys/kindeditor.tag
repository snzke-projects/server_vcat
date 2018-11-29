<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="replace" type="java.lang.String" required="true" description="需要替换的textarea编号"%>
<%@ attribute name="height" type="java.lang.String" required="false" description="编辑器高度"%>
<%@ attribute name="width" type="java.lang.String" required="false" description="编辑器高度"%>
<script type="text/javascript">include('kindeditor_lib','${ctxStatic}/kindeditor/',['kindeditor.js']);</script>
<script type="text/javascript">
	var ${replace}Editor = null;
	KindEditor.ready(function(K) {
        K.DEBUG = true;
		${replace}Editor = K.create('#${replace}', {
                uploadJson : '${ctx}/kindeditor/upload',
                height: '${height}',
                width: '${width},',
                allowFileManager : false,
                filterMode: false,
            	items : [
                    'source', '|', 'undo', 'redo', '|', 'preview', 'print', 'template', 'code', 'cut', 'copy', 'paste',
                    'plainpaste', 'wordpaste', '|', 'justifyleft', 'justifycenter', 'justifyright',
                    'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
                    'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 'fullscreen', '/',
                    'formatblock', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold',
                    'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image','multiimage',
                    'insertfile', 'table', 'hr', 'emoticons', 'baidumap', 'pagebreak',
                    'anchor', 'link', 'unlink'
                ]
        });
	});
</script>