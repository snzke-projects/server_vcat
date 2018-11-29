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
            if(isNull($('#type').val())){
                alertx("请选择购物券类型！",function(){$('#type').select2('open');});
                return false;
            }
			return true;
		}
	</script>
</head>
<body>
	<form:form id="inputForm" modelAttribute="coupon" action="${ctx}/ec/coupon/saveCoupon" method="post" class="form-horizontal">
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
                        <label class="control-label">名称：</label>
                        <div class="controls">
                            <input type="text" id="name" name="name" value="${coupon.name}" maxlength="20" class="input-large required"/>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">额度：</label>
                        <div class="controls">
                            <input type="text" id="fund" name="fund" value="${coupon.fund}" maxlength="13" class="input-mini number required"/>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">总张数：</label>
                        <div class="controls">
                            <input type="text" id="total" name="total" value="${coupon.total}" placeholder="请输入数字" class="input-medium required digits">
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">类型：</label>
                        <div class="controls">
                            <form:select id="type" path="type" class="input-large">
                                <form:option value="">请选择</form:option>
                                <c:forEach items="${fns:getDictList('ec_coupon_type')}" var="t">
                                    <form:option value="${t.value}">${t.label}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>
					<div class="control-group">
						<label class="control-label">发放开始时间：</label>
						<div class="controls">
							<input type="text" id="startTime" name="startTime" value="<fmt:formatDate value="${coupon.startTime}" pattern="yyyy-MM-dd HH:mm"/>" readonly="readonly" maxlength="16" class="input-date Wdate required" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">发放结束时间：</label>
						<div class="controls">
							<input type="text" id="endTime" name="endTime" value="<fmt:formatDate value="${coupon.endTime}" pattern="yyyy-MM-dd HH:mm"/>" readonly="readonly" maxlength="16" class="input-date Wdate required" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
						</div>
					</div>
				</div>
				<div class="form-actions">
					<input id="selectedProductArchived" type="hidden">
					<shiro:hasPermission name="ec:coupon:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
			</div>
		</div>
	</form:form>
</body>
</html>