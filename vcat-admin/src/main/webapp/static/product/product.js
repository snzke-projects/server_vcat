var propertyPropertyIndex;
$(function() {
    $("#name").focus();
    if($("#id").length > 0){
        $(".templateItemRow :input").attr("readOnly",true);
    }
    $("#inputForm").validate({
        submitHandler: function(form){
            descriptionEditor.sync();
            if(validateForm()){
                $(".templateItemRow :input").attr("readOnly",false);
                var allSpecCouponAllInventory = 0;
                var allSpecCouponPartInventory = 0;
                $('.couponAllInventory').each(function () {
                    allSpecCouponAllInventory += parseInt(getDefault($(this).val(), 0));
                });
                $('.couponPartInventory').each(function () {
                    allSpecCouponPartInventory += parseInt(getDefault($(this).val(), 0));
                });
                $("#couponAllUsable").val(allSpecCouponAllInventory > 0 ? 1 : 0);
                $("#couponPartUsable").val(allSpecCouponPartInventory > 0 ? 1 : 0);

                function updateNameIndex(index,input){
                    input = $(input);
                    input.attr("name",input.attr('name').replace(/\[\d+\]/g,'[' + index + ']'));
                }

                $('#propertyUl :input[name*=id]').each(updateNameIndex);
                $('#propertyUl :input[name*=value]').each(updateNameIndex);

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
    propertyPropertyIndex = 0 + $('.property').length;
});
function validateForm(){
    // 验证商品基本信息
    if(isNull($("#name").val())){
        alertx("商品名称不能为空！",function(){
            $("#name").focus();
        });
        return false;
    }
    if(!isInteger($("#displayOrder").val())){
        alertx("商品排序输入不正确！！",function(){
            $("#displayOrder").focus();
        });
        return false;
    }

    if(isNull($("#categoryId").val())){
        alertx("商品分类不能为空！",function(){
            $("#categoryName").trigger("click");
        });
        return false;
    }
    if(isNull($("#brand").val())){
        alertx("商品所属品牌不能为空！",function(){
            $("#brand").select2("open");
        });
        return false;
    }
    if("1" == $("input:radio[name=isAutoLoad]:checked").val() && isNull($('#autoLoadDate').val())){
        alertx("自动上架时间不能为空！",function(){
            $("#autoLoadDate").trigger("click");
        });
        return false;
    }

    if("1" == $("input:radio[name=isAutoLoad]:checked").val() && !isNull($('#autoLoadDate').val())
        && new Date().getTime() >= new Date($('#autoLoadDate').val().replace(/-/g,"/")).getTime()){
        alertx("自动上架时间必须大于当前时间！",function(){
            $("#autoLoadDate").trigger("click");
        });
        return false;
    }

    if("1" == $("input:radio[name=isHot]:checked").val() && !isNull($('#reserveRecommendStartTime').val())){
        alertx("当前商品是预购活动商品，不能设置热销！",function(){
            $("input:radio[name='isHot'][value='0']").focus();
        });
        return false;
    }

    if("0" == $('input:radio[name=freeShipping]:checked').val()
        && isNull($('#expressTemplate').val())){
        alertx("请选择邮费模板！",function(){
            $("#expressTemplate").select2('open');
        });
        return false;
    }

    if(isNull(descriptionEditor.text())){
        alertx("商品图文详情不能为空！",function(){
            descriptionEditor.focus();
            $("body").animate({scrollTop:$('#description').prev().offset().top},300);
        });
        return false;
    }

    if(!checkSpec()){return false;};

    if(isNull($("#productImages").val())){
        alertx("请至少上传一张商品图片",function(){
            $("#productImages_image_upload_button").trigger("click");
        });
        return false;
    }

    $("#lef").val(getDefault($("#lef").val(),0));
    $("#lecf").val(getDefault($("#lecf").val(),0));

    var lef = $("#lef").val();
    var lecf = $("#lecf").val();

    if(!isMoney(lef)){
        alertx("上架奖励金额格式不正确",function(){
            $("#lef").focus();
        });
        return false;
    }

    if(!isMoney(lecf)){
        alertx("上架奖励额度格式不正确",function(){
            $("#lecf").focus();
        });
        return false;
    }

    if(lecf != 0 && parseFloat(lef) >= parseFloat(lecf)){
        alertx("上架奖励金额不能大于等于上架奖励额度",function(){
            $("#lef").focus();
        });
        return false;
    }

    return true;
}

function newProductProperty(propertyId,propertyName){
    var buildNewProductProperty = true;
    $(".propertyLabel").each(function (){
        if($(this).html().indexOf(propertyName) == 0){
            buildNewProductProperty = false;
            return false;
        }
    });
    if(buildNewProductProperty){
        var html = "<div class=\"control-group property_"+propertyPropertyIndex+"\">";
        html += "<label class=\"control-label propertyLabel\">"+propertyName+"：</label>";
        html += "<div class=\"controls\">";
        html += "<input type=\"hidden\" name=\"propertyList["+(propertyPropertyIndex)+"].id\" value=\""+propertyId+"\">";
        html += "<input class=\"required input-large\" type=\"text\" name=\"propertyList["+propertyPropertyIndex+"].value\" maxlength=\"100\">";
        html += '<span class="icon-remove" onclick="$(\'.property_'+propertyPropertyIndex+'\').remove();" style="margin-left: 9px;"></span>';
        html += "</div>";
        html += "</div>";
        $("#propertyUl").append(html);
        propertyPropertyIndex++;
    }
}

function categoryTreeselectCallBack(v, h, f){
    var categoryId = $("#categoryId").val();
    getPropertyListHTMLByCategoryId(categoryId);
}

function getPropertyListHTMLByCategoryId(categoryId){
    if("" != categoryId){
        $(".property").remove();
    }
    $.ajax({
        async : false,
        type : 'POST',
        url : ctx + "/ec/property/listData?categoryId="+categoryId,
        success : function(propertyList){
            try{
                if(propertyList && propertyList.length > 0){
                    for(var i in propertyList){
                        newProductProperty(propertyList[i].id,propertyList[i].name);
                    }
                }
            }catch(e){
                console.log("获取属性集合失败："+e.message);
                return doError(e);
            }
        },
        error: function(XMLHttpRequest) {
            return doError(XMLHttpRequest);
        }
    });
}
function checkIsAutoLoad(radio){
    if(radio.value == '1'){
        $('.autoLoad').show("fast");
    }else{
        $(":radio[name=isAutoLoad][value='0']").attr("checked",true);
        $('.autoLoad').hide("fast");
        $('#autoLoadDate').val("");
        $('.autoLoadDate').hide("fast");
    }
}
function checkIsHotSale(radio){
    if(radio.value == '1'){
        $('.hotSalePanel').show("fast");
    }else{
        $(":radio[name=isHotSale][value='0']").attr("checked",true);
        $('.hotSalePanel').hide("fast");
    }
}

function checkAutoLoad(radio){
    if(radio.value == '1'){
        $('.autoLoadDate').show("fast");
    }else{
        $('#autoLoadDate').val("");
        $('.autoLoadDate').hide("fast");
    }
}

function showAddCategory(){
    top.$.jBox.open("iframe:"+ctx+"/ec/category/form?sqlMap['onlyAdd']=true", "添加商品分类",$(top.document).width()-220,$(top.document).height()-180,{
        buttons: {"确定": true},
        loaded: function () {
            $(".jbox-content", top.document).css("overflow-y", "hidden");
        }
    });
}

function showAddSupplier(){
    top.$.jBox.open("iframe:"+ctx+"/ec/supplier/brandForm?sqlMap['onlyAdd']=true", "添加品牌",$(top.document).width()-220,$(top.document).height()-180,{
        buttons: {"确定": true},
        loaded: function () {
            $(".jbox-content", top.document).css("overflow-y", "hidden");
        }
        ,closed:function (){
            refreshBrandList();
        }
    });
}

function refreshBrandList(){
    $.ajax({
        type : 'POST',
        url : ctx + "/ec/supplier/getBrandList",
        success : function(brandList){
            try{
                if(brandList && brandList.length > 0){
                    var selectedBrandId = $('#brand option:selected').val();
                    $('#brand option:not(:first)').remove();
                    var html = "";
                    for(var i in brandList){
                        html += '<option value="'+brandList[i].id+'"';
                        if(brandList[i].id == selectedBrandId){
                            html += ' selected';
                        }
                        html += '>'+brandList[i].name+'</option>';
                    }
                    $('#brand').append(html);
                    $('#brand').select2();
                }
            }catch(e){
                console.log("获取品牌失败："+e.message);
                return doError(e);
            }
        },
        error: function(XMLHttpRequest) {
            return doError(XMLHttpRequest);
        }
    });
}
function refreshItemChangeLogList(itemId,itemName){
    top.$.jBox.open("iframe:" + ctx + "/ec/product/findItemChangeLog?associationId=" + itemId, itemName + " 规格变更日志记录",$(top.document).width()-220,$(top.document).height()-180,{
        buttons: {"返回": true},
        loaded: function () {
            $(".jbox-content", top.document).css("overflow-y", "hidden");
        }
    });
}
function checkFreeShipping(freeShipping){
    if(freeShipping.value == '0'){
        $('.expressTemplate').show("fast");
    }else{
        $('#expressTemplate').val("");
        $('.expressTemplate').hide("fast");
    }
}
function checkVirtual(virtual){
    if(virtual.value == '0'){
        $('input:radio[name="freeShipping"][value="0"]').trigger('click');
        $('.freeShipping').show("fast");
        $('.expressTemplate').show("fast");
    }else{
        $('#expressTemplate').val("");
        $('input:radio[name="freeShipping"][value="1"]').trigger('click');
        $('.freeShipping').hide("fast");
        $('.expressTemplate').hide("fast");
    }
}