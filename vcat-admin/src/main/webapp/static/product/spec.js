$(function (){
    // 绑定选择规格属性类
    $('.specItemList li').live("click",function(){
        $(this).addClass("live").siblings().removeClass('live');
        var index = $('.specItemList li').index(this);
        var specItemValueUl = $(".specItemValue ul").hide().eq(index);
        specItemValueUl.show();
        linkCheckAll(specItemValueUl);
        showAddSpecValue('',true);
    });

    // 绑定选择规格属性值
    $('.specItemValueList .specValueLi').live("click",function(){
        if($(this).hasClass("live")){
            $(this).removeClass("live");
        }else{
            $(this).addClass("live");
        }
        linkCheckAll($('.specItemValue ul:visible'));
    });

    // 绑定添加规格属性按钮
    $('#newSpecBtn').bind('click',function(){
        addSpec($('#categoryId').val());
    });

    // 绑定添加规格属性值按钮
    $('#newSpecValueBtn').bind('click',function(){
        addSpecValue();
    });
});

/**
 * 编辑规格或取消编辑
 * @param a
 * @param i
 */
function editSpec(a,i){
    var readOnly = $('.templateItemRow').eq(i).find(":input :eq(0)").attr("readOnly");
    if(readOnly){
        $('.templateItemRow').eq(i).find(":input").removeAttr("readOnly");
        $(a).removeClass("icon-edit");
        $(a).addClass("icon-ban-circle");
    }else{
        $('.templateItemRow').eq(i).find(":input").attr("readOnly",true);
        $(a).removeClass("icon-ban-circle");
        $(a).addClass("icon-edit");
    }
}

/**
 * 添加规格
 * @param categoryId
 * @returns {boolean}
 */
function addSpec(categoryId){
    var name = $("#newSpecTitleText").val();
    var value = $("#newSpecValueText").val();
    if(isNull(name)){
        alertx("规格名称不能为空",function (){
            $("#newSpecTitleText").focus();
        });
        return false;
    }
    if(isNull(value)){
        alertx("规格备选值不能为空",function (){
            $("#newSpecValueText").focus();
        });
        return false;
    }
    value = value.replace(/，/g,",").replace(/\|/g,",");
    doSaveSpec(categoryId,name,value);
}

/**
 * 添加规格
 * @param categoryId
 * @param name
 * @param value
 */
function doSaveSpec(categoryId,name,value){
    value = filterSame(value);
    $.ajax({
        type : 'POST',
        url : ctx + "/ec/product/saveSpec",
        data : {
            "category.id" : categoryId,
            name : name,
            value : value
        },
        success : function(){
            try{
                alertx("添加规格 " + name + " 成功！",function(){
                    buildSpecList(categoryId);
                });
                $('.addSpecUl').hide();
                $('#newSpecTitleText').val('');
                $('#newSpecValueText').val('');
            }catch(e){
                console.log("添加规格 " + name + " 失败："+e.message);
                return doError(e);
            }
        },
        error: function(XMLHttpRequest) {
            return doError(XMLHttpRequest);
        }
    });
}

/**
 * 获取货号
 * @returns 货号
 */
function getSN(){
    var now = new Date();
    return "P" + now.pattern("yyyyMMddHHssmmS");
}

/**
 * 删除规格
 * @param id
 */
function deleteSpecRow(id){
    if($("#itemTable .templateItemRow").length == 1){
        return;
    }
    $("#" + id).remove();
}

/**
 * 展示规格属性选择框
 */
function showSpecDialog(){
    if("" == $('#categoryId').val()){
        alertx("请先选择商品分类！",function(){
            $("#categoryName").trigger("click");
        });
        return false;
    }

    buildSpecList($('#categoryId').val());

    showDialog("specDialog");
}

/**
 * 根据商品分类ID重新构建
 * @param categoryId
 */
function buildSpecList(categoryId){
    $('.specItemList li:not(.addSpecLi)').remove();
    $('.specItemValue ul:not(.addSpecUl)').remove();
    $('.specItemList .live').removeClass("live");
    $('.addSpecUl').hide();
    $.ajax({
        async : false,
        type : 'POST',
        url : ctx + "/ec/product/specName?category.id=" + categoryId,
        success : function(specList){
            try{
                if(specList && specList.length > 0){
                    for(var i in specList){
                        pushSpec(specList[i],i == 0);
                    }
                }
            }catch(e){
                console.log("获取规格属性集合失败："+e.message);
                return doError(e);
            }
        },
        error: function(XMLHttpRequest) {
            return doError(XMLHttpRequest);
        }
    });
    if($(".templateItemRow").length > 0){
        var specNameArray = new Array();
        $('.specTableHead').each(function (){
            specNameArray.push(this.innerHTML);
        });
        if(specNameArray.length > 0){
            $('.specItemValueList li.live').removeClass('live');
            for(var i in specNameArray){
                var specName = specNameArray[i];
                $('.' + specName.toJQS() + 'TD').each(function(){
                    var specValue = $(this).attr("specValue");
                    $('.' + specName.toJQS() + '_' + specValue.toJQS()).addClass("live");
                });
                linkCheckAll($(".specItemValue ul[title=" + specName.toJQS() + "]"));
            }
        }
    }
}

