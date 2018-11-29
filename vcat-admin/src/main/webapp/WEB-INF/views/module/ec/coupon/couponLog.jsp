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
            <td width="20%">券名称：</td>
            <td width="30%">${coupon.name}</td>
            <td width="20%">券金额：</td>
            <td width="30%">${coupon.fund}元</td>
        </tr>
        <tr>
            <td>活动开始时间：</td>
            <td><fmt:formatDate value="${coupon.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td>活动结束时间：</td>
            <td><fmt:formatDate value="${coupon.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
        </tr>
        <tr>
            <td>可邀请次数：</td>
            <td>${coupon.total}次</td>
            <td>已邀请次数：</td>
            <td>${coupon.couponUsedCount}次</td>
        </tr>
        <tr>
            <td>券类型：</td>
            <td colspan="3">${fns:getDictLabel(coupon.type, 'ec_coupon_type', '未知类型')}</td>
        </tr>
    </tbody>
</table>
<div class="panel panel-info">
    <div class="panel-heading">
        <div class="panel-title" style="font-size: 14px;">
            获取历史列表
        </div>
    </div>
    <div class="panel-body">
        <form:form id="searchForm" action="${ctx}/ec/coupon/couponLog" method="post" class="breadcrumb form-search">
            <input type="hidden" name="id" value="${coupon.id}">
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <table id="logTable" class="table table-bordered table-condensed table-hover">
                <thead>
                <tr>
                    <th width="10%">获取小店</th>
                    <th width="80%">备注</th>
                    <th width="10%">获取时间</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${page.list}" var="coupon">
                    <tr>
                        <td>${coupon.shopName}</td>
                        <td>${coupon.note}</td>
                        <td><fmt:formatDate value="${coupon.date}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
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