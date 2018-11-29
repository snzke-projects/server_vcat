<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>设置团购</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        var isNew = '${groupBuying.isNewRecord}' === 'true';
        var couldEdit = isNew || '${groupBuying.status}' === '0';
        var loadNormalItem = '${groupBuying.isNewRecord}' === 'true';
        $(function() {
            $("#name").focus();
            $("#inputForm").validate({
                submitHandler: function(form){
                    if(!checkForm())return;
                    if(!initItemInput())return;
                    loading('正在提交，请稍等...');
                    form.submit();
                },
            });
            $('.itemTable').datagrid(getDatagridOption({
                pagination:false,
                idField:'id',
                checkOnSelect:false,
                selectOnCheck:false,
                columns:[[
                    {field:'product.name',title:'商品名称',width:300,formatter: function(value,row){
                        return abbr(row.product.name,50);
                    }},
                    {field:'name',title:'规格名称',width:100,editor:'text'},
                    {field:'retailPrice',title:'售价',width:80
                        <c:if test="${groupBuying.isNewRecord || groupBuying.status == 0}">
                        ,editor:'text'
                        </c:if>
                    },
                    {field:'purchasePrice',title:'结算价',width:80
                        <c:if test="${groupBuying.isNewRecord || groupBuying.status == 0}">
                        ,editor:{type:'numberbox',options:{precision:2}}
                        </c:if>
                    },
                    {field:'saleEarning',title:'佣金',width:80
                        <c:if test="${groupBuying.isNewRecord || groupBuying.status == 0}">
                        ,editor:{type:'numberbox',options:{precision:2}}
                        </c:if>
                    },
                    {field:'bonusEarning',title:'分红',width:80
                        <c:if test="${groupBuying.isNewRecord || groupBuying.status == 0}">
                        ,editor:{type:'numberbox',options:{precision:2}}
                        </c:if>
                    },
                    {field:'firstBonusEarning',title:'一级团队分红',width:80
                        <c:if test="${groupBuying.isNewRecord || groupBuying.status == 0}">
                        ,editor:{type:'numberbox',options:{precision:2}}
                        </c:if>
                    },
                    {field:'secondBonusEarning',title:'二级团队分红',width:80
                        <c:if test="${groupBuying.isNewRecord || groupBuying.status == 0}">
                        ,editor:{type:'numberbox',options:{precision:2}}
                        </c:if>
                    },
                    {field:'point',title:'扣点',width:80
                        <c:if test="${groupBuying.isNewRecord || groupBuying.status == 0}">
                        ,editor:{type:'numberbox',options:{precision:2}}
                        </c:if>
                    },
                    {field:'inventory',title:'库存',width:80,editor:{type:'numberbox'}}
                ]],
                onClickCell:function (index, field){
                    $('.itemTable').datagrid('beginEdit', index);
                    $(".itemTable").datagrid("unselectRow", index);
                },
                onDblClickCell:function (index, field){
                    $('.itemTable').datagrid('endEdit', index);
                    $(".itemTable").datagrid("unselectRow", index);
                }
            }));
            if(!couldEdit){
                $('#productIdCallbackButton').hide();
                $('.icon-trash').hide();
                $(':radio').attr('readonly',true);
                $('#times').attr('readonly',true);
                $('#neededPeople').attr('readonly',true);
                $('#period').attr('readonly',true);
            }
        });
        function checkForm(){
            if($(":radio[name=isLimit]:checked").val() == 1
                    && isNull($('#times').val())){
                alertx('限购数量不能为空',function (){
                    $('#times').focus();
                });
                return false;
            }
            var period = $('#period').val();
            if(parseInt(period) < 1){
                alertx('拼团周期必须大于0',function (){
                    $('#period').focus();
                });
                return false;
            }
            var now = new Date();
            var endDate = new Date($('#endDate').val().replace(/-/g,"/"));
            var oldEndDate = new Date($('#oldEndDate').val().replace(/-/g,"/"));

            if((isNull($('#oldEndDate').val()) || endDate < oldEndDate)
                    && (now.addDays(parseInt(period)).getTime() > endDate.getTime())){
                alertx('结束时间必须大于当前时间 + 拼团周期：' + now.pattern('yyyy-MM-dd HH:mm'),function (){
                    $('#endDate').focus();
                },null, 500);
                return false;
            }
            return true;
        }
        function initItemInput(){
            var rows = $('.itemTable').datagrid('getRows');
            if(rows && rows.length == 1){
                $('.itemTable').datagrid('endEdit', 0);
                var row = rows[0];

                var retailPrice = parseFloat(row.retailPrice);
                var purchasePrice = parseFloat(row.purchasePrice);
                var saleEarning = parseFloat(row.saleEarning);
                var bonusEarning = parseFloat(row.bonusEarning);
                var firstBonusEarning = parseFloat(row.firstBonusEarning);
                var secondBonusEarning = parseFloat(row.secondBonusEarning);
                var point = parseFloat(row.point);
                var sumRetailPrice = purchasePrice.add(saleEarning).add(bonusEarning).add(firstBonusEarning).add(secondBonusEarning).add(point);

                if(retailPrice != sumRetailPrice){
                    alertx("销售价 必须等于 (结算价 + 佣金 + 分红 + 一级团队分红 + 二级团队分红 + 扣点)！");
                    return false;
                }

                $('#inputForm').append('<input type="hidden" name="productItem.itemSn" value="' + row.itemSn + '">');
                $('#inputForm').append('<input type="hidden" name="productItem.name" value="' + row.name + '">');
                $('#inputForm').append('<input type="hidden" name="productItem.purchasePrice" value="' + row.purchasePrice + '">');
                $('#inputForm').append('<input type="hidden" name="productItem.retailPrice" value="' + row.retailPrice + '">');
                $('#inputForm').append('<input type="hidden" name="productItem.saleEarning" value="' + row.saleEarning + '">');
                $('#inputForm').append('<input type="hidden" name="productItem.bonusEarning" value="' + row.bonusEarning + '">');
                $('#inputForm').append('<input type="hidden" name="productItem.firstBonusEarning" value="' + row.firstBonusEarning + '">');
                $('#inputForm').append('<input type="hidden" name="productItem.secondBonusEarning" value="' + row.secondBonusEarning + '">');
                $('#inputForm').append('<input type="hidden" name="productItem.inventory" value="' + row.inventory + '">');
                $('#inputForm').append('<input type="hidden" name="productItem.point" value="' + row.point + '">');
                $('#inputForm').append('<input type="hidden" name="productItem.weight" value="' + row.weight + '">');
            }
            return true;
        }
        function setItemData(product){
            if(product && product.isHot){
                alertx('请取消商品热销标识后再选择！');
                productIdClearProductList();
                return;
            }
            if(loadNormalItem){
                updateTable(product.id);
            }else{
                loadNormalItem = true;
            }
        }
        function checkIsLimit(radio){
            if(radio.value == '1'){
                $('.times').show("fast");
            }else{
                $(":radio[name=isLimit][value='0']").attr("checked",true);
                $('.times').hide("fast");
            }
        }
        function updateTable(productId){
            $.get(ctx + "/ec/product/getProductItemList?id=" + productId + "&sqlMap['productItemType']=0",function(selectedProductItemArray){
                if(selectedProductItemArray && selectedProductItemArray.length > 0){
                    if(selectedProductItemArray.length > 1){
                        alertx('只能选择单规格商品！',function (){
                            $('#productIdCallbackButton').trigger('click');
                        });
                        return false;
                    }
                    $('#singlePrice').val(selectedProductItemArray[0].retailPrice);
                    $('.itemTable').datagrid('loadData', { total: selectedProductItemArray.length, rows: selectedProductItemArray});
                }else{
                    $('.itemTable').datagrid('loadData', { total: 0, rows: []});
                }
            });
            checkAnother(productId);
        }
        function checkAnother(productId){
            $.get(ctx + "/ec/groupBuying/getAnother?id=" + $('#id').val() + "&product.id=" + productId,function(another){
                if(another){
                    $('.itemTable').datagrid('loadData', { total: 0, rows: []});
                    alertx('该商品已参加其他团购，请重新选择！',function (){
                        $('#productIdCallbackButton').trigger('click');
                    });
                    return false;
                }
            });
        }
    </script>