/**
 * 展示单规格属性
 * @param spec 规格属性
 * @param show 是否选中
 */
function pushSpec(spec,selected){
    $(".addSpecLi").before("<li" + (selected ? " class='live'" : "") + " title='" + spec.name + "'>" + abbr(spec.name,22) + "<span class='specNum' title='" + spec.name + "'>[0]</span><i class='icon-trash' onclick='deleteSpec(\""+spec.id+"\",\""+spec.name+"\");' title='删除"+spec.name    +"'></i></li>");
    var itemValueHTML = "<ul class='specItemValueList" + (selected ? "" : " hide") + "' title='" + spec.name + "'>";
    var valueArray = spec.value.split(",");
    for(var i in valueArray){itemValueHTML += "<li specName='" + spec.name + "' title='" + spec.name + '-' + valueArray[i] + "' class='" + spec.name + "_" + valueArray[i] + " specValueLi'>" + valueArray[i] + "</li>";}
    itemValueHTML+="<li class='addSpecValueLi' onclick='showAddSpecValue(\""+spec.id+"\")'>添加新属性</li>";
    itemValueHTML+= "</ul>";
    $(".addSpecUl").before(itemValueHTML);
}

/**
 * 展示添加规格属性值
 * @param specId
 * @param init
 */
function showAddSpecValue(specId,init){
    if(init === true || $('.addSpecValueLabel').is(":visible")){
        $('.addSpecValueLabel').hide();
        $('.specCheckAll').show();
        $('#specValueId').val('');
        $('.addSpecValueLi').html("添加新属性");
    }else{
        $('.addSpecValueLabel').show();
        $('.specCheckAll').hide();
        $('#specValueId').val(specId);
        $('.addSpecValueLi').html("取消添加");
    }
}

/**
 * 添加规格属性值
 * @returns {boolean}
 */
function addSpecValue(){
    var specId = $('#specValueId').val();
    if(isNull(specId)){
        alertx("请选择左侧规格属性！");
        return false;
    }
    var specValue = $("#newSpecValueLabelText").val();
    if(isNull(specValue)){
        alertx("请输入规格属性值！",function (){ $("#newSpecValueLabelText").focus();});
        return false;
    }
    specValue = filterSame(specValue);
    specValue = specValue.replace(/，/g,",").replace(/\|/g,",");
    $.ajax({
        type : 'POST',
        url : ctx + "/ec/product/addSpecValue",
        data : {id : specId,value:specValue},
        success : function(){
            try{
                alertx("添加成功！",function(){
                    buildSpecList($('#categoryId').val());
                    $('#newSpecValueLabelText').val('');
                    $('.addSpecValueLabel').hide();
                    $('.specCheckAll').show();
                });
            }catch(e){
                console.log("添加规格属性值失败："+e.message);
                return doError(e);
            }
        },
        error: function(XMLHttpRequest) {
            return doError(XMLHttpRequest);
        }
    });
}

/**
 * 删除规格
 * @param id
 * @param name
 */
function deleteSpec(id,name){
    confirmx("确认删除 " + name + " ？",function (){
        $.ajax({
            type : 'POST',
            url : ctx + "/ec/product/deleteSpec",
            data : {id : id},
            success : function(){
                try{
                    alertx("删除规格属性 " + name + " 成功！",function(){
                        buildSpecList($('#categoryId').val());
                    });
                }catch(e){
                    console.log("删除规格属性 " + name + " 失败："+e.message);
                    return doError(e);
                }
            },
            error: function(XMLHttpRequest) {
                return doError(XMLHttpRequest);
            }
        });
    });
    window.event.stopPropagation();
}

/**
 * 更新全选按钮状态以及已选规格属性数量
 * @param selectedUl 所选规格属性类
 */
function linkCheckAll(selectedUl){
    var title = selectedUl.attr('title');
    var liveCount = selectedUl.find(".live").length;
    $('#specCheckAll').attr("checked",liveCount > 0 && liveCount == selectedUl.find("li:not(.addSpecValueLi)").length);
    $('.specItemList span[title=' + title.toJQS() + ']').html("[" + liveCount + "]");
}

