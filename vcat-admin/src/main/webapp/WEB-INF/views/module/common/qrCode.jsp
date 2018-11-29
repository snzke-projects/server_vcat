<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script type="text/javascript">
    function showQRCode(title,content){
        content = encodeURIComponent(content);
        loading("请稍等...");
        $('#qrCodeTitle').html(title);
        $.get(ctx + "/common/getQRCodeImagePath?content=" + content,function(image){
            $('#qrCodeImage').attr("src", "${pageContext.request.contextPath}" + image);
            closeTip();
            showDialog("qrCodeDialog");
        });
    }
</script>
<div id="qrCodeDialog" class="vcat-dialog" style="width: 50%">
    <div class="panel panel-info">
        <div class="panel-heading">
            <div class="panel-title" style="font-size: 14px;">
                <span id="qrCodeTitle"></span> 二维码
                <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('qrCodeDialog')"></a>
            </div>
        </div>
        <div class="panel-body" style="text-align:center;">
            <img id="qrCodeImage" alt="链接二维码">
        </div>
    </div>
</div>