</head>
<body>
<form:form id="inputForm" modelAttribute="groupBuying" action="${ctx}/ec/groupBuying/save" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="status"/>
    <form:hidden path="limitId"/>
    <form:hidden path="singlePrice"/>
    <input type="hidden" id="oldProductId" name="sqlMap['oldProductId']" value="${groupBuying.product.id}"/>
    <input type="hidden" id="oldProductItemId" name="sqlMap['oldProductItemId']" value="${groupBuying.productItem.id}"/>
    <input type="hidden" id="oldEndDate" value="<fmt:formatDate value="${groupBuying.endDate}" pattern="yyyy-MM-dd HH:mm"/>"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">团购名称：</label>
        <div class="controls">
            <form:input path="name" maxlength="20" cssClass="required input-large"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">团购商品：</label>
        <div class="controls">
            <sys:productSelect id="productId" name="product.id" value="${groupBuying.product.id}" callback="setItemData" queryParam="type=sj&archived=0"/>
            <script type="text/javascript">
                $(function() {
                    if(!isNew){
                        $('.icon-trash').hide();
                        $.get(ctx + "/ec/product/getProductItem?id=${groupBuying.productItem.id}",function(item){
                            $('.itemTable').datagrid('loadData', { total: 1, rows: [item] });
                        });
                    }
                });
            </script>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">结束时间：</label>
        <div class="controls">
            <input id="endDate" type="text" name="endDate" value="<fmt:formatDate value="${groupBuying.endDate}" pattern="yyyy-MM-dd HH:mm"/>"
                   readonly="readonly" maxlength="16" class="input-date Wdate required" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">是否限购：</label>
        <div class="controls">
            <div class="isLimit${!groupBuying.isNewRecord && groupBuying.status != 0 ? ' hide' :''}">
                <form:radiobuttons path="isLimit" items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" cssClass="required" onclick="checkIsLimit(this)" />
            </div>
            <c:if test="${!groupBuying.isNewRecord && groupBuying.status != 0}">
                <span>${groupBuying.isLimit ? "是" : "否"}</span>
            </c:if>
        </div>
    </div>
    <div class="control-group times${groupBuying.isLimit ? "" : " hide"}">
        <label class="control-label">限购数量：</label>
        <div class="controls">
            <form:input path="times" maxlength="50" cssClass="input-medium digits"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">拼团人数：</label>
        <div class="controls">
            <form:input path="neededPeople" maxlength="50" cssClass="required input-medium digits"/>
            <span class="help-inline">设置几人成团</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">拼团周期：</label>
        <div class="controls">
            <form:input path="period" maxlength="50" cssClass="required input-medium digits"/>
            <span class="help-inline">单位：天</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">团购排序：</label>
        <div class="controls">
            <form:input path="displayOrder" maxlength="50" cssClass="required input-medium digits"/>
            <span class="help-inline">数字越大越靠前</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">团购商品规格：</label>
        <div class="controls">
            <table class="itemTable" style="width: auto;margin-top: 5px;"></table>
        </div>
    </div>
    <div class="form-actions" >
        <shiro:hasPermission name="ec:groupBuying:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>