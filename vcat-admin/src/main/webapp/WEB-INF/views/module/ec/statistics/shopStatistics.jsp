<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>店铺销量统计表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
            $("#contentTable").datagrid(getDatagridOption({
                singleSelect: true,
                frozenColumns:[[
                    {field:'name',title:'店铺名称',width:150}
                    ,{field:'phone',title:'联系方式',width:100}
                ]],
                columns:[[
                    {field:'totalReturns',title:'总销售额',width:100},
                    {field:'totalSales',title:'总销量',width:100},
                    {field:'saleEarning',title:'销售利润',width:100},
                    {field:'bonusEarning',title:'销售分红',width:100},
                    {field:'firstBonusEarning',title:'一级团队分红',width:120},
                    {field:'secondBonusEarning',title:'二级团队分红',width:120},
                    {field:'showDetail',title:'详情',width:100,
                        formatter: function(value,row){
                            return '<a href="javascript:void(0)" onclick="stopBubble();showOrderList(\'' + row.id + '\',\'' + row.name + '\')">查看店铺订单</a>';
                        }
                    }
                ]]
            }));
        });
        function exportStatistics(){
            top.$.jBox.confirm("确认导出？","系统提示",function(v){
                if(v=="ok"){
                    $("#searchForm").attr("action",ctx + "/ec/statistics/exportShopStatistics");
                    $("#searchForm").submit();
                    $("#searchForm").attr("action",ctx + "/ec/statistics/shopStatistics");
                    closeTip();
                }
            },{buttonsFocus:1});
        }
        function clearForm(){
            $('#keyWord').val("");
            $('#ost').val("");
            $('#oet').val("");
            $('#orderBy').val("1");
            $('#orderBy').select2();
            $('#top').val("");
            $('#top').select2();
        }

        function showOrderList(shopId,shopName){
            showViewPage(ctx + '/ec/statistics/shopOrder?' +
                    'sqlMap[\'shopId\']=' + shopId
                    + '&sqlMap[\'shopName\']=' + shopName
                    + '&sqlMap[\'ost\']=' + $('#ost').val()
                    + '&sqlMap[\'oet\']=' + $('#oet').val() , shopName + '订单详情');
        }
        function reload(){
            reloadGrid('/ec/statistics/shopStatisticsData');
        }
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="shopStatistics" action="${ctx}/ec/statistics/shopStatistics" method="post" class="form-search" onsubmit="loading()">
		<div class="panel panel-info">
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					查询条件
				</div>
			</div>
			<div class="panel-body">
                <input id="countLimit" name="countLimit" type="hidden" value="${shopStatistics.sqlMap.top}"/>
                <label>关键字：</label>
                <form:input id="keyWord" path="sqlMap['keyWord']" htmlEscape="false" maxlength="50" class="input-xlarge" placeholder="店铺名称、店铺手机号、店铺编号"/>
                <label>排序：</label>
                <form:select id="orderBy" path="sqlMap['orderBy']" class="input-medium">
                    <form:option value="1" label="销量从高到低"/>
                    <form:option value="2" label="销量从低到高"/>
                    <form:option value="3" label="销售额从高到低"/>
                    <form:option value="4" label="销售额从低到高"/>
                    <form:option value="5" label="销售利润从高到低"/>
                    <form:option value="6" label="销售分红从高到低"/>
                </form:select><br>
                <div style="margin-top: 5px">
                    <label>下单时间：</label>
                    <input id="ost" type="text" name="sqlMap['ost']" value="${shopStatistics.sqlMap['ost']}" readonly="readonly" style="width: 120px;" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
                    <span>&nbsp;至&nbsp;</span>
                    <input id="oet" type="text" name="sqlMap['oet']" value="${shopStatistics.sqlMap['oet']}" readonly="readonly" style="width: 120px;" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
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
        店铺销量统计说明：<br>
            1.此列表为查询符合条件的已支付的普通、拿样、庄园商品和团购订单统计详情<br>
            2.如果订单包含未完成的退款单，则不参与统计<br>
            3.具体详情可点击右边的[查看店铺订单]超链接
    </span>
</body>
</html>