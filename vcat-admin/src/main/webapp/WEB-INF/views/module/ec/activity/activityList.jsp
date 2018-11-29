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
			confirmx("激活后不能删除该体验官，确认激活？","${ctx}/ec/activity/activateActivity?id="+id);
		}
		function showDetail(id,title){
			top.$.jBox.open("iframe:${ctx}/ec/activity/participationList?id="+id, title + " 体验官参与情况",$(top.document).width()-220,$(top.document).height()-180,{
				buttons: {"确定": true},
				loaded: function(){
					$(".jbox-content", top.document).css("overflow-y", "hidden");
				}
			});
		}
        function viewReport(id,title){
            top.$.jBox.open("iframe:${ctx}/ec/activity/viewActivityQuestionnaireReport?activityId="+id, title + " 问卷统计",$(top.document).width()-220,$(top.document).height()-180,{
                buttons: {"确定": true},
                loaded: function () {
                    $(".jbox-content", top.document).css("overflow-y", "hidden");
                },
                closed:function(){
                    $("#searchForm").submit()
                }
            });
        }
        function activateFeedback(id,isActivate){
            var title = isActivate == 1 ? "取消" : "";
            isActivate = isActivate == 1 ? 0 : 1;
            $.getJSON(ctx + "/ec/activity/activateFeedback?id="+id+"&isActivate="+isActivate,function(){
                alertx(title + "激活报告成功！",function(){$("#searchForm").submit()});
            });
        }
	</script>
</head>
<body>
<form:form id="searchForm" modelAttribute="activity" action="${ctx}/ec/activity/activityList" method="post" class="form-search">
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
		<th>V猫体验官标题</th>
		<th width="50px">体验官席位</th>
		<th width="50px">剩余席位</th>
        <th width="60px">费用名称</th>
		<th width="50px">费用</th>
		<th width="120px">体验官开始时间</th>
		<th width="120px">体验官结束时间</th>
		<th width="80px">体验官激活状态</th>
		<th width="120px">激活时间</th>
        <th width="80px">报告发布状态</th>
        <th width="80px">报告激活状态</th>
		<th width="200px">操作</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${page.list}" var="activity">
		<tr>
			<td><a href="${ctx}/ec/activity/activityForm?id=${activity.id}">${fns:abbr(activity.title,50)}</a></td>
			<td>${activity.seat}&nbsp;席</td>
			<td>${activity.lastSeat}&nbsp;席</td>
            <td>${activity.feeName}</td>
			<td>${activity.fee}&nbsp;元</td>
			<td><fmt:formatDate value="${activity.startDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			<td><fmt:formatDate value="${activity.endDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			<td style="color:<c:if test="${0 eq activity.isActivate}">red</c:if><c:if test="${1 eq activity.isActivate}">green</c:if>">${fns:getDictLabel(activity.isActivate,'ec_activate' , '未知状态')}</td>
			<td><fmt:formatDate value="${activity.activateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td style="color:<c:if test="${0 eq activity.feedBackStatus}">red</c:if><c:if test="${1 eq activity.feedBackStatus}">green</c:if>">${fns:getDictLabel(activity.feedBackStatus,'ec_feedback_status' , '未知状态')}</td>
            <td style="color:<c:if test="${0 eq activity.feedBack.isActivate}">red</c:if><c:if test="${1 eq activity.feedBack.isActivate}">green</c:if>">${fns:getDictLabel(activity.feedBack.isActivate,'ec_activate' , '未知状态')}</td>
			<td>
				<c:if test="${0 eq activity.isActivate}"><a href="javascript:void(0);" onclick="checkActivate('${activity.id}')">激活</a></c:if>
				<a href="${ctx}/ec/activity/activityForm?id=${activity.id}">修改</a>
				<c:if test="${0 eq activity.isActivate}"><a href="${ctx}/ec/activity/deleteActivity?id=${activity.id}" onclick="return confirmx('确定要删除 ${activity.title}？',this.href);">删除</a></c:if>
				<c:if test="${1 eq activity.isActivate}"><a href="javascript:void(0);" onclick="showDetail('${activity.id}','${activity.title}')">参与人员</a></c:if>
                <c:if test="${1 eq activity.isActivate}"><a href="javascript:void(0);" onclick="viewReport('${activity.id}','${activity.title}')">发布报告</a></c:if>
                <c:if test="${null != activity.feedBack && 0 eq activity.feedBack.isActivate}"><a href="javascript:void(0);" onclick="activateFeedback('${activity.id}','${activity.feedBack.isActivate}')">激活报告</a></c:if>
			</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>