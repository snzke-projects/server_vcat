<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>概况</title>
	<meta name="decorator" content="default"/>
    <style>
        .leftTitle{
            display: block;
            width: 200px;
            float: left;
            text-align: right;
            padding-right: 20px;
        }
    </style>
</head>
<body>
    <form:form id="searchForm" modelAttribute="withdrawal" action="${ctx}/ec/statistics/overview" method="post" class="form-search">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    查询条件
                </div>
            </div>
            <div class="panel-body">
                <label>统计时间：</label>
                <input id="ost" name="st" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width: 150px;"
                       value="${st}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'});"/>
                <label>至&nbsp;&nbsp;</label>
                <input id="oet" name="et" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width: 150px;"
                       value="${et}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'});"/>
                &nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
                <table class="table table-hover table-bordered" style="margin-top: 5px;">
                    <thead><tr><th>统计概况</th></tr></thead>
                    <tbody>
                    <tr><td><span class="leftTitle">总订单：</span><span style="float: left">${orderCount} 个</span></td></tr>
                    <tr><td><span class="leftTitle">销售总额：</span><span style="float: left">￥ ${totalPrice} 元</span></td></tr>
                    <tr><td><span class="leftTitle">V猫币总额：</span><span style="float: left">￥ ${totalCoupon} 元</span></td></tr>
                    <tr><td><span class="leftTitle">平均客单价：</span><span style="float: left">￥ ${avgPrice} 元</span></td></tr>
                    <tr><td><span class="leftTitle">已接收邀请但未注册：</span><span style="float: left">${quasiSeller} 人</span></td></tr>
                    <tr><td><span class="leftTitle">注册买家：</span><span style="float: left">${newBuyer} 人</span></td></tr>
                    <tr><td><span class="leftTitle">注册卖家：</span><span style="float: left">${newSeller} 人</span></td></tr>
                    <tr><td><span class="leftTitle">普通订单：</span><span style="float: left">${normalOrderCount} 个</span></td></tr>
                    <tr><td><span class="leftTitle">拿样订单：</span><span style="float: left">${simpleOrderCount} 个</span></td></tr>
                    <tr><td><span class="leftTitle">全额抵扣订单：</span><span style="float: left">${fullDeductionOrderCount} 个</span></td></tr>
                    <tr><td><span class="leftTitle">部分抵扣订单：</span><span style="float: left">${partialDeductionOrderCount} 个</span></td></tr>
                    <tr><td><span class="leftTitle">体验官订单：</span><span style="float: left">${activityOrderCount} 个</span></td></tr>
                    <tr><td><span class="leftTitle">销售订单：</span><span style="float: left">${paidOrderCount} 个</span></td></tr>
                    <tr><td><span class="leftTitle">分享活动参与量：</span><span style="float: left">${shareCount} 个</span></td></tr>
                    </tbody>
                </table>
            </div>
        </div>
    </form:form>
	<table class="table table-hover table-bordered">
		<thead><tr><th>订单处理情况</th></tr></thead>
		<tbody>
            <tr><td><span class="leftTitle">所有订单：</span><span style="float: left">${allOrderCount} 个</span></td></tr>
            <tr><td><span class="leftTitle">已取消订单：</span><span style="float: left">${cancelOrderCount} 个</span></td></tr>
			<tr><td><span class="leftTitle">未付款订单：</span><span style="float: left">${nonPayment} 个</span></td></tr>
			<tr><td><span class="leftTitle">待确认订单：</span><span style="float: left">${toBeConfirmed} 个</span></td></tr>
			<tr><td><span class="leftTitle">待发货订单：</span><span style="float: left">${toBeShipped} 个</span></td></tr>
			<tr><td><span class="leftTitle">已发货订单：</span><span style="float: left">${shipped} 个</span></td></tr>
			<tr><td><span class="leftTitle">已完成订单：</span><span style="float: left">${completed} 个</span></td></tr>
		</tbody>
	</table>
	<table class="table table-hover table-bordered">
		<thead><tr><th>商品信息统计</th></tr></thead>
		<tbody>
		<tr><td><span class="leftTitle">商品总数：</span><span style="float: left">${productCount} 个</span></td></tr>
		<tr><td><span class="leftTitle">缺货商品：</span><span style="float: left">${outStockProductCount} 个</span></td></tr>
		</tbody>
	</table>
</body>
</html>