/**
 * 全选分类规格属性
 * @param check 全选按钮
 */
function checkAllItemValue(check){
    var selectedUl = $(".specItemValue ul").eq($('.specItemList li').index($(".specItemList .live")));
    var specItemListLis = selectedUl.find("li:not(.addSpecValueLi)");
    if(check.checked){
        specItemListLis.addClass("live");
    }else{
        specItemListLis.removeClass("live");
    }
    linkCheckAll(selectedUl);
}

/**
 * 生成规格列表
 */
function saveSpec(){
    var selectedSpec = getSelectSpec();

    if(isNull(selectedSpec)){
        alertx("请选择规格！");
        return;
    }

    buildSpecTable(selectedSpec);
    showDialog("specDialog");
}

/**
 * 获取已选规格属性对象
 * @returns 已选规格属性对象
 */
function getSelectSpec(){
    if($('.specItemValueList li.live').length == 0){
        return null;
    }
    var spec = {};

    $('.specItemValueList li.live').each(function(){
        var specName = $(this).attr('specName');
        if("undefined" == typeof(spec[specName])){
            var specValueArray = new Array();
            specValueArray.push(this.innerHTML);
            spec[specName] = specValueArray;
        }else{
            spec[specName].push(this.innerHTML);
        }
    });

    return spec;
}

/**
 * 根据已选规格属性对象构建规格列表
 * @param selectedSpec
 */
function buildSpecTable(selectedSpec){
    buildSpecTableHead(selectedSpec);
    $('.templateItemRow').remove();
    var titleArray = getTitleArray(selectedSpec);
    var specArray = getSpecArray(selectedSpec);
    var sn = getSN();
    for(var i in specArray){
        $('.lastSpecTr').before(getSpecRowHTML(parseInt(i)+1,sn,specArray[i],titleArray));
    }
    copySpecValueFromFirstRow();
}

/**
 * 为第一行规格绑定事件，填写值时拷贝到其他行
 */
function copySpecValueFromFirstRow(){
    $('#templateItemRow_1 :text:visible').each(function (){
        var input = $(this);
        if(isNull(input.val())){
            input.bind("blur",function(){
                var name = this.name.split('.')[1];
                var value = this.value;
                $('.'+name).val(value);
                $(this).unbind("blur");
            });
        }
    });
}

/**
 * 构建已选规格属性表头
 * @param selectedSpec 已选属性
 */
function buildSpecTableHead(selectedSpec){
    var specNameHTML = "";
    for(var title in selectedSpec){
        specNameHTML += "<th class='specTableHead'>" + title + "</th>";
    }
    $('.specTableHead').remove();
    $('.itemName').remove();
    $(".specNameTitleTH").after(specNameHTML);
}

/**
 * 获取标题数组
 * @param selectedSpec
 * @returns {Array}
 */
function getTitleArray(selectedSpec){
    var titleArray = new Array();
    for(var title in selectedSpec){
        titleArray.push(title);
    }
    return titleArray;
}

/**
 * 获取已选规格属性数组
 * @param selectedSpec 已选属性
 * @returns {Array}
 */
function getSpecArray(selectedSpec){
    var specArray = new Array();
    var titleArray = getTitleArray(selectedSpec);
    selectedSpec.titleIndex = 0;
    eachValue(titleArray,selectedSpec[titleArray[0]],selectedSpec,{},function(value){
        specArray.push($.extend({},value));
    });

    return specArray;
}

/**
 * 动态迭代多维数组
 * @param titleArray 多维数组标题
 * @param valueArray 第一个值数组
 * @param selectedSpec 数组对象(标题为键，数组为值)
 * @param value 当前迭代的值
 * @param func 回调函数
 */
function eachValue(titleArray,valueArray,selectedSpec,value,func){
    var tal = titleArray.length;                    // 获取数组的长度
    for(var valueIndex in valueArray){              // 遍历值数组
        value[titleArray[selectedSpec.titleIndex]] = valueArray[valueIndex];    // 记录该标题所对应的值
        if(selectedSpec.titleIndex < tal - 1){                                  // 判断数组维度是否到最深，如果不是最深，则进入下一个维度继续遍历
            selectedSpec.titleIndex = selectedSpec.titleIndex + 1;              // 维度索引
            eachValue(titleArray,selectedSpec[titleArray[selectedSpec.titleIndex]],selectedSpec,value,func);    // 遍历下一维度数组
        }else if(selectedSpec.titleIndex == tal - 1){                           // 如果当前维度为最深维度，则执行回调函数
            func.call(value,value);
        }
    }
    selectedSpec.titleIndex = selectedSpec.titleIndex - 1;                      // 最深维度遍历完毕，回归上层维度继续遍历
}

