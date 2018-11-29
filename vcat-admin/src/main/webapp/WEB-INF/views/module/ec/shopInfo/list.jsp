<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>庄园信息列表</title>
	<meta name="decorator" content="default"/>
    <style>
        .datagrid-row{height:60px}
        .datagrid-cell{overflow: visible;height: auto;}
    </style>
	<script type="text/javascript">
        $(function (){
            $("#btnExport").click(function(){
                confirmx("确认导出？",function(v){
                    loading("正在导出，请稍等");
                    $("#searchForm").attr("action",ctx + "/ec/shopInfo/exportList");
                    $("#searchForm").submit();
                    $("#searchForm").attr("action",ctx + "/ec/shopInfo/listData");
                    closeTip();
                });
            });
            $("#btnSubmit").click(function(){
                reloadGrid('/ec/shopInfo/listData');
            });
            $('#contentTable').datagrid(getDatagridOption({
                url:ctx + "/ec/shopInfo/listData",
                columns:[[
                    {field:'shopName',title:'小店名称',width:140,formatter:function(value,row){return row.shop.name}},
                    {field:'shopPhone',title:'小店手机号',width:100},
                    {field:'productName',title:'庄园',formatter:function(value,row){return row.product.name}},
                    {field:'farmName',title:'庄园名称',width:100},
                    {field:'realName',title:'真实姓名',width:100},
                    {field:'baseCardInventory',title:'基地卡片库存',width:100},
                    {field:'companyCardInventory',title:'公司卡片库存',width:100},
                    {field:'qrCodeUrlPath',title:'微信二维码',width:70,formatter:function(value,row){
                        return '<img src="' + value + '" title="微信二维码" alt="微信二维码" style="width: 50px;height: 50px"/>';
                    }},
                    {field:'note',title:'备注',width:200},
                    {field:'action',title:'操作',width:70,formatter:function(value,row){
                        return '<a href="${ctx}/ec/shopInfo/form?id=' + row.id + '">编辑</a>';
                    }}
                ]]
            }));
        });
	</script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/ec/shopInfo/list">庄园主信息列表</a></li>
    <shiro:hasPermission name="ec:shopInfo:edit"><li><a href="${ctx}/ec/shopInfo/form">设置庄园主</a></li></shiro:hasPermission>
</ul>
<form:form id="searchForm" modelAttribute="shopInfo" method="post" class="form-search">
	<div class="panel panel-info">
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				查询条件
			</div>
		</div>
		<div class="panel-body">
            <label>关键字：</label>
            <form:input id="keyWord" path="sqlMap['keyWord']" maxlength="50" class="input-xlarge" placeholder="店铺名、商品名、真实名称、庄园名"/>&nbsp;
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
            &nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
		</div>
	</div>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable"></table>
</body>
</html>