<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>专题管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function validateForm(){
            if(isNull($("#parentId").val())){
                alertx("请选择上级专题！",function(){
                    $("#parentName").trigger("click");
                });
                return false;
            }
            if(isNull($("#title").val())){
                alertx("请输入专题名称！",function(){
                    $("#title").focus();
                });
                return false;
            }
//            if(isNull($("#titleImg").val())){
//                alertx("请上传专题标题图片！",function(){
//                    $("#titleImg_image_upload_button").trigger("click");
//                });
//                return false;
//            }
//            if(isNull($("#listImg").val())){
//                alertx("请上传专题列表图片！",function(){
//                    $("#listImg_image_upload_button").trigger("click");
//                });
//                return false;
//            }
            if(isNull($("#displayOrder").val()) || !isNumber($("#displayOrder").val())){
                alertx("排序格式不正确！",function(){
                    $("#displayOrder").focus();
                });
                return false;
            }

			return true;
		}
        function saveTopic(){
            if(!validateForm()){
                return;
            }
            loading('正在提交，请稍等...');
            // 限制只有末级专题才能选择商品
//            if(isNull($('#id').val())){
//                $.get(ctx + "/ec/topic/checkHasProduct?topicId="+$('#parentId').val(),function(hasProduct){
//                    if(hasProduct){
//                        top.$.jBox.confirm("上级专题中已选择商品，是否将已选择商品转移至当前专题中？<br>确定：确认转移<br>取消：直接删除上级专题已选择商品<br>关闭窗口：取消保存",'系统提示',function(v,h,f){
//                            if(v=='ok'){
//                                $('#moveParentProduct').val(true);
//                                $('#inputForm').submit();
//                            }else if(v=='cancel'){
//                                $('#deleteParentProduct').val(true);
//                                $('#inputForm').submit();
//                            }
//                        },{buttonsFocus:1,width:500});
//                    }else{
//                        $('#inputForm').submit();
//                    }
//                });
//            }else{
//                $('#inputForm').submit();
//            }
            $('#inputForm').submit();
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs<c:if test="${category.sqlMap.onlyAdd eq 'true'}"> hide</c:if>">
		<li><a href="${ctx}/ec/topic/list">专题列表</a></li>
		<li class="active"><a href="${ctx}/ec/topic/form?id=${topic.id}&parent.id=${topic.parent.id}">专题<shiro:hasPermission name="ec:topic:edit">${not empty topic.id?'修改':'添加'}</shiro:hasPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="topic" action="${ctx}/ec/topic/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
        <form:hidden id="moveParentProduct" path="sqlMap['moveParentProduct']"/>
        <form:hidden id="deleteParentProduct" path="sqlMap['deleteParentProduct']"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">上级专题:</label>
			<div class="controls">
                <sys:treeselect id="parent" name="parent.id" value="${topic.parent.id}" labelName="parent.title" labelValue="${topic.parent.title}"
					title="专题" url="/ec/topic/treeData" extId="${topic.id}" allowClear="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">专题名称:</label>
			<div class="controls">
				<form:input path="title" maxlength="20" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">专题标题图片:</label>
			<div class="controls">
				<sys:imageUpload id="titleImg" name="titleImgUrl" value="${topic.titleImgUrlPath}"></sys:imageUpload>
			</div>
		</div>
        <div class="control-group">
            <label class="control-label">专题列表图片:</label>
            <div class="controls">
                <sys:imageUpload id="listImg" name="listImgUrl" value="${topic.listImgUrlPath}"></sys:imageUpload>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">开始时间：</label>
            <div class="controls">
                <input id="startTime" type="text" name="startTime" value="<fmt:formatDate value="${topic.startTime}" pattern="yyyy-MM-dd HH:mm"/>" readonly="readonly" maxlength="16" class="input-date Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">结束时间：</label>
            <div class="controls">
                <input id="endTime" type="text" name="endTime" value="<fmt:formatDate value="${topic.endTime}" pattern="yyyy-MM-dd HH:mm"/>" readonly="readonly" maxlength="16" class="input-date Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">排序:</label>
            <div class="controls">
                <form:input path="displayOrder" htmlEscape="false" maxlength="50" class="required digits input-small"/>
            </div>
        </div>
		<div class="form-actions">
			<shiro:hasPermission name="ec:topic:edit"><input id="btnSubmit" class="btn btn-primary" type="button" value="保 存" onclick="saveTopic()"/>&nbsp;</shiro:hasPermission>
            <shiro:hasPermission name="ec:topic:edit">
                <c:if test="${topic != null && topic.id != null && topic.id != ''}">
                    <input id="btnSelect" class="btn btn-primary" type="button" value="选择商品" onclick="showViewPage(ctx + '/ec/topic/selectedProductList?sqlMap[\'topicId\']=${topic.id}','${topic.title} 已选商品')"/>&nbsp;
                </c:if>
            </shiro:hasPermission>
			<input id="btnCancel" class="btn<c:if test="${topic.sqlMap.onlyAdd eq 'true'}"> hide</c:if>" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>