/**
 * 生成规格列表
 * @param index 当前规格索引
 * @param sn 货号
 * @param item 规格属性
 * @returns {string}
 */
function getSpecRowHTML(index,sn,item,titleArray){
    sn = sn + "-" + index;
    var trHTML = '<tr id="templateItemRow_' + index + '" class="templateItemRow">';
    trHTML += '<td><input type="text" title="规格货号" name="itemList[' + (index-1) + '].itemSn" maxlength="60" class="itemSn" value="' + sn + '"/></td>';
    var ii = 0;
    var itemName = "";
    for(var t in titleArray){
        var title = titleArray[t];
        trHTML += '<td class="' + title + 'TD" specValue="' + item[title] + '">';
        trHTML += item[title]
        trHTML += '<input type="hidden" name="itemList[' + (index-1) + '].specList[' + (ii) + '].name" value="' + title + '"/>';
        trHTML += '<input type="hidden" name="itemList[' + (index-1) + '].specList[' + (ii) + '].value" value="' + item[title] + '"/>';
        trHTML += '</td>';
        itemName += title + "：" + item[title] + " ";
        ii++;
    }
    if(itemName.length > 0){
        itemName = itemName.substring(0,itemName.length - 1);
    }
    trHTML += '<input type="hidden" name="itemList[' + (index-1) + '].name" value="' + itemName + '"/>';
    trHTML += getItemRowHTML(itemName,index-1,item);
    trHTML += '<td><i class="icon-trash" onclick="deleteSpecRow(\'templateItemRow_' + index + '\')"></i></td>';
    trHTML += '</tr>';
    return trHTML;
}

/**
 * 获取规格行HTML
 * @param itemName
 * @param index
 * @param item
 * @returns {string}
 */
function getItemRowHTML(itemName,index,item){
    var trHTML = "";
    item = getDefault(item,{});
    trHTML += '<td><input type="text" title="商品规格 ' + itemName + ' 零售价" name="itemList[' + index + '].retailPrice" maxlength="13" class="input-mini retailPrice number" value="'+getDefault(item.retailPrice,'')+'"/></td>';
    trHTML += '<td><input type="text" title="商品规格 ' + itemName + ' 进价" name="itemList[' + index + '].purchasePrice" maxlength="13" class="input-mini purchasePrice number" value="'+getDefault(item.purchasePrice,'')+'"/></td>';
    trHTML += '<td><input type="text" title="商品规格 ' + itemName + ' 销售佣金" name="itemList[' + index + '].saleEarning" maxlength="13" class="input-mini saleEarning number" value="'+getDefault(item.saleEarning,'')+'"/></td>';
    trHTML += '<td><input type="text" title="商品规格 ' + itemName + ' 分红"  name="itemList[' + index + '].bonusEarning" maxlength="13" class="input-mini bonusEarning number" value="'+getDefault(item.bonusEarning,'')+'"/></td>';
    trHTML += '<td><input type="text" title="商品规格 ' + itemName + ' 一级团队分红"  name="itemList[' + index + '].firstBonusEarning" maxlength="13" class="input-mini firstBonusEarning number" value="'+getDefault(item.firstBonusEarning,'')+'"/></td>';
    trHTML += '<td><input type="text" title="商品规格 ' + itemName + ' 二级团队分红"  name="itemList[' + index + '].secondBonusEarning" maxlength="13" class="input-mini secondBonusEarning number" value="'+getDefault(item.secondBonusEarning,'')+'"/></td>';
    trHTML += '<td><input type="text" title="商品规格 ' + itemName + ' 平台扣点金额"  name="itemList[' + index + '].point" maxlength="13" class="input-mini point number" value="'+getDefault(item.point,'')+'"/></td>';
    //trHTML += '<td><input type="text" title="商品规格 ' + itemName + ' 可用V猫币"  name="itemList[' + index + '].couponValue" maxlength="13" class="input-mini couponValue number" value="'+getDefault(item.couponValue,'')+'"/></td>';
    trHTML += '<td><input type="text" title="商品规格 ' + itemName + ' 库存" name="itemList[' + index + '].inventory" maxlength="10" class="input-min inventory digits" value="'+getDefault(item.inventory,'')+'"/></td>';
    //trHTML += '<td><input type="text" title="商品规格 ' + itemName + ' 全额抵扣库存" name="itemList[' + index + '].couponAllInventory" maxlength="10" class="input-min couponAllInventory digits" value="'+getDefault(item.couponAllInventory,'')+'"/></td>';
    //trHTML += '<td><input type="text" title="商品规格 ' + itemName + ' 非全额抵扣库存" name="itemList[' + index + '].couponPartInventory" maxlength="10" class="input-min couponPartInventory digits" value="'+getDefault(item.couponPartInventory,'')+'"/></td>';
    trHTML += '<td><input type="text" title="商品规格 ' + itemName + ' 重量" name="itemList[' + index + '].weight" maxlength="13" class="input-mini weight digits" value="'+getDefault(item.weight,'')+'"/></td>';
    return trHTML;
}

