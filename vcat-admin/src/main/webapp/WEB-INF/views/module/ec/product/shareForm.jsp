<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分享奖励管理</title>
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

			if("1" == $("#selectedProductArchived").val()){
				alertx($('#productId').parent().find('.productListSpan').html() + " 已下架，请重新选择商品！",function(){
					$('#productId').parent().find('.btn').trigger("click");
				});
				return false;
			}

			if("" == $('#id').val() && new Date().getTime() > strToDate($("#startTime").val())){
				alertx("开始时间不能小于当前时间！");
				$("#startTime").trigger("click");
				return false;
			}
			if(strToDate($("#startTime").val()) >= strToDate($("#endTime").val())){
				alertx("结束时间不能小于等于开始时间！");
				$("#endTime").trigger("click");
				return false;
			}
			if("" == $("#imgUrl").val()){
				alertx("请上传活动图片！",function(){
					$('#imgUrl').parent().find('.btn').trigger("click");
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
	<form:form id="inputForm" modelAttribute="share" action="${ctx}/ec/product/saveShare" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="panel panel-info">
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					设置分享活动
				</div>
			</div>
			<div class="panel-body">
				<div class="tab-content">
					<div class="control-group">
						<label class="control-label">活动标题:</label>
						<div class="controls">
							<form:input path="title" htmlEscape="false" maxlength="100" class="input-xlarge required"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">所属商品:</label>
						<div class="controls">
							<sys:productSelect id="productId" name="product.id" value="${share.product.id}" queryParam="type=sj&archived=0" callback="setArchived"></sys:productSelect>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">活动图片:</label>
						<div class="controls">
							<sys:imageUpload id="imgUrl" name="imgUrl" value="${share.imgUrlPath}" widthScale="640" heightScale="330"></sys:imageUpload>
							<span class="help-inline">推荐上传分辨率比例为640*330的图片</span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">活动开始时间:</label>
						<div class="controls">
							<input type="text" id="startTime" name="startTime" value="<fmt:formatDate value="${share.startTime}" pattern="yyyy-MM-dd HH:mm"/>" readonly="readonly" maxlength="16" class="input-date Wdate required" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">活动结束时间:</label>
						<div class="controls">
							<input type="text" id="endTime" name="endTime" value="<fmt:formatDate value="${share.endTime}" pattern="yyyy-MM-dd HH:mm"/>" readonly="readonly" maxlength="16" class="input-date Wdate required" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">可分享次数:</label>
						<div class="controls">
							<input type="text" id="availableShare" name="availableShare" value="${share.availableShare}" placeholder="请输入数字" class="input-medium required digits">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">单次分享奖励:</label>
						<div class="controls">
							<input type="text" id="fund" name="fund" value="${share.fund}" placeholder="请输入奖励金额(元)" class="input-medium required number">
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