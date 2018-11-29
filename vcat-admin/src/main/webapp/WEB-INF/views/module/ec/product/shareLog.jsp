<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分享活动详情列表</title>
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
            <td width="20%">活动标题：</td>
            <td width="80%" colspan="3">${shareEarning.title}</td>
        </tr>
        <tr>
            <td>所属商品：</td>
            <td colspan="3">${shareEarning.product.name}</td>
        </tr>
        <tr>
            <td width="20%">活动开始时间：</td>
            <td width="30%"><fmt:formatDate value="${shareEarning.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td width="20%">活动结束时间：</td>
            <td width="30%"><fmt:formatDate value="${shareEarning.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
        </tr>
        <tr>
            <td>可分享次数：</td>
            <td>${shareEarning.availableShare}次</td>
            <td>单次分享奖励：</td>
            <td>${shareEarning.fund}元</td>
        </tr>
        <tr>
            <td>已分享次数：</td>
            <td>${shareEarning.sharedCount}次</td>
            <td>活动图片：</td>
            <td><img src="${shareEarning.imgUrlPath}" width="320" height="165" title="${shareEarning.title}"></td>
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
        <form:form id="searchForm" action="${ctx}/ec/product/shareLog" method="post" class="breadcrumb form-search">
            <input type="hidden" name="id" value="${shareEarning.id}">
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <table id="logTable" class="table table-bordered table-condensed table-hover">
                <thead>
                <tr>
                    <th width="60%">活动标题</th>
                    <th width="20%">小店名称</th>
                    <th width="10%">分享方式</th>
                    <th width="10%">分享时间</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${page.list}" var="share">
                    <tr>
                        <td>${share.title}</td>
                        <td>${share.shopName}</td>
                        <td>${share.typeName}</td>
                        <td><fmt:formatDate value="${share.shareTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <div class="pagination">${page}</div>
        </form:form>
    </div>
</div>
<charts:pie id="pie" title="${shareEarning.title} 分享统计表" dataCallBack="getChartData" unit="次"></charts:pie>
</body>
</html>