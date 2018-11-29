<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分类管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(function() {
			$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					if(validateForm()){
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
            buildSpec('${category.id}');
		});

		function validateForm(){
			if(isNull($("#categoryId").val())){
				alertx("请选择上级分类！",function(){
                    $("#categoryName").trigger("click");
                });
				return false;
			}
//            if(isNull($("#icon").val())){
//                alertx("请上传图标！",function(){
//                    $("#icon_image_upload_button").trigger("click");
//                });
//                return false;
//            }

			return true;
		}

		function newProperty(propertyId,propertyName){
			var buildNewProperty = true;
			$(".property_name").each(function (){
				var name = $(this).val();
				if(name.indexOf(propertyName) == 0){
					buildNewProperty = false;
				}
			});
			if(buildNewProperty){
				$("#propertyDIV").append(getPropertyDIVHTML(propertyId,propertyName));
			}
		}
		var propertyIndex = 0 + parseInt("${fn:length(category.propertyList)}");
		function getPropertyDIVHTML(propertyId,propertyName){
			var uuid = new Date().getTime();
			var html = '<input class="property_'+uuid+' property_name" type="text" style="width:50px" readonly="readonly" value=\"'+propertyName+'\">';
			html += '<input type="hidden" class="property_'+uuid+'" name="propertyList['+(propertyIndex++)+'].id" value=\"'+propertyId+'\"></input>';
			html += '&nbsp;<span class="icon-remove property_'+uuid+'" onclick="$(\'.property_'+uuid+'\').remove();"></span>';
			return html;
		}
        function buildSpec(id){
            $.ajax({
                type : 'POST',
                url : ctx + "/ec/product/specName?category.id=" + id,
                success : function(specList){
                    var specHTML = "";
                    if(isNull(specList) || specList.length == 0){
                        $('#btnShowSpecList').before("<span>该分类下暂无规格</span>");
                    }
                    for(var i in specList){
                        if(i >= 5){
                            break;
                        }
                        var spec = specList[i];
                        var uuid = (Math.random() + "").replace(/\./g,"");
                        var specClick = 'onclick=' + (spec.editable ? 'toAddSpec("'+spec.id+'","'+spec.name+'","'+spec.value+'")' : 'void(0)');
                        specHTML += '<span class="spec_'+uuid+' spec">';
                        specHTML += '<input class="spec" type="text" style="width:80px" readonly="readonly" value=\"'+spec.name+'\" '+specClick+' title="点击可编辑">';
                        if(spec.editable){
                            specHTML += '&nbsp;<span class="icon icon-remove spec" onclick="deleteSpec(\''+spec.id+'\',\''+spec.name+'\')" title="删除规格 '+spec.name+'"></span>';
                        }
                        specHTML += '&nbsp;</span>';
                    }
                    $('.spec').remove();
                    if(5 < specList.length){
                        $('#btnShowSpecList').show("fast");
                    }else{
                        $('#btnShowSpecList').hide();
                    }
                    $('#btnShowSpecList').before(specHTML);
                },
                error: function(XMLHttpRequest) {
                    return doError(XMLHttpRequest);
                }
            });
        }
        function showSpecList(id){
            top.$.jBox.open("iframe:"+ctx+"/ec/product/spec?category.id="+id, "规格列表",$(top.document).width()-220,$(top.document).height()-180,{
                buttons: {"确定": true},
                loaded: function () {
                    $(".jbox-content", top.document).css("overflow-y", "hidden");
                },
                closed: function(){
                    buildSpec('${category.id}');
                }
            });
        }
        function toAddSpec(id,name,value){
            $("#specId").val(id);
            $("#specName").val(name);
            $("#specValue").val(value);
            showDialog('specDialog');
        }
        function saveSpec(){
            var id = $("#specId").val();
            var name = $("#specName").val();
            var value = $("#specValue").val();
            if(isNull(name)){
                alertx("属性名称不能为空",function (){
                    $("#name").focus();
                });
                return false;
            }
            if(isNull(value)){
                alertx("属性值不能为空",function (){
                    $("#value").focus();
                });
                return false;
            }
            value = value.replace(/，/g,",");
            value = value.replace(/\|/g,",");
            doSaveSpec(id,name,value);
        }
        function doSaveSpec(id,name,value){
            value = filterSame(value);
            $.ajax({
                type : 'POST',
                url : ctx + "/ec/product/saveSpec",
                data : {
                    id : id,
                    "category.id" : $('#id').val(),
                    name : name,
                    value : value
                },
                success : function(){
                    try{
                        alertx("保存规格属性 " + name + " 成功！",function(){
                            location.href = location.href;
                        });
                    }catch(e){
                        console.log("保存规格属性 " + name + " 失败："+e.message);
                        return doError(e);
                    }
                },
                error: function(XMLHttpRequest) {
                    return doError(XMLHttpRequest);
                }
            });
        }
        function deleteSpec(id,name){
            confirmx("确认删除 " + name + " ？",function (){
                $.ajax({
                    type : 'POST',
                    url : ctx + "/ec/product/deleteSpec",
                    data : {
                        id : id
                    },
                    success : function(){
                        try{
                            alertx("删除规格属性 " + name + " 成功！",function(){
                                location.href = location.href;
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
        }
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs<c:if test="${category.sqlMap.onlyAdd eq 'true'}"> hide</c:if>">
		<li><a href="${ctx}/ec/category/list">分类列表</a></li>
		<li class="active"><a href="${ctx}/ec/category/form?id=${category.id}&parent.id=${category.parent.id}">分类<shiro:hasPermission name="ec:category:edit">${not empty category.id?'修改':'添加'}</shiro:hasPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="category" action="${ctx}/ec/category/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">上级分类:</label>
			<div class="controls">
                <sys:treeselect id="category" name="parent.id" value="${category.parent.id}" labelName="parent.name" labelValue="${category.parent.name}"
					title="分类" url="/ec/category/treeData" extId="${category.id}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分类名称:</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="50" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分类图标:</label>
			<div class="controls">
				<sys:imageUpload id="icon" name="icon" value="${category.iconPath}" widthScale="1" heightScale="1"></sys:imageUpload>
                <span class="help-inline">推荐上传分辨率为100*100的图片</span>
			</div>
		</div>
        <div class="control-group">
            <label class="control-label">排序:</label>
            <div class="controls">
                <form:input path="displayOrder" htmlEscape="false" maxlength="50" class="required digits input-small"/>
                <span class="help-inline">排列顺序，升序。</span>
            </div>
        </div>
		<div class="control-group">
			<label class="control-label">该分类下商品属性:</label>
			<div class="controls">
				<span id="propertyDIV">
					<c:forEach items="${category.propertyList}" var="pro" varStatus="status">
						<input class="property_${pro.id} property_name" type="text" style="width:50px;" readonly="readonly" value="${pro.name}">
						<c:if test="${pro.editable}">
                            <input type="hidden" class="property_${pro.id}" name="propertyList[${status.index}].id" value="${pro.id}">
                            <shiro:hasPermission name="ec:category:property:edit">
                                <span class="icon-remove property_${pro.id}" onclick="$('.property_${pro.id}').remove();"></span>
                            </shiro:hasPermission>
                        </c:if>
					</c:forEach>
				</span>
                <shiro:hasPermission name="ec:category:property:edit">
				    <sys:propertyList id="categoryProperty" callbackFunc="newProperty"></sys:propertyList>
                </shiro:hasPermission>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">该分类下商品规格:</label>
			<div class="controls specDIV">
                <input id="btnShowSpecList" class="btn hide" type="button" value="更多" onclick="showSpecList('${category.id}')"/>
                <shiro:hasPermission name="ec:category:spec:edit">
                    <input id="btnAddSpec" class="btn" type="button" value="添加规格" onclick="toAddSpec()"/>
                </shiro:hasPermission>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">描述:</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasAnyPermissions name="ec:category:edit,ec:category:add"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasAnyPermissions>
			<input id="btnCancel" class="btn<c:if test="${category.sqlMap.onlyAdd eq 'true'}"> hide</c:if>" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

    <div id="specDialog" class="vcat-dialog" style="width: 50%;">
        <div class="panel panel-info"><!-- 退货单 -->
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    规格属性
                    <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('specDialog')"></a>
                </div>
            </div>
            <div class="panel-body">
                <table class="table table-responsive table-hover">
                    <tbody>
                    <tr>
                        <td width="30%">规格属性名称：</td>
                        <td width="70%"><input id="specName" type="text" maxlength="100" style="margin-bottom: 0px"/></td>
                    </tr>
                    <tr>
                        <td colspan="2">规格属性值：</td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <textarea id="specValue" cols="10" rows="3" style="resize:none;width:95%;"></textarea>
                            <span class="help-inline">多个属性值以逗号[,]分隔</span>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;">
                    <input type="hidden" id="specId">
                    <shiro:hasPermission name="ec:category:spec:edit">
                        <input onclick="saveSpec()" class="btn btn-success" type="button" value="保存"/>
                    </shiro:hasPermission>
                    <input onclick="showDialog('specDialog')" class="btn btn-default" type="button" value="返回"/>
                </div>
            </div>
        </div>
    </div>
</body>
</html>