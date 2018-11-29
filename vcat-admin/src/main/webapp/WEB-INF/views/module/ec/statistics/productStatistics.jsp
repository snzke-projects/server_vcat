<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>对账单</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
            $("#contentTable").datagrid(getDatagridOption({
                singleSelect: true,
                frozenColumns:[[
                    {field:'supplierName',title:'供应商',width:150}
                    ,{field:'brandName',title:'品牌',width:150}
                    ,{field:'name',title:'商品名称',width:600}
                ]],
                columns:[[
                    {field:'retailPrice',title:'售价',width:100},
                    {field:'saleEarning',title:'销售利润',width:100},
                    {field:'totalReturns',title:'总销售额',width:60},
                    {field:'totalSales',title:'总销量',width:60},
                    {field:'repeatCount',title:'复购次数',width:60,
                        formatter: function(value,row){
                            return '<a href="javascript:void(0)" onclick="showBuyerList(\'' + row.supplierId + '\',\'' + row.brandId + '\',\'' + row.id + '\',\'' + row.name.toJQS() + '\')">' + value + '</a>';
                        }
                    },
                    {field:'buyerCount',title:'购买店主',width:60,
                        formatter: function(value,row){
                            return '<a href="javascript:void(0)" onclick="showBuyerList(\'' + row.supplierId + '\',\'' + row.brandId + '\',\'' + row.id + '\',\'' + row.name.toJQS() + '\')">' + value + '</a>';
                        }
                    },
                    {field:'sellerCount',title:'销售店主',width:60,
                        formatter: function(value,row){
                            return '<a href="javascript:void(0)" onclick="showSellerList(\'' + row.supplierId + '\',\'' + row.brandId + '\',\'' + row.id + '\',\'' + row.name.toJQS() + '\')">' + value + '</a>';
                        }},
                    {field:'proxyCount',title:'代理店主',width:60,
                        formatter: function(value,row){
                            return '<a href="javascript:void(0)" onclick="showSellerList(\'' + row.supplierId + '\',\'' + row.brandId + '\',\'' + row.id + '\',\'' + row.name.toJQS() + '\')">' + value + '</a>';
                        }},
                    {field:'salesRanking',title:'销量排行',width:60},
                    {field:'normalOrderCount',title:'普通订单',width:60},
                    {field:'sampleOrderCount',title:'拿样订单',width:60},
                    {field:'showDetail',title:'详情',width:100,
                        formatter: function(value,row){
                            return '<a href="javascript:void(0)" onclick="stopBubble();showViewPage(\''
                                    + ctx + '/ec/statistics/productOrder?'
                                    + 'sqlMap[\\\'productId\\\']=' + row.id
                                    + '&sqlMap[\\\'productName\\\']=' + row.name
                                    + '&sqlMap[\\\'supplierId\\\']=' + $('#supplierId').val()
                                    + '&sqlMap[\\\'brandId\\\']=' + $('#brandId').val()
                                    + '&sqlMap[\\\'ost\\\']=' + $('#ost').val()
                                    + '&sqlMap[\\\'oet\\\']=' + $('#oet').val()
                                    + '\',\'' + row.name + ' 订单详情\')">查看商品订单</a>';
                        }
                    }
                ]]
            }));
        });

        function showBuyerList(supplierId,brandId,productId,productName){
            stopBubble();
            showViewPage(ctx + "/ec/statistics/buyerList?supplierId=" + supplierId + "&brandId=" + brandId + "&id=" + productId,productName + " 购买用户列表");
        }
        function showSellerList(supplierId,brandId,productId,productName){
            stopBubble();
            showViewPage(ctx + "/ec/statistics/sellerList?supplierId=" + supplierId + "&brandId=" + brandId + "&id=" + productId,productName + " 代理店铺列表");
        }
        function exportStatistics(){
            top.$.jBox.confirm("确认导出？","系统提示",function(v){
                if(v=="ok"){
                    $("#searchForm").attr("action",ctx + "/ec/statistics/exportProductStatistics");
                    $("#searchForm").submit();
                    $("#searchForm").attr("action",ctx + "/ec/statistics/productStatistics");
                    closeTip();
                }
            },{buttonsFocus:1});
        }
        function clearForm(){
            $('#supplierId').val("");
            $('#supplierId').select2();
            $('#brandId').val("");
            $('#brandId').select2();
            $('#keyWord').val("");
            $('#ost').val("");
            $('#oet').val("");
            $('#orderBy').val("1");
            $('#orderBy').select2();
            $('#top').val("");
            $('#top').select2();
            $('#countLimit').val("");
        }
        function reload(){
            reloadGrid('/ec/statistics/productStatisticsData');
        }
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="productStatistics" action="${ctx}/ec/statistics/productStatistics" method="post" class="form-search" onsubmit="loading()">
		<div class="panel panel-info">
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					查询条件
				</div>
			</div>
			<div class="panel-body">
                <input id="countLimit" name="countLimit" type="hidden" value="${productStatistics.sqlMap.top}"/>
                <label>供应商：</label>
                <form:select id="supplierId" path="sqlMap['supplierId']" class="input-xxlarge">
                    <form:option value="" label="全部"/>
                    <form:options items="${supplierList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
                </form:select>
                <label>品牌：</label>
                <form:select id="brandId" path="sqlMap['brandId']" class="input-xxlarge">
                    <form:option value="" label="全部"/>
                    <form:options items="${brandList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
                </form:select><br>
                <div style="margin-top: 5px">
                    <label>下单时间：</label>
                    <input id="ost" type="text" name="sqlMap['ost']" value="${productStatistics.sqlMap['ost']}" readonly="readonly" style="width: 120px;" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
                    <span>&nbsp;至&nbsp;</span>
                    <input id="oet" type="text" name="sqlMap['oet']" value="${productStatistics.sqlMap['oet']}" readonly="readonly" style="width: 120px;" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
                    <label>商品名称：</label>
                    <form:input id="keyWord" path="sqlMap['keyWord']" htmlEscape="false" maxlength="50" class="input-xlarge" placeholder="商品名称"/>
                    <label>排序：</label>
                    <form:select id="orderBy" path="sqlMap['orderBy']" class="input-medium">
                        <form:option value="1" label="销量从高到低"/>
                        <form:option value="2" label="销量从低到高"/>
                        <form:option value="3" label="售价从高到低"/>
                        <form:option value="4" label="售价从低到高"/>
                        <form:option value="5" label="销售利润从高到低"/>
                        <form:option value="6" label="销售利润从低到高"/>
                    </form:select>
                    <label>TOP：</label>
                    <form:select id="top" path="sqlMap['top']" class="input-small" onchange="$('#countLimit').val(this.value)">
                        <form:option value="" label="全部"/>
                        <form:option value="5" label="5"/>
                        <form:option value="10" label="10"/>
                        <form:option value="20" label="20"/>
                        <form:option value="50" label="50"/>
                        <form:option value="100" label="100"/>
                    </form:select>
                    &nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="reload()"/>
                    &nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出" onclick="exportStatistics()"/>
                    &nbsp;<input id="btnClear" class="btn" type="button" value="清空" onclick="clearForm()"/>
                </div>
			</div>
		</div>
	</form:form>
	<sys:message content="${message}"/>
    <table id="contentTable"></table>
    <span class="help-inline" style="color: green;">
        商品销量统计说明：<br>
            1.此列表为查询符合条件的已支付的普通、拿样、庄园商品和团购商品订单<br>
            2.如果订单包含未完成的退款单，则不参与统计<br>
            3.具体详情可点击右边的[查看商品订单]超链接<br>
            4.复购次数：<br>
                同一买家购买同一商品超过一次的次数，仅计算订单数量，不计算订单中购买商品数量，可点击[复购次数]查看详情<br>
            5.购买店主：<br>
                购买此商品的店主数量，可点击[购买店主]查看详情<br>
            6.代理店主：<br>
                代理此商品的店主数量，可点击[代理店主]查看详情<br>
    </span>
</body>
</html>