/**
 * 验证商品规格是否输入正确
 * @returns {boolean}
 */
function checkSpec(){
    // 验证商品规格
    if($('#itemTable .templateItemRow').length == 0){
        alertx("请添加商品规格！",function(){
            showSpecDialog();
        });
        return false;
    }
    var templateItemRowSucc = true;
    $("#itemTable .templateItemRow").each(function (){
        var templateItemRow = $(this);
        var forNext = true;
        var retailPrice = 0;
        var purchasePrice = 0;
        var saleEarning = 0;
        var bonusEarning = 0;
        var firstBonusEarning = 0;
        var secondBonusEarning = 0;
        var couponValue = 0;
        var point = 0;
        templateItemRow.find("td :input").each(function (){
            var input = $(this);

            if("" == input.val()
                && "text" == input.attr("type")
                //&& !input.hasClass('couponAllInventory')
                //&& !input.hasClass('couponPartInventory')
            ){
                alertx(input.attr("title") + "不能为空！",function(){
                    input.focus();
                });
                templateItemRowSucc = false;
                forNext = false;
                return false;
            }

            if((input.hasClass('retailPrice')
                || input.hasClass('purchasePrice')
                || input.hasClass('saleEarning')
                || input.hasClass('bonusEarning')
                || input.hasClass('firstBonusEarning')
                || input.hasClass('secondBonusEarning')
                || input.hasClass('point')
                //|| input.hasClass('couponValue')
                )
                && !isDecimal(input.val())){
                alertx(input.attr("title") + "格式不正确！",function(){
                    input.focus();
                });
                templateItemRowSucc = false;
                forNext = false;
                return false;
            }

            if(input.hasClass('retailPrice')){
                retailPrice = parseFloat(input.val());
            }
            if(input.hasClass('purchasePrice')){
                purchasePrice = parseFloat(input.val());
            }
            if(input.hasClass('saleEarning')){
                saleEarning = parseFloat(input.val());
            }
            if(input.hasClass('bonusEarning')){
                bonusEarning = parseFloat(input.val());
            }
            if(input.hasClass('firstBonusEarning')){
                firstBonusEarning = parseFloat(input.val());
            }
            if(input.hasClass('secondBonusEarning')){
                secondBonusEarning = parseFloat(input.val());
            }
            //if(input.hasClass('couponValue')){
            //    couponValue = parseFloat(input.val());
            //}
            if(input.hasClass('point')){
                point = parseFloat(input.val());
            }

            if((input.hasClass('inventory')
                //|| input.hasClass('couponAllInventory')
                //|| input.hasClass('couponPartInventory')
                || input.hasClass('weight'))
                && !isNumber(input.val())){
                alertx(input.attr("title") + "格式不正确！",function(){
                    input.focus();
                });
                templateItemRowSucc = false;
                forNext = false;
                return false;
            }
        });
        if(!forNext){return forNext;}
        if(retailPrice != (purchasePrice + saleEarning + bonusEarning + firstBonusEarning + secondBonusEarning + point
            //+ couponValue
            ).toFixed(5)){
            alertx("销售价 必须等于 (结算价 + 佣金 + 分红 + 一级团队分红 + 二级团队分红 + 扣点)！",function(){
                templateItemRow.find('.retailPrice').focus();
            });
            forNext = false;
            templateItemRowSucc = false;
            return templateItemRowSucc;
        }
        templateItemRowSucc = forNext;
        return forNext;
    });
    return templateItemRowSucc;
}

/**
 * 过滤相同字符串
 * @param str
 * @returns {string}
 */
function filterSame(str){
    var ar2 = str.split(",");
    var array = new Array();
    var j=0;
    for(var i=0;i<ar2.length;i++){
        if((array == "" || array.toString().match(new RegExp(ar2[i],"g")) == null)&&ar2[i]!=""){
            array[j] =ar2[i];
            j++;
        }
    }
    return array.toString();
}