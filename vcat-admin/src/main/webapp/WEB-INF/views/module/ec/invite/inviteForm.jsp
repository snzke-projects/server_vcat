<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>邀请奖励管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(function() {
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
			if(new Date().getTime() > strToDate($("#startTime").val())){
				alertx("开始时间不能小于当前时间！", function (){
                    $("#startTime").trigger("click");
                });
				return false;
			}
			if(strToDate($("#startTime").val()) >= strToDate($("#endTime").val())){
				alertx("开始时间必须大于结束时间！", function (){
                    $("#endTime").trigger("click");
                });
				return false;
			}
            if(isNull($("#inviterEarning").val()) || !isMoney($("#inviterEarning").val())){
                alertx("邀请人奖励金额格式不正确",function(){
                    $("#inviterEarning").focus();
                });
                return false;
            }
            if(isNull($("#inviteeEarning").val()) || !isMoney($("#inviteeEarning").val())){
                alertx("被邀请人奖励金额格式不正确",function(){
                    $("#inviteeEarning").focus();
                });
                return false;
            }
			return true;
		}
	</script>
</head>
<body>
	<form:form id="inputForm" modelAttribute="invite" action="${ctx}/ec/invite/saveInvite" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="panel panel-info">
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					设置邀请奖励活动
				</div>
			</div>
			<div class="panel-body">
				<div class="tab-content">
					<div class="control-group">
						<label class="control-label">活动开始时间：</label>
						<div class="controls">
							<input type="text" id="startTime" name="startTime" value="<fmt:formatDate value="${invite.startTime}" pattern="yyyy-MM-dd HH:mm"/>" readonly="readonly" maxlength="16" class="input-date Wdate required" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">活动结束时间：</label>
						<div class="controls">
							<input type="text" id="endTime" name="endTime" value="<fmt:formatDate value="${invite.endTime}" pattern="yyyy-MM-dd HH:mm"/>" readonly="readonly" maxlength="16" class="input-date Wdate required" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">可邀请次数：</label>
						<div class="controls">
							<input type="text" id="availableInvite" name="availableInvite" value="${invite.availableInvite}" placeholder="请输入数字" class="input-medium required digits">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">邀请人奖励金额：</label>
						<div class="controls">
							<input type="text" id="inviterEarning" name="inviterEarning" value="${invite.inviterEarning}" placeholder="请输入奖励金额(元)" class="input-medium required number">
						</div>
					</div>
                    <div class="control-group">
                        <label class="control-label">被邀请人奖励金额：</label>
                        <div class="controls">
                            <input type="text" id="inviteeEarning" name="inviteeEarning" value="${invite.inviteeEarning}" placeholder="请输入奖励金额(元)" class="input-medium required number">
                        </div>
                    </div>
				</div>
				<div class="form-actions">
					<input id="selectedProductArchived" type="hidden">
					<shiro:hasPermission name="ec:invite:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
			</div>
		</div>
	</form:form>
</body>
</html>