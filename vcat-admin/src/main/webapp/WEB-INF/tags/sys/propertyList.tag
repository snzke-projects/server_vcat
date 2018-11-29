<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="id" type="java.lang.String" required="true" description="属性选择控件唯一标识"%>
<%@ attribute name="callbackFunc" type="java.lang.String" required="true" description="当用户选择时回调的函数，回调函数默认传递两个参数：id,name"%>
<script type="text/javascript">
	$(function (){
		${id}GetListHTML();
	});
	function ${id}GetListHTML(){
		$.ajax({
			async : false,
			type : 'POST',
			url : "${ctx}/ec/property/listData",
			success : function(propertyList){
				try{
					var optionsHTML = "";
					if(propertyList && propertyList.length > 0){
						for(var i in propertyList){
							optionsHTML += ${id}GetOptionHTML(propertyList[i]);
						}
					}
					if(optionsHTML.length > 0){
						$("#${id} .property_option").remove();
						$("#${id}").append(optionsHTML);
					}
				}catch(e){
					console.log("获取属性集合失败："+e.message);
					return;
				}
			}
		});
	}

	function ${id}GetOptionHTML(property){
		if(property){
			var optionHTML = "<option class='property_option' value='"+property.id+"'>";
			optionHTML += property.name;
			optionHTML += "</option>";
			return optionHTML;
		}
		return " ";
	}
	function ${id}CallFunc(optionValue,optionTitle){
		if("" != optionValue && "${callbackFunc}" in window && "function" == typeof(${callbackFunc})){
			${callbackFunc}.call(this,optionValue,optionTitle);
		}
	}
	function ${id}AddProperty(name){
		if("" == name){
			alertx("属性名称不能为空！",function (){
				$("#${id}Property").focus();
			});
			return;
		}
		confirmx("确定添加 " + name + " 属性吗？",function (){
			$.ajax({
				async : false,
				type : 'POST',
				url : "${ctx}/ec/property/save",
				data : {name : name},
				success : function(productProperty){
					${id}GetListHTML();
					$('#${id}Property').val("");
                    ${id}CallFunc(productProperty.id,productProperty.name);
					top.$.jBox.tip("添加 " + name + " 成功");
				}
			});
		});
	}
</script>
<select id="${id}" onchange="${id}CallFunc(this.value,$('#${id} option').eq(this.selectedIndex).html())"class="input-small">
	<option value="" selected>选择属性</option>
</select>

<shiro:hasPermission name="ec:product:property:add">
    <span></span>
    <input type="text" id="${id}Property" class="input-small" placeholder="新属性名称">
    <span class="input-group-btn">
        <button class="btn btn-default" type="button" onclick="${id}AddProperty($('#${id}Property').val())">添加属性</button>
    </span>
</shiro:hasPermission>