<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>邮费模板管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        var selectedDistrictObject = {};
		$(function() {
			$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
                    if(!checkForm())return;
                    if(buildCityList())return;
                    loading('正在提交，请稍等...');
                    form.submit();
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
            if('${expressTemplate.valuationMethod}' == ''){
                $('.valuationMethodUl li:eq(0)').trigger('click');
            }
            refreshDistrict(0,100000);
            if(!isNull('${expressTemplate.province}')){
                $('#province').val('${expressTemplate.province}');
                $('#province').trigger("change");
            }
            if(!isNull('${expressTemplate.city}')){
                $('#city').val('${expressTemplate.city}');
                $('#city').trigger("change");
            }
            if(!isNull('${expressTemplate.district}')){
                $('#district').val('${expressTemplate.district}');
                $('#district').trigger("change");
            }
<c:forEach items="${expressTemplate.freightConfigList}" var="freight" varStatus="index">
    <c:if test="${index.index > 0}">
            selectedDistrictObject[${index.index}] = {}
        <c:forEach items="${freight.cityList}" var="city" varStatus="cityIndex">
            var p_${index.index}_${city.parent.id} = initConfigProvince('${index.index}','${city.parent.id}');
            p_${index.index}_${city.parent.id}.array.push({id:'${city.id}',name:'${city.name}'});
            p_${index.index}_${city.parent.id}.selectAll = p_${index.index}_${city.parent.id}.sourceArray.length == p_${index.index}_${city.parent.id}.array.length;
        </c:forEach>
    </c:if>
</c:forEach>
		});
        function initConfigProvince(configIndex,provinceId){
            if(!selectedDistrictObject[configIndex][provinceId]){
                selectedDistrictObject[configIndex][provinceId] = {};
                selectedDistrictObject[configIndex][provinceId].array = new Array();
                selectedDistrictObject[configIndex][provinceId].selectAll = false;
                selectedDistrictObject[configIndex][provinceId].sourceArray = getAreaList(provinceId);
            }
            return selectedDistrictObject[configIndex][provinceId];
        }
        function checkForm(){
            if(isNull($('#province').val())){
                alertx("请完善发货地址！",function (){$('#province').select2("open")});
                return false;
            }
            if(isNull($('#city').val())){
                alertx("请完善发货地址！",function (){$('#city').select2("open")});
                return false;
            }
            if(isNull($('#district').val())){
                alertx("请完善发货地址！",function (){$('#district').select2("open")});
                return false;
            }
            var valuationMethod = $('#valuationMethod').val();
            var inputError = false;
            function setError(title,input){
                inputError = true;
                alertx(title,function (){input.focus();});
                return false;
            }
            // 默认首重和续重都为1
            $('.first').val('1');
            $('.increment').val('1');
            $(':input.first,.increment').each(function (){
                var input = $(this);
                if('1' == valuationMethod && (!isNumber(input.val()) || parseFloat(input.val()) == 0)){
                    return setError("模板配置单位格式不正确！",input);
                }else if(('2' == valuationMethod || '3' == valuationMethod) && (!isDecimal(input.val()) || parseFloat(input.val()) <= 0)){
                    return setError("模板配置单位格式不正确！",input);
                }
            });
            if(inputError){
                return false;
            }
            $(':input.firstPrice,.incrementPrice').each(function (){
                var input = $(this);
                if(!isDecimal(input.val()) || parseFloat(input.val()) <= 0){
                    return setError("模板配置运费格式不正确！",input);
                }
            });
            if(inputError){
                return false;
            }
            return true;
        }
        function showValuation(valuationMethod,li){
            $('.valuationMethodUl li').removeClass("live");
            $('#valuationMethod').val(valuationMethod);
            $(li).addClass("live");
            if(1 == valuationMethod){
                $(".valuationTitle").html("件");
                $(".valuationUnit").html("个");
            }else if(2 == valuationMethod){
                $(".valuationTitle").html("重");
                $(".valuationUnit").html("Kg");
            }else if(3 == valuationMethod){
                $(".valuationTitle").html("体积");
                $(".valuationUnit").html("M³");
            }
        }
        function refreshDistrict(level,selectedId){
            var length = $("select.district").length;
            if(level < length){
                removeDistrictOption(level,length);
                var areaList = getAreaList(selectedId);
                if(areaList && areaList.length > 0){
                    var html = "<option value=''>请选择</option>";
                    for(var i in areaList){
                        html += '<option value="'+areaList[i].id+'"';
                        if(areaList[i].id == selectedId){
                            html += ' selected';
                        }
                        html += '>'+areaList[i].name+'</option>';
                    }
                    $("select.district").eq(level).append(html);
                    $("select.district").eq(level).select2();
                }
            }
        }
        function getAreaList(parentId){
            var list = null;
            $.ajax({
                async : false,
                type : 'POST',
                url : ctx + "/sys/area/getAreaList?parent.id="+parentId,
                success : function(areaList){
                    try{
                        list = areaList;
                    }catch(e){
                        console.log("获取行政区集合失败："+e.message);
                        return doError(e);
                    }
                },
                error: function(XMLHttpRequest) {
                    return doError(XMLHttpRequest);
                }
            });
            return list;
        }
        function getDistrict(li,parentId){
            var areaList = getAreaList(parentId);
            var configIndex = $('#configIndex').val();
            if(areaList && areaList.length > 0){
                $(".districtUl").html('');
                var html = '<li><input type="checkbox" id="checkAllBox" onclick="$(\'.districtBox\').attr(\'checked\',this.checked);buildSelectedDistrictObject();"><label for="checkAllBox">全选</label></li>';
                for(var i in areaList){
                    html += '<li><input type="checkbox" class="districtBox" onclick="linkCheckAll();buildSelectedDistrictObject()" ';
                    html += 'value="'+areaList[i].id+'" title="'+areaList[i].name+'" id="districtBox_'+areaList[i].id+'">';
                    html += '<label for="districtBox_'+areaList[i].id+'">'+areaList[i].name+'</label></li>';
                }
                $(".districtUl").append(html);
            }

            var indexObject = selectedDistrictObject[configIndex];
            if(indexObject){
                var selected = indexObject[parentId];
                if(selected && selected.selectAll){
                    $('#checkAllBox').attr('checked',true);
                    $('.districtBox').attr('checked',true);
                }else if(selected && selected.array){
                    var array = selected.array;
                    for(var i = 0;i<array.length;i++){
                        $(".districtBox[title="+array[i].name+"]").attr("checked",true);
                    }
                    linkCheckAll();
                }
            }

            $(li).addClass('live').siblings().removeClass('live');
        }
        function buildSelectedDistrictObject(){
            if($('#provinceList li.live').length == 0){
                return;
            }
            var province = $('#provinceList li.live').attr("id");
            var configIndex = $('#configIndex').val();
            var provinceObject = {};
            var provinceArray = new Array();
            $('.districtBox:checked').each(function (){
                var checkbox = $(this);
                var checkDistrict = {};
                checkDistrict.id = checkbox.val();
                checkDistrict.name = checkbox.attr("title");
                provinceArray.push(checkDistrict);
            });
            provinceObject.array = provinceArray;
            provinceObject.selectAll = $('.districtBox:checked').length == $('.districtBox').length;
            selectedDistrictObject[configIndex][province] = provinceObject;
            var selectedDistrictTitle = "";
            for(var provinceCode in selectedDistrictObject[configIndex]){
                var array = selectedDistrictObject[configIndex][provinceCode].array;
                var selectAll = selectedDistrictObject[configIndex][provinceCode].selectAll;
                if(array.length == 0){
                    delete selectedDistrictObject[configIndex][provinceCode];
                    continue;
                }
                selectedDistrictTitle += $('#' + provinceCode).text();
                if(selectAll){
                    selectedDistrictTitle += ",";
                    continue;
                }
                var districtTitle = '[';
                for(var i = 0;i<array.length;i++){
                    districtTitle += array[i].name + ',';
                }
                districtTitle = districtTitle.substring(0,districtTitle.length - 1);
                districtTitle += ']';
                selectedDistrictTitle += districtTitle + ',';
            }
            if(selectedDistrictTitle.lastIndexOf(',') == selectedDistrictTitle.length - 1){
                selectedDistrictTitle = selectedDistrictTitle.substring(0,selectedDistrictTitle.length - 1);
            }
            $('#selectedDistrict').html(selectedDistrictTitle);
            $('#configRow_'+configIndex).html(abbr(selectedDistrictTitle,80));
            $('#configRow_'+configIndex).attr("title",selectedDistrictTitle);
            $('#firstName_'+configIndex).val(selectedDistrictTitle);
        }

        function linkCheckAll(){
            $('#checkAllBox').attr("checked",$('.districtBox:checked').length == $('.districtBox').length);
        }

        function removeDistrictOption(level,length){
            if(level < length){
                $('select.district').eq(level).find("option").remove();
                $('select.district').eq(level++).select2();
                removeDistrictOption(level,length);
            }
        }
        function addConfig(){
            var configIndex = $('.configRow').length;
            if(!selectedDistrictObject[configIndex]){
                selectedDistrictObject[configIndex] = {};
            }
            var configHTML = "<tr id='configRowTR_"+configIndex+"' class='configRow'>";
            // 拼接模板HTML
            configHTML += '<td>';
            configHTML += '<span id="configRow_'+configIndex+'" title="请选择省份">指定地区</span>';
            configHTML += '<a href="javascript:void(0)" onclick="showDistrict('+configIndex+')" class="icon-edit" titile="选择指定邮费地区" style="margin-left:5px;"></a>';
            configHTML += '<a href="javascript:void(0)" onclick="$(\'#configRowTR_'+configIndex+'\').remove();delete selectedDistrictObject['+configIndex+'];$(\'#configIndex\').val(\'\');" class="icon-trash" titile="删除" style="margin-left:5px;"></a>';
            configHTML += '<input id="firstName_'+configIndex+'" name="freightConfigList['+configIndex+'].name" type="hidden">';
            configHTML += '<input name="freightConfigList['+configIndex+'].nationwideFlag" type="hidden" value="false">';
            configHTML += '</td>';
            configHTML += '<td><input id="first_'+configIndex+'" name="freightConfigList['+configIndex+'].first" type="text" class="input-small first hide"><span class="firstSpan">1</span></td>';
            configHTML += '<td><input id="firstPrice_'+configIndex+'" name="freightConfigList['+configIndex+'].firstPrice" type="text" class="input-small required firstPrice"></td>';
            configHTML += '<td><input id="increment_'+configIndex+'" name="freightConfigList['+configIndex+'].increment" type="text" class="input-small increment hide"><span class="incrementSpan">1</span></td>';
            configHTML += '<td><input id="incrementPrice_'+configIndex+'" name="freightConfigList['+configIndex+'].incrementPrice" type="text" class="input-small required incrementPrice"></td>';
            configHTML += "</tr>";
            $('.lastTr').before(configHTML);
        }
        function showDistrict(configIndex){
            var oldIndex = $("#configIndex").val()
            $("#configIndex").val(configIndex);
            if(!selectedDistrictObject[configIndex]){
                selectedDistrictObject[configIndex] = {};
            }
            if(oldIndex != configIndex){
                $('#selectedDistrict').html($('#configRow_'+configIndex).attr("title"));
                $(".districtUl").html('');
            }
            $('#provinceList li.live').removeClass('live');
            $(".districtUl li").remove();
            buildSelectedDistrictObject();
            showDialog('districtDialog');
        }
        function buildCityList(){
            var cityListHtml = "";
            function setCityError(index){
                alertx("请指定地区！", function () {showDistrict(index)});
                return true;
            }
            if(isEmptyObject(selectedDistrictObject) && $('.configRow').length > 1) return setCityError(0)
            for(var configIndex in selectedDistrictObject){
                var config = selectedDistrictObject[configIndex];
                if(isEmptyObject(config))return setCityError(configIndex)
                var cityIndex = 0;
                for(var provinceCode in config){
                    var cityList = config[provinceCode];
                    var array = cityList.array;
                    if(provinceCode && cityList && array && array.length == 0)return setCityError(configIndex)
                    for(var i in array){
                        cityListHtml += '<input type="hidden" title="'+array[i].name+'" name="freightConfigList['+configIndex+'].cityList['+cityIndex+'].id" value="'+array[i].id+'" class="buildInput"> \r\n';
                        cityIndex++;
                    }
                }
            }
            $('.buildInput').remove();
            $('#btnSubmit').before(cityListHtml);
        }
        /**
         * 判断对象是否为空属性对象
         * @param obj
         * @returns {boolean}
         */
        function isEmptyObject(obj){
            for(var n in obj){return false}
            return true;
        }
    </script>
    <style>
        .valuationMethodUl{margin: 0;overflow-y: auto;}
        .valuationMethodUl li{float:left;margin:0 10px 0 0;cursor:pointer;display: inline-block;border:1px solid #ccc;text-align: center;padding:0 10px;line-height: 30px;font-size: 14px;color: #636363;}
        .valuationMethodUl li.live{background: url("${ctxStatic}/images/spec_cur.png") no-repeat scroll right bottom rgba(0, 0, 0, 0);border: 1px solid #ff5a00;background-color: #fffbea;}
        .valuationMethodUl li:hover{border: 1px solid #ff5a00;}
        #provinceList li.live{background:#FAD4C0}
        #provinceList li:hover{background:#D9EDF7}
    </style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/ec/express/expressTemplateList">邮费模板列表</a></li>
		<li class="active"><a href="${ctx}/ec/express/expressTemplateForm?id=${expressTemplate.id}">邮费模板<shiro:hasPermission name="ec:expressTemplate:edit">${not empty expressTemplate.id?'修改':'添加'}</shiro:hasPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="expressTemplate" action="${ctx}/ec/express/expressTemplateSave" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">模板名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="required"/>
			</div>
		</div>
        <div class="control-group">
            <label class="control-label">发货人姓名：</label>
            <div class="controls">
                <form:input path="addresserName" cssClass="input-medium required"></form:input>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">发货人电话：</label>
            <div class="controls">
                <form:input path="addresserPhone" maxlength="20" cssClass="input-medium required"></form:input>
            </div>
        </div>
		<div class="control-group">
			<label class="control-label">发货地址：</label>
			<div class="controls">
                <form:hidden path="nation" value="100000"></form:hidden>
				<form:select path="province" cssClass="input-medium district" onchange="refreshDistrict(1,this.value)">
                    <form:options items="${provinceList}" itemLabel="name" itemValue="id"></form:options>
                </form:select>
                <form:select path="city" cssClass="input-medium district" onchange="refreshDistrict(2,this.value)" cssStyle="margin-left: 5px">
                    <form:options items="${cityList}" itemLabel="name" itemValue="id"></form:options>
                </form:select>
                <form:select path="district" cssClass="input-medium district" onchange="refreshDistrict(3,this.value)">
                    <form:options items="${districtList}" itemLabel="name" itemValue="id"></form:options>
                </form:select><br>
			</div>
		</div>
        <div class="control-group">
            <label class="control-label">发货详细地址：</label>
            <div class="controls">
                <form:input path="detailAddress" cssClass="input-xxlarge required"></form:input>
            </div>
        </div>
		<div class="control-group">
			<label class="control-label">计价方式：</label>
			<div class="controls">
				<ul class="valuationMethodUl">
                    <%--<li onclick="showValuation(1,this)" <c:if test="${expressTemplate.valuationMethod eq '1'}">class="live"</c:if>>按件数</li>--%>
                    <li onclick="showValuation(2,this)" <c:if test="${expressTemplate.valuationMethod eq '2'}">class="live"</c:if>>按重量</li>
                    <%--<li onclick="showValuation(3,this)" <c:if test="${expressTemplate.valuationMethod eq '3'}">class="live"</c:if>>按体积</li>--%>
                </ul>
                <form:hidden path="valuationMethod"></form:hidden>
			</div>
		</div>
        <div class="control-group">
            <label class="control-label">邮费设置：</label>
            <div class="controls">
                <div class="panel panel-info"><!-- 退货单 -->
                    <div class="panel-heading">
                        <div class="panel-title" style="font-size: 14px;">
                            快递运费设置
                        </div>
                    </div>
                    <div class="panel-body">
                        <table class="table table-bordered table-hover">
                            <tr>
                                <th>配送区域</th>
                                <th width="100px">首<span class="valuationTitle">重</span>(<span class="valuationUnit">Kg</span>)</th>
                                <th width="100px">运费(元)</th>
                                <th width="100px">续<span class="valuationTitle">重</span>(<span class="valuationUnit">Kg</span>)</th>
                                <th width="100px">运费(元)</th>
                            </tr>
                            <tr class='configRow'>
                                <td>
                                    <span title="全国默认地区">全国默认地区</span>
                                    <form:hidden path="freightConfigList[0].name" value="全国默认地区"></form:hidden>
                                    <form:hidden path="freightConfigList[0].nationwideFlag" value="true"></form:hidden>
                                </td>
                                <td>
                                    <input id="first_0" name="freightConfigList[0].first" value="${expressTemplate.freightConfigList[0].firstShow}" type="text" class="input-small first hide">
                                    <span class="firstSpan">1</span>
                                </td>
                                <td><input id="firstPrice_0" name="freightConfigList[0].firstPrice" value="${expressTemplate.freightConfigList[0].firstPrice}" type="text" class="input-small required firstPrice"></td>
                                <td>
                                    <input id="increment_0" name="freightConfigList[0].increment" value="${expressTemplate.freightConfigList[0].incrementShow}" type="text" class="input-small increment hide">
                                    <span class="incrementSpan">1</span>
                                </td>
                                <td><input id="incrementPrice_0" name="freightConfigList[0].incrementPrice" value="${expressTemplate.freightConfigList[0].incrementPrice}" type="text" class="input-small required incrementPrice"></td>
                            </tr>
                            <c:forEach items="${expressTemplate.freightConfigList}" var="freight" varStatus="index">
                                <c:if test="${index.index > 0}">
                                    <tr id="configRowTR_${index.index}" class="configRow">
                                        <td>
                                            <span title="${freight.name}" id="configRow_${index.index}">${freight.name}</span>
                                            <a href="javascript:void(0)" onclick="showDistrict(${index.index})" class="icon-edit" titile="选择指定邮费地区" style="margin-left:5px;"></a>
                                            <a href="javascript:void(0)" onclick="$('#configRowTR_${index.index}').remove();delete selectedDistrictObject[${index.index}];$('#configIndex').val('');" class="icon-trash" titile="删除" style="margin-left:5px;"></a>
                                            <form:hidden id="firstName_${index.index}" path="freightConfigList[${index.index}].name" value="${freight.name}"></form:hidden>
                                            <form:hidden path="freightConfigList[${index.index}].nationwideFlag" value="false"></form:hidden>
                                        </td>
                                        <td>
                                            <input id="first_${index.index}" name="freightConfigList[${index.index}].first" value="${freight.firstShow}" type="text" class="input-small first hide">
                                            <span class="firstSpan">1</span>
                                        </td>
                                        <td><input id="firstPrice_${index.index}" name="freightConfigList[${index.index}].firstPrice" value="${freight.firstPrice}" type="text" class="input-small required firstPrice"></td>
                                        <td>
                                            <input id="increment_${index.index}" name="freightConfigList[${index.index}].increment" value="${freight.incrementShow}" type="text" class="input-small increment hide">
                                            <span class="incrementSpan">1</span>
                                        </td>
                                        <td><input id="incrementPrice_${index.index}" name="freightConfigList[${index.index}].incrementPrice" value="${freight.incrementPrice}" type="text" class="input-small required incrementPrice"></td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                            <tr class="lastTr hide"></tr>
                        </table>
                    </div>
                    <div class="panel-footer">
                        <i class="icon-map-marker"></i><a href="javascript:void(0)" onclick="addConfig()">指定地区城市设置运费</a>
                    </div>
                </div>
            </div>
        </div>
		<div class="form-actions">
			<shiro:hasPermission name="ec:expressTemplate:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
    <div id="districtDialog" class="vcat-dialog" style="width: 700px;">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    指定地区邮费
                    <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('districtDialog')"></a>
                </div>
            </div>
            <div class="panel-body">
                <p style="margin-left: 25px;">已选：<span id="selectedDistrict">请选择城市</span></p>
                <div style="float: left;">
                    <ul style="width: 200px;list-style: none;height: 300px;overflow-y: auto;border: 1px solid #ccc;" id="provinceList">
                        <c:forEach items="${multipleAreaList}" var="area">
                            <li onclick="getDistrict(this,'${area.id}')" id="${area.id}"><span style="margin-left: 5px;">${area.name}</span></li>
                        </c:forEach>
                    </ul>
                </div>
                <div style="float: left;">
                    <ul class="districtUl" style="width: 400px;list-style: none;height: 300px;overflow-y: auto;border: 1px solid #ccc;">
                        <li>请选择省份</li>
                    </ul>
                </div><br>
                <div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;float: left;width:650px;">
                    <input type="hidden" id="configIndex">
                    <input onclick="showDialog('districtDialog')" class="btn btn-success" type="button" value="确定"/>
                </div>
            </div>
        </div>
    </div>
</body>
</html>