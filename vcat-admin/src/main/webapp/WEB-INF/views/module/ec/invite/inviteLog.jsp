<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>邀请活动详情列表</title>
	<meta name="decorator" content="default"/>
    <script type="text/javascript">
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
        function getChartData(){
            return ${fns:toJson(chartList)};
        }
    </script>
</head>
<body>
<table id="contentTable" class="table table-bordered table-hover" style="width: 97%; margin: 10px auto 10px;">
    <tbody>
        <tr>
            <td width="20%">活动开始时间：</td>
            <td width="30%"><fmt:formatDate value="${inviteEarning.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td width="20%">活动结束时间：</td>
            <td width="30%"><fmt:formatDate value="${inviteEarning.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
        </tr>
        <tr>
            <td>可邀请次数：</td>
            <td>${inviteEarning.availableInvite}次</td>
            <td>已邀请次数：</td>
            <td>${inviteEarning.invitedCount}次</td>
        </tr>
        <tr>
            <td>邀请人奖励：</td>
            <td>${inviteEarning.inviterEarning}元</td>
            <td>被邀请人奖励：</td>
            <td>${inviteEarning.inviteeEarning}元</td>
        </tr>
    </tbody>
</table>
<div class="panel panel-info">
    <div class="panel-heading">
        <div class="panel-title" style="font-size: 14px;">
            分享历史列表
        </div>
    </div>
    <div class="panel-body">
        <form:form id="searchForm" action="${ctx}/ec/invite/inviteLog" method="post" class="breadcrumb form-search">
            <input type="hidden" name="id" value="${inviteEarning.id}">
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <table id="logTable" class="table table-bordered table-condensed table-hover">
                <thead>
                <tr>
                    <th width="30%">邀请人</th>
                    <th width="30%">被邀请人</th>
                    <th width="15%">邀请人获得奖励</th>
                    <th width="15%">被邀请人获得奖励</th>
                    <th width="10%">邀请时间</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${page.list}" var="invite">
                    <tr>
                        <td>${invite.inviter}</td>
                        <td>${invite.invitee}</td>
                        <td>${invite.inviterEarning}</td>
                        <td>${invite.inviteeEarning}</td>
                        <td><fmt:formatDate value="${invite.date}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <div class="pagination">${page}</div>
        </form:form>
    </div>
</div>
<charts:pie id="pie" title="邀请活动统计表" dataCallBack="getChartData" unit="人"></charts:pie>
</body>
</html>