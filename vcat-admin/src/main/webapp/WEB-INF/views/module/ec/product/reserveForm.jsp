<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>设置商品预购</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        var isNew = 'true' === '${reserve.isNewRecord}'
        $(function() {
            $("#inputForm").validate({
                submitHandler: function(form){
                    var productIds = $('#product').val();
                    if(isNull(productIds)){
                        alertx('请选择预购商品！',function (){
                            $('#productCallbackButton').trigger("click");
                        });
                        return false;
                    }
                    loading('正在提交，请稍等...');
                    var id = $('#id').val();

                    function readyToSubmit(){
                        if(!isNull(id)){
                            form.submit();
                        }else {
                            var reserveProductNameLabel = getExistsReserve(productIds);
                            if(reserveProductNameLabel != ""){
                                alertx("<p>商品：</p><p style='color: red;'>" + reserveProductNameLabel +"</p><p>包含<span style='color: red;'>未结束的预购活动</span>，保存失败。</p>",function(){closeTip();},600);
                            }else {
                                form.submit();
                            }
                        }
                    }

                    var hotProductNameLabel = "";
                    var hotRecommendId = "";
                    var productList = getExistsHotProductList(productIds);
                    for(var i = 0; i < productList.length; i++){
                        hotProductNameLabel += ((i > 0 ? "</br>　　" : "　　") + productList[i].name);
                        hotRecommendId += ((i > 0 ? "|" : "") + productList[i].hotRecommendId);
                    }
                    if(hotProductNameLabel != ""){
                        confirmx("<p>商品：</p><p style='color: red;'>" + hotProductNameLabel +"</p><p>是<span style='color: red;'>热销商品</span>，点击【确定】将取消该商品热销标识。</p>",function (){
                            $('#hotRecommendId').val(hotRecommendId);
                            readyToSubmit();
                        },function(){closeTip();},600);
                    }else{
                        readyToSubmit();
                    }
                }
            });
        });

        /**
         * 获取预购商品名称
         * @param productIds
         * @returns {string}
         */
        function getExistsReserve(productIds){
            var productNameLabel = "";
            $.ajax({
                async : false,
                type : 'GET',
                url : ctx + "/ec/product/getExistsReserve?productIds=" + productIds,
                success : function(productList){
                    if(productList && productList.length > 0){
                        for(var i = 0; i < productList.length; i++){
                            productNameLabel += ((i > 0 ? "</br>　　" : "　　") + productList[i].name);
                        }
                    }
                }
            });
            return productNameLabel;
        }

        /**
         * 获取热销商品名称
         * @param productIds
         * @returns {string}
         */
        function getExistsHotProductList(productIds){
            var productList = "";
            $.ajax({
                async : false,
                type : 'GET',
                url : ctx + "/ec/product/getExistsHot?productIds=" + productIds,
                success : function(productListResult){
                    productList = productListResult;
                }
            });
            return productList;
        }
    </script>
</head>
<body>
<form:form id="inputForm" modelAttribute="reserve" action="${ctx}/ec/product/saveReserve" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="sqlMap['hotRecommendId']" id="hotRecommendId"/>
    <sys:message content="${message}"/>
    <div class="panel panel-info">
        <div class="panel-heading">
            <div class="panel-title" style="font-size: 14px;">
                设置预购商品
            </div>
        </div>
        <div class="panel-body">
            <div class="tab-content">
                <div class="control-group">
                    <label class="control-label">预购商品：</label>
                    <div class="controls">
                        <c:if test="${reserve.isNewRecord}">
                            <sys:productSelect id="product" name="sqlMap['productIds']" radio="false"></sys:productSelect>
                        </c:if>
                        <c:if test="${!reserve.isNewRecord}">
                            <input id="product" name="sqlMap['productIds']" value="${reserve.product.id}" type="hidden"/>
                            <a href="javascript:void(0)" onclick="showViewPage(ctx + '/ec/product/form?id=${reserve.product.id}&isView=true','${reserve.product.name} 商品详情')">${reserve.product.name}</a>
                        </c:if>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">预售开始时间：</label>
                    <div class="controls">
                        <input type="text" id="startTime" name="startTime" value="<fmt:formatDate value="${reserve.startTime}" pattern="yyyy-MM-dd HH:mm"/>" readonly maxlength="16" class="input-date required Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">预售结束时间：</label>
                    <div class="controls">
                        <input type="text" id="endTime" name="endTime" value="<fmt:formatDate value="${reserve.endTime}" pattern="yyyy-MM-dd HH:mm"/>" readonly maxlength="16" class="input-date required Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
                    </div>
                </div>
            </div>
            <div class="form-actions">
                <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>
                <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
            </div>
        </div>
    </div>
</form:form>
</body>
</html>