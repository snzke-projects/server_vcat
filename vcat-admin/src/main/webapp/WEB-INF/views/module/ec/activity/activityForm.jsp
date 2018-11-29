<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>活动</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(function() {
			$("#title").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					if(validateForm()){
						detailsEditor.sync();
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
			if("" == $("#imgUrl").val()){
				alertx("请上传体验官图片！",function (){
                    $('#imgUrl_image_upload_button').trigger('click');
                });
				return false;
			}
            if(strToDate($("#startDate").val()) >= strToDate($("#endDate").val())){
                alertx("体验官开始时间必须小于体验官结束时间！", function (){
                    $("#startDate").trigger("click");
                });
                return false;
            }
			if("" == $("#intro").val()){
				alertx("简介不能为空！",function(){
					$("#intro").focus();
				});
				return false;
			}
			detailsEditor.sync();
			if("" == $("#details").val()){
				alertx("体验官详情不能为空！",function(){
					$(".ke-edit-iframe").focus();
				});
				return false;
			}
			return true;
		}
	</script>
</head>
<body>
	<form:form id="inputForm" modelAttribute="activity" action="${ctx}/ec/activity/saveActivity" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="panel panel-info">
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					设置体验官
				</div>
			</div>
			<div class="panel-body">
				<div class="tab-content">
					<div class="control-group">
						<label class="control-label">体验官标题：</label>
						<div class="controls">
							<form:input path="title" htmlEscape="false" maxlength="100" class="input-xxlarge required"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">体验官地址：</label>
						<div class="controls">
							<form:input path="address" htmlEscape="false" maxlength="200" class="input-xxlarge required"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">体验官开始时间：</label>
						<div class="controls">
							<input id="startDate" type="text" name="startDate" value="<fmt:formatDate value="${activity.startDate}" pattern="yyyy-MM-dd HH:mm"/>" readonly="readonly" maxlength="16" class="input-date Wdate required" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">体验官结束时间：</label>
						<div class="controls">
							<input id="endDate" type="text" name="endDate" value="<fmt:formatDate value="${activity.endDate}" pattern="yyyy-MM-dd HH:mm"/>" readonly="readonly" maxlength="16" class="input-date Wdate required" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">席位：</label>
						<div class="controls">
							<form:input path="seat" maxlength="10" cssClass="input-small digits required"/>
						</div>
					</div>
                    <div class="control-group">
                        <label class="control-label">费用名称：</label>
                        <div class="controls">
                            <form:input path="feeName" maxlength="50" cssClass="input-small required"/>
                        </div>
                    </div>
					<div class="control-group">
						<label class="control-label">费用：</label>
						<div class="controls">
							<form:input path="fee" maxlength="10" cssClass="input-small number required"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">体验官图片：</label>
						<div class="controls">
							<sys:imageUpload id="imgUrl" name="imgUrl" value="${activity.imgUrlPath}" widthScale="640" heightScale="330" ></sys:imageUpload>
							<span class="help-inline">只能上传分辨率比例为640*330的图片</span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">体验官简介：</label>
						<div class="controls">
							<textarea id="intro" name="intro" placeholder="请输入体验官简介" style="width: 99%" rows="3">${activity.intro}</textarea>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">体验官详情：</label>
						<div class="controls">
							<textarea id="details" name="details">${activity.details}</textarea>
							<sys:kindeditor replace="details" width="95%" height="400px"></sys:kindeditor>
						</div>
					</div>
				</div>
				<div class="form-actions">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
			</div>
		</div>
	</form:form>
</body>
</html>