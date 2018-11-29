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
					descriptionEditor.sync();
                    if(isNull($('#supplierLogo').val())){
                        alertx("请上传一张Logo图片！",function(){$('#supplierLogo_image_upload_button').trigger("click")});
                        return false;
                    }
                    if(isNull($('#description').val())){
                        alertx("请填写品牌故事！",function(){descriptionEditor.focus()});
                        return false;
                    }
                    if(isNull($('#supplier').val())){
                        alertx("请选择所属供应商！",function(){$('#supplier').select2("open");});
                        return false;
                    }
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
		});
        function showAddSupplier(){
            top.$.jBox.open("iframe:"+ctx+"/ec/supplier/form?sqlMap['onlyAdd']=true", "添加供应商",$(top.document).width()-220,$(top.document).height()-180,{
                buttons: {"确定": true},
                loaded: function () {
                    $(".jbox-content", top.document).css("overflow-y", "hidden");
                }
                ,closed:function (){
                    refreshSupplierList();
                }
            });
        }

        function refreshSupplierList(){
            $.ajax({
                type : 'POST',
                url : ctx + "/ec/supplier/getSupplierList",
                success : function(supplierList){
                    try{
                        if(supplierList && supplierList.length > 0){
                            var selectedSupplierId = $('#supplier option:selected').val();
                            $('#supplier option:not(:first)').remove();
                            var html = "";
                            for(var i in supplierList){
                                html += '<option value="'+supplierList[i].id+'"';
                                if(supplierList[i].id == selectedSupplierId){
                                    html += ' selected';
                                }
                                html += '>'+supplierList[i].name+'</option>';
                            }
                            $('#supplier').append(html);
                            $('#supplier').select2();
                        }
                    }catch(e){
                        console.log("获取供应商集合失败："+e.message);
                        return doError(e);
                    }
                },
                error: function(XMLHttpRequest) {
                    return doError(XMLHttpRequest);
                }
            });
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs<c:if test="${brand.sqlMap.onlyAdd eq 'true'}"> hide</c:if>">
		<li><a href="${ctx}/ec/supplier/brandList">品牌列表</a></li>
		<li class="active"><a href="${ctx}/ec/supplier/brandForm?id=${brand.id}">品牌<shiro:hasPermission name="ec:brand:edit">${not empty brand.id?'修改':'添加'}</shiro:hasPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="brand" action="${ctx}/ec/supplier/saveBrand" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">品牌名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="200" class="required"/>
			</div>
		</div>
        <div class="control-group">
            <label class="control-label">所属供应商：</label>
            <div class="controls">
                <form:select id="supplier" path="supplier.id" class="input-medium">
                    <form:option value="" label="请选择供应商"/>
                    <form:options items="${supplierList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
                </form:select>
                <shiro:hasPermission name="ec:supplier:add"><input type="button" class="btn btn-primary" onclick="showAddSupplier()" value="添加供应商" style="margin-left: 5px;"></shiro:hasPermission>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">邮费：</label>
            <div class="controls">
                <form:input path="freightCharge" htmlEscape="false" maxlength="10" class="required number input-mini"/>
            </div>
        </div>
		<div class="control-group">
			<label class="control-label">LOGO图片：</label>
			<div class="controls">
				<sys:imageUpload id="supplierLogo" name="logoUrl" value="${brand.logoUrlPath}" ></sys:imageUpload>
                <span class="help-inline">推荐上传分辨率为100*100的图片</span>
			</div>
		</div>
        <div class="control-group">
            <label class="control-label">简介：</label>
            <div class="controls">
                <textarea id="intro" name="intro" class="required" maxlength="100" rows="2" style="width: 95%">${brand.intro}</textarea>
                <span class="help-inline">此简介在小店选择品牌时会展示</span>
                <span class="help-inline">长度限制为100个字</span>
            </div>
        </div>
		<div class="control-group">
			<label class="control-label">品牌故事：</label>
			<div class="controls">
				<form:textarea id="description" htmlEscape="false" path="description" rows="4" class="input-xxlarge" />
				<sys:kindeditor replace="description" width="100%" height="300px"/>
			</div>
		</div>
		<div class="form-actions" >
			<shiro:hasPermission name="ec:brand:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn<c:if test="${brand.sqlMap.onlyAdd eq 'true'}"> hide</c:if>" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>