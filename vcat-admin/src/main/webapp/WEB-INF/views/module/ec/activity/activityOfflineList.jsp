<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>活动</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}
		function checkActivate(id){
			confirmx("激活后不能删除该活动，确认激活？","${ctx}/ec/activity/activateActivityOffline?id="+id);
		}
        function changeOpenStatus(id,openStatus){
            confirmx("确认" + (1 == openStatus ? "开放报名" : "关闭报名") + "？","${ctx}/ec/activity/changeOpenStatus?id="+id+"&openStatus="+openStatus);
        }
		function showDetail(id,title){
			top.$.jBox.open("iframe:${ctx}/ec/activity/offlineParticipationList?id="+id, title + " 活动参与情况",$(top.document).width()-220,$(top.document).height()-180,{
				buttons: {"确定": true},
				loaded: function(){
					$(".jbox-content", top.document).css("overflow-y", "hidden");
				}
			});
		}
	</script>
</head>
<body>
<form:form id="searchForm" modelAttribute="activityOffline" action="${ctx}/ec/activity/activityOfflineList" method="post" class="form-search">
	<div class="panel panel-info">
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				查询条件
			</div>
		</div>
		<div class="panel-body">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<label>标题：</label>
			<form:input path="title" htmlEscape="false" maxlength="50" placeholder="标题"/>&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		</div>
	</div>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-bordered table-hover">
	<thead>
	<tr>
		<th>V猫活动标题</th>
        <th width="100px">活动地址</th>
		<th width="50px">活动席位</th>
		<th width="50px">剩余席位</th>
        <th width="50px">已报名人数</th>
		<th width="120px">活动开始时间</th>
		<th width="120px">活动结束时间</th>
		<th width="80px">活动激活状态</th>
		<th width="120px">激活时间</th>
        <th width="80px">报名状态</th>
		<th width="200px">操作</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${page.list}" var="activity">
		<tr>
			<td><a href="${ctx}/ec/activity/activityOfflineForm?id=${activity.id}">${fns:abbr(activity.title,50)}</a></td>
            <td title="${activity.address}">${fns:abbr(activity.address,17)}</td>
			<td>${activity.seat}&nbsp;席</td>
			<td>${activity.lastSeat}&nbsp;席</td>
            <td>${activity.seat - activity.lastSeat}&nbsp;人</td>
			<td><fmt:formatDate value="${activity.startDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			<td><fmt:formatDate value="${activity.endDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			<td style="color:<c:if test="${0 eq activity.isActivate}">red</c:if><c:if test="${1 eq activity.isActivate}">green</c:if>">${fns:getDictLabel(activity.isActivate,'ec_activate' , '未知状态')}</td>
			<td><fmt:formatDate value="${activity.activateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td style="color:<c:if test="${0 eq activity.openStatus}">red</c:if><c:if test="${1 eq activity.openStatus}">green</c:if>">${fns:getDictLabel(activity.openStatus,'ec_activity_offline_open_status' , '未知状态')}</td>
			<td>
				<c:if test="${0 eq activity.isActivate}"><a href="javascript:void(0);" onclick="checkActivate('${activity.id}')">激活</a></c:if>
				<a href="${ctx}/ec/activity/activityOfflineForm?id=${activity.id}">修改</a>
				<c:if test="${0 eq activity.isActivate}"><a href="${ctx}/ec/activity/deleteActivityOffline?id=${activity.id}" onclick="return confirmx('确定要删除 ${activity.title}？',this.href);">删除</a></c:if>
				<c:if test="${1 eq activity.isActivate}"><a href="javascript:void(0);" onclick="showDetail('${activity.id}','${activity.title}')">参与人员</a></c:if>
                <c:if test="${1 eq activity.isActivate}"><a href="javascript:void(0);" onclick="changeOpenStatus('${activity.id}','${activity.openStatus == 0 ? 1 : 0}')">${fns:getDictLabel((activity.openStatus == 0 ? 1 : 0), 'ec_activity_offline_open_status', '开放报名')}</a></c:if>
			</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>