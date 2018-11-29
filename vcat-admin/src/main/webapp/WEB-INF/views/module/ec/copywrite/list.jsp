<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>文案管理</title>
	<meta name="decorator" content="default"/>
    <script type="application/javascript">
        $(function(){
            $("#contentTable").datagrid(getDatagridOption({
                url : ctx + '/ec/copywrite/listData?'+$('#searchForm').serialize(),
                columns:[[
                    {field:'product',title:'商品名称',width:500,formatter:function(value,row){
                        return '<a href="javascript:void(0)" onclick="stopBubble();showViewPage(ctx+\'/ec/product/form?id=' + value.id + '&isView=true\',\'' + value.name + ' 商品详情\')">' + value.nameLabel + '</a>';
                    }},
                    {field:'title',title:'文案标题',width:300,formatter:function(value,row){
                        return value ? '<a href="' + ctx + '/ec/copywrite/form?id=' + row.id + '">' + value + '</a>' : "";
                    }},
//                    {field:'content',title:'文案内容',formatter:function(value,row){return '<span title="' + value + '">' + abbr(value,100) + '</span>'}},
                    {field:'isActivate',title:'激活状态',width:100,formatter:function(value,row){
                        return value ? "已激活" : "未激活";
                    },styler:function (value,row){
                        return "color:" + (value ? "green" : "red");
                    }},
                    {field:'activateTime',title:'激活时间',width:130},
                    {field:'oper',title:'操作',width:100,formatter: function(value,row){
                        var html = '';
                        var aTitle = row.title ? row.title : row.product.name;
                        html += '<a href="javascript:void(0)" onclick="activate(\'' + row.id + '\',\'' + row.isActivate + '\',\'' + aTitle.toJQS() + '\')">' + (row.isActivate ? "取消" : "") + '激活</a>';
                        html += ' <a href="' + ctx + '/ec/copywrite/form?id=' + row.id + '">修改</a>';
                        return html;
                    }}
                ]]
            }));
        });
        function activate(id,isActivate,title){
            stopBubble();
            if(title && title != "undefined"){
                title = '【' + title + '】'
            }else{
                title = '';
            }
            var msgTitle = isActivate === 'true' ? "<span style='color:red'>取消</span>" : "";
            confirmx('确认' + msgTitle + '激活' + title + '？',function (){
                $.get(ctx + '/ec/copywrite/activate?id=' + id + '&isActivate=' + (isActivate === 'true' ? false : true),function (){
                    alertx(msgTitle + "激活" + title + "成功！",function (){reloadGrid('/ec/copywrite/listData')},700);
                });
            },null,700);
        }
        function clearForm(){
            $('#isActivate').val('');
            $('#isActivate').select2();
            $('#keyWord').val('');
            $('#st').val('');
            $('#et').val('');
        }
    </script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/ec/copywrite/list">文案列表</a></li>
		<shiro:hasPermission name="ec:copywrite:edit"><li><a href="${ctx}/ec/copywrite/form">添加文案</a></li></shiro:hasPermission>
	</ul>
    <form:form id="searchForm" modelAttribute="copywrite" action="${ctx}/ec/copywrite/list" method="post" class="breadcrumb form-search">
        <label>关键字：</label>
        <form:input id="keyWord" path="sqlMap['keyWord']" maxlength="50" cssClass="input-medium" placeholder="商品名称、文案标题"/>&nbsp;
        <label>激活状态：</label>
        <form:select path="isActivate" cssClass="input-medium">
            <form:option value="" label="全部"/>
            <form:option value="true" label="已激活"/>
            <form:option value="false" label="未激活"/>
        </form:select>
        <label>激活时间：</label>
        <form:input id="st" path="sqlMap['st']" readonly="readonly" maxlength="10" cssClass="Wdate" cssStyle="width: 100px;" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
        <label>至&nbsp;&nbsp;</label>
        <form:input id="et" path="sqlMap['et']" readonly="readonly" maxlength="10" cssClass="Wdate" cssStyle="width: 100px;" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
        <input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="reloadGrid('/ec/copywrite/listData')"/>
        <input id="btnClear" class="btn btn-default" type="button" value="清空" onclick="clearForm()"/>
    </form:form>
	<sys:message content="${message}"/>
    <table id="contentTable"></table>
</body>
</html>