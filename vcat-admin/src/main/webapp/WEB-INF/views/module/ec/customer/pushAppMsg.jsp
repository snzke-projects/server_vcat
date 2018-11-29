<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>推送应用消息</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function toSubmit(){
			var msg = $('#articleId').val();
			if(isNull(msg)){
				alertx("请选择要推送的消息文章！",function (){
					$('#relationButton').focus();
				});
				return;
			}
			confirmx("确认发送？",function (){
				$('#inputForm').submit();
			});
		}
	</script>
</head>
<body>
	<form:form id="inputForm" action="${ctx}/ec/customer/pushAppMsg" method="post" class="form-horizontal">
	<sys:message content="${message}"/>
	<div class="control-group">
		<label class="control-label">小店等级：</label>
		<div class="controls">
			<c:forEach items="${levelList}" var="level" varStatus="status">
				<input type="checkbox" name="levels" value="${level.id}" id="level_${status.index}" checked>
				<label for="level_${status.index}"><img src="${level.urlPath}" alt="${level.name}" width="20" height="13"/></label>
			</c:forEach>
			<a href="javascript:void(0);" onclick="$(this).parent().find(':checkbox').attr('checked','true')">全选</a>
			<a href="javascript:void(0);" onclick="$(this).parent().find(':checked').removeAttr('checked')">清空</a>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">发送时间：</label>
		<div class="controls">
			<input type="text" name="sendTime" readonly class="input-medium Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
			<span class="help-inline">为空则立即发送</span>
		</div>
	</div>
	<%--<div class="control-group">
		<label class="control-label">消息类型：</label>
		<div class="controls">
			<input type="radio" name="msgType" value="1" id="msgType_1" checked><label for="msgType_1">促销</label><span class="help-inline">用户点击消息进入店铺商品列表</span><br>
			<input type="radio" name="msgType" value="2" id="msgType_2"><label for="msgType_2">系统消息</label><span class="help-inline">用户点击消息进入系统消息列表</span><br>
			<input type="radio" name="msgType" value="3" id="msgType_3"><label for="msgType_3">文章消息</label><span class="help-inline">用户点击消息进入V猫教室</span>
		</div>
	</div>--%>
	<%--<div class="control-group">
		<label class="control-label">消息内容：</label>
		<div class="controls">
			<input type="text" class="required input-xxlarge" id="msg" maxlength="20" name="msg" style="margin-bottom: 0px;" placeholder="例：听说现在分享商品能够获得更多曝光率"/>
		</div>
	</div>--%>
	<div class="control-group">
		<label class="control-label">系统消息文章：</label>
		<div class="controls">
			<input type="hidden" id="articleId" name="articleId"/>
			<ol id="articleSelectList"></ol>
			<a id="relationButton" href="javascript:" class="btn">选择系统消息</a>
			<script type="text/javascript">
				var articleSelect = [];
				function articleSelectAddOrDel(id,title){
                    articleSelect = [];
					articleSelect.push([id,title]);
					articleSelectRefresh();
				}
				function articleSelectRefresh(){
					$("#articleDataRelation").val("");
					$("#articleSelectList").children().remove();
					for (var i=0; i<articleSelect.length; i++){
						$("#articleSelectList").append("<li>"+articleSelect[i][1]+"&nbsp;&nbsp;<a href=\"javascript:\" onclick=\"articleSelectAddOrDel('"+articleSelect[i][0]+"','"+articleSelect[i][1]+"');\">×</a></li>");
						$("#articleDataRelation").val($("#articleDataRelation").val()+articleSelect[i][0]+",");
					}
					if(null != articleSelect && articleSelect.length > 0){
						$('#articleId').val(articleSelect[0][0]);
					}
				}
				$.getJSON("${ctx}/cms/article/findByIds",{ids:$("#articleDataRelation").val()},function(data){
					for (var i=0; i<data.length; i++){
						articleSelect.push([data[i][1],data[i][2]]);
					}
					articleSelectRefresh();
				});
				$("#relationButton").click(function(){
                    var systemCategoryId = '${fns:getDictSingleValue('ec_system_article_category_id', '1')}';
					top.$.jBox.open("iframe:${ctx}/cms/article/selectList?pageSize=10&category.id="+systemCategoryId+"&lockCategory=1&selectType=radio", "选择文章",$(top.document).width()-220,$(top.document).height()-180,{
						buttons:{"确定":true}, loaded:function(h){
							$(".jbox-content", top.document).css("overflow-y","hidden");
						}
					});
				});
			</script>
		</div>
	</div>
	<div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;">
		<input onclick="toSubmit()" class="btn btn-success" type="button" value="发 送"/>
	</div>
	</form:form>
</body>
</html>