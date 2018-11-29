<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>建议反馈管理</title>
	<meta name="decorator" content="default"/>
    <script type="application/javascript">
        $(function (){
            $("#btnSubmit").click(function(){
                reloadGrid('/ec/groupBuying/activityListData');
            });
            $('#contentTable').datagrid(getDatagridOption({
                url:ctx + "/ec/groupBuying/activityListData",
                frozenColumns:[[            // 锁定列
                    {field:'name',title:'团购名称',width:300,formatter:function(value,row){
                        return row.groupBuying.name;
                    }},
                    {field:'typeLabel',title:'团购类型',width:140},
                ]],
                columns:[[
                    {field:'sponsor',title:'团长',width:100,formatter:function(value,row){return value.userName}},
                    {field:'joinCount',title:'拼团人数',width:100},
                    {field:'startDate',title:'团购开始时间',width:130},
                    {field:'endDate',title:'团购结束时间',width:130},
                    {field:'statusLabel',title:'团购状态',width:100
                        ,styler:function(value,row){return 'color:' + row.statusColor}},
                    {field:'action',title:'操作',width:70,formatter:function(value,row){
                        return '<a href="javascript:void(0)" onclick="stopBubble();showOrderList(\'' + row.id + '\',\'' + row.groupBuying.name + '\')">查看订单</a>';
                    }}
                ]]
            }));
        });
        function showOrderList(groupBuyingId,groupBuyingName){
            showViewPage(ctx + '/ec/groupBuying/groupBuyingOrder?' +
                    'sqlMap[\'groupBuyingId\']=' + groupBuyingId
                    + '&sqlMap[\'groupBuyingName\']=' + groupBuyingName
                    + '&sqlMap[\'skipGroupBuying\']=no', groupBuyingName + '订单详情');
        }
    </script>
</head>
<body>
	<sys:message content="${message}"/>
    <form:form id="searchForm" modelAttribute="sponsor" method="post" class="form-search">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    查询条件
                </div>
            </div>
            <div class="panel-body">
                <label>关键字：</label>
                <form:input id="keyWord" path="sqlMap['keyWord']" maxlength="50" class="input-xlarge" placeholder="团长名称、团购名称"/>&nbsp;
                <label>团购类型：</label>
                <form:select path="type" cssClass="input-medium">
                    <form:option value="" label="全部"></form:option>
                    <form:option value="0" label="0元团购"></form:option>
                    <form:option value="1" label="卖家团购"></form:option>
                    <form:option value="2" label="买家团购"></form:option>
                </form:select>
                <label>拼团状态：</label>
                <form:select path="status" cssClass="input-medium">
                    <form:option value="" label="全部"></form:option>
                    <form:option value="0" label="进行中"></form:option>
                    <form:option value="1" label="拼团成功"></form:option>
                    <form:option value="2" label="拼团失败"></form:option>
                </form:select>
                &nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
            </div>
        </div>
    </form:form>
    <sys:message content="${message}"/>
    <table id="contentTable"></table>
</body>
</html>