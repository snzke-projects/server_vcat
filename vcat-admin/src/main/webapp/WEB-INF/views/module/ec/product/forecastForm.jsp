<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品上架预告</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(function() {
			$("#title").focus();
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
		});
		function validateForm(){
			if("" == $("#productId").val()){
				alertx("请选择上架商品！",function(){
					$('#productId').parent().find('.btn').trigger("click");
				});
				return false;
			}
			if("0" == $("#selectedProductArchived").val()){
				alertx($('#productId').parent().find('.productListSpan').html() + " 已上架，请重新选择商品！",function(){
					$('#productId').parent().find('.btn').trigger("click");
				});
				return false;
			}
			if("" == $("#imgUrl").val()){
				alertx("请上传预告图片！",function(){
					$('#imgUrl').parent().find('.btn').trigger("click");
				});
				return false;
			}
			if("" == $("#intro").val()){
				alertx("简介不能为空！",function(){
					$("#intro").focus();
				});
				return false;
			}

			if($('#forecastDate').val() != "" && new Date().getTime() >= new Date($('#forecastDate').val().replace(/-/g,"/")).getTime()){
				alertx("预计商品上架时间必须大于当前时间！",function(){
					$("#forecastDate").trigger("click");
				});
				return false;
			}

			return true;
		}
		function setArchived(product){
			if(!isNull(product) && !isNull(product.archived)){
				$('#selectedProductArchived').val(product.archived);
			}
		}
	</script>
</head>
<body>
	<form:form id="inputForm" modelAttribute="productForecast" action="${ctx}/ec/product/saveForecast" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="panel panel-info">
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					设置商品上架预告
				</div>
			</div>
			<div class="panel-body">
				<div class="tab-content">
					<div class="control-group">
						<label class="control-label">预告标题：</label>
						<div class="controls">
							<form:input path="title" htmlEscape="false" maxlength="100" class="input-xxlarge required"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">上架商品：</label>
						<div class="controls productControls">
							<sys:productSelect id="productId" name="product.id" value="${productForecast.product.id}" queryParam="type=xj&archived=1" callback="setArchived"></sys:productSelect>
                            <span class="help-inline">只能选择下架的商品参加上架预告活动</span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">预告图片：</label>
						<div class="controls">
							<sys:imageUpload id="imgUrl" name="imgUrl" value="${productForecast.imgUrlPath}" widthScale="640" heightScale="330" ></sys:imageUpload>
							<span class="help-inline">只能上传分辨率比例为640*330的图片</span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">预计商品上架时间：</label>
						<div class="controls">
							<input type="text" id="forecastDate" name="forecastDate" value="<fmt:formatDate value="${productForecast.forecastDate}" pattern="yyyy-MM-dd HH:mm"/>" readonly maxlength="16" class="input-date required Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">商品预告简介：</label>
						<div class="controls">
							<textarea id="intro" name="intro" placeholder="请输入商品预告简介" style="width: 95%" rows="5">${productForecast.intro}</textarea>
						</div>
					</div>
				</div>
				<div class="form-actions">
					<input id="selectedProductArchived" type="hidden">
					<shiro:hasPermission name="ec:product:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
			</div>
		</div>
	</form:form>
</body>
</html>