<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>用户查询</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        var openWindow = getOpenWindow();
        var selectedIds = "${selectedIds}";
        var idsKey = "${idsKey}" == "" ? "selectedIds" : "${idsKey}";
        $(function (){
            $('#contentTable').datagrid(getDatagridOption({
                singleSelect: ${radio},
                url: '${ctx}/ec/customer/listData?' + $('#searchForm').serialize(),
                columns: [[
                    {field: 'userName', title: '用户名', width: 100},
                    {field: 'phoneNumber', title: '手机号', width: 120},
                    {field: 'from', title: '注册来源', width: 140},
                    {field: 'createDate', title: '注册时间', width: 140},
                    {field: 'advancedShop', title: '用户等级', width: 80, styler:function(value, row){
                        return 'color:' + (row.shop && row.shop.advancedShop == 1 ? 'orange' : 'cornflowerblue');
                    }, formatter: function (value, row){
                        return row.shop && row.shop.advancedShop == 1 ? '钻石小店' : '特约小店';
                    }}
                ]],
                onSelect : function (index, row){
                    updateSelectedIds(row.id,true);
                    eval("openWindow." + idsKey + " = $('#selectedIds').val()");
                },
                onUnselect : function (index, row){
                    updateSelectedIds(row.id,false);
                    eval("openWindow." + idsKey + " = $('#selectedIds').val()");
                }
            }));
        });

        function updateSelectedIds(id,checked){
            var ids = $('#selectedIds').val();
            if('true' == '${radio}'){
                ids="|"+id;
            }else if(checked){
                if(ids.indexOf(id) < 0){
                    ids+="|"+id;
                }
            }else{
                ids = ids.replace("|"+id,"");
            }
            $('#selectedIds').val(ids);
        }
    </script>
</head>
<body>
<div class="panel panel-info">
    <div class="panel-heading">
        <div class="panel-title" style="font-size: 14px;">
            查询条件
        </div>
    </div>
    <div class="panel-body">
        <form:form id="searchForm" modelAttribute="customer" action="${ctx}/ec/customer/listData" method="post" class="breadcrumb form-search">
            <input name="type" type="hidden" value="${type}"/>
            <input name="radio" type="hidden" value="${radio}"/>
            <input name="sqlMap['type']" type="hidden" value="${customer.sqlMap.type}"/>
            <input id="selectedIds" name="selectedIds" type="hidden" value="${selectedIds}"/>
            <label>注册来源：</label>
            <form:select path="registered" cssStyle="width: 120px;">
                <form:option value="">全部</form:option>
                <form:option value="0">微信注册</form:option>
                <form:option value="1">客户端注册</form:option>
                <form:option value="2">网页接收邀请</form:option>
            </form:select>
            <label>用户等级：</label>
            <form:select path="shop.advancedShop" cssStyle="width: 120px;">
                <form:option value="">全部</form:option>
                <form:option value="0">特约小店</form:option>
                <form:option value="1">钻石小店</form:option>
            </form:select>
            <label>注册时间：</label>
            <input id="st" name="sqlMap['st']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width:135px;"
                   value="${customer.sqlMap['st']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});" title="注册时间大于该时间"/>
            <label style="margin-left: 0px;">至</label>
            <input id="et" name="sqlMap['et']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width:135px;"
                   value="${customer.sqlMap['et']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});" title="注册时间小于该时间"/>
            <label>关键字：</label>
            <form:input path="sqlMap['keyWord']" htmlEscape="false" maxlength="50" class="input-large" placeholder="用户名、手机号"/>
            <input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="reloadGrid('/ec/customer/listData')"/>
        </form:form>
    </div>
</div>
<div class="panel panel-info">
    <div class="panel-heading">
        <div class="panel-title" style="font-size: 14px;">
            用户列表
        </div>
    </div>
    <div class="panel-body">
        <table id="contentTable" style="width: 95%;"></table>
    </div>
</div>
</body>
</html>