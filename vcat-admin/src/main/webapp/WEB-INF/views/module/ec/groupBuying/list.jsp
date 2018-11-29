<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>建议反馈管理</title>
	<meta name="decorator" content="default"/>
    <script type="application/javascript">
        $(function (){
            $("#btnSubmit").click(function(){
                reloadGrid('/ec/groupBuying/listData');
            });
            $('#contentTable').datagrid(getDatagridOption({
                url:ctx + "/ec/groupBuying/listData",
                frozenColumns:[[            // 锁定列
                    {field:'name',title:'团购名称',width:140,formatter:function(value,row){
                        return '<a href="${ctx}/ec/groupBuying/form?id=' + row.id + '">' + value + '</a>';
                    }},
                    {field:'productName',title:'团购商品',width:400,formatter:function(value,row){return row.product.name}},
                ]],
                columns:[[
                    {field:'price',title:'团购价',width:100},
                    {field:'isLimit',title:'是否限购',width:100
                        ,styler:function(value,row){return 'color:' + (value ? "orange" : "cornflowerblue")}
                        ,formatter:function(value,row){return value ? "是" : "否"}
                    },
                    {field:'times',title:'限购数量',width:70},
                    {field:'inventory',title:'团购库存',width:100},
                    {field:'neededPeople',title:'拼团人数',width:100},
                    {field:'endDate',title:'团购结束时间',width:130},
                    {field:'statusLabel',title:'团购状态',width:100
                        ,styler:function(value,row){return 'color:' + row.statusColor}},
                    {field:'displayOrder',title:'团购排序',width:60},
                    {field:'action',title:'操作',width:70,formatter:function(value,row){
                        var html = '';
                        if(row.status == 0){
                            html += '<a href="javascript:void(0)" onclick="stopBubble();activate(\'' + row.id + '\',\'' + row.product.id + '\',\'' + row.name + '\')">激活</a>';
                        }
                        html += ' <a href="${ctx}/ec/groupBuying/form?id=' + row.id + '">修改</a>';
                        return html;
                    }}
                ]]
            }));
        });
        function activate(id,productId,name){
            confirmx("确认激活" + name + "？",function (){
                $.get(ctx + "/ec/groupBuying/activate?id=" + id + "&product.id=" + productId,function(){
                    alertx(name + "激活成功！",function(){
                        reloadGrid('/ec/groupBuying/listData');
                    });
                });
            });
        }
    </script>
</head>
<body>
	<sys:message content="${message}"/>
    <form:form id="searchForm" modelAttribute="groupBuying" method="post" class="form-search">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    查询条件
                </div>
            </div>
            <div class="panel-body">
                <label>关键字：</label>
                <form:input id="keyWord" path="sqlMap['keyWord']" maxlength="50" class="input-xlarge" placeholder="商品名称、团购名称"/>&nbsp;
                <label>活动状态：</label>
                <form:select path="status" cssClass="input-medium">
                    <form:option value="">全部</form:option>
                    <form:option value="0">未激活</form:option>
                    <form:option value="1">进行中</form:option>
                    <form:option value="2">已结束</form:option>
                </form:select>
                &nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
            </div>
        </div>
    </form:form>
    <sys:message content="${message}"/>
    <table id="contentTable"></table>
</body>
</html>