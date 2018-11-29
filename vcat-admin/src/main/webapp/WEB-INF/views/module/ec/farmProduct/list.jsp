<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>庄园关联产品列表</title>
	<meta name="decorator" content="default"/>
    <style>
        /*.datagrid-cell{overflow: visible;height: auto;}*/
    </style>
	<script type="text/javascript">
        $(function (){
            $("#btnSubmit").click(function(){
                reloadGrid('/ec/farmProduct/listData');
            });
            $('#contentTable').datagrid(getDatagridOption({
                url:ctx + '/ec/farmProduct/listData',
                columns:[[
                    {field:'farm',title:'庄园',formatter:function(value,row){return row.farm.name}},
                    {field:'wechatNo',title:'服务微信号',width:100},
                    {field:'productNames',title:'庄园商品',formatter:function(value,row){return value ? ('<span title="' + value + '">' + value.replace(/[\r]/g,'</br>') + '</span>') : ""}},
                    {field:'action',title:'操作',width:130,formatter:function(value,row){
                        var html = '<a href="' + ctx + '/ec/farmProduct/form?farm.id=' + row.farm.id + '">查看详情</a>';
                        html += ' <a href="javascript:void(0)" onclick="toSetWechat(\'' + row.farm.id + '\',\'' + getDefault(row.wechatNo,'') + '\')">编辑微信号</a>'
                        return html;
                    }}
                ]]
            }));
        });
        function toSetWechat(id,oldWechatNo){
            $('#id').val(id);
            $('#wechatNo').val(oldWechatNo);
            showDialog('setWechatNoDialog');
        }
        function updateWechat(){
            $.get(ctx + '/ec/farmProduct/updateWechatNo?id=' + $('#id').val() + '&wechatNo=' + $('#wechatNo').val(),function (){
                alertx('保存成功！',function (){
                    showDialog('setWechatNoDialog');
                    reloadGrid('/ec/farmProduct/listData');
                    $('#id').val("");
                    $('#wechatNo').val("");
                });
            });
        }
	</script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/ec/farmProduct/list">庄园关联产品列表</a></li>
</ul>
<form:form id="searchForm" modelAttribute="farmProduct" action="${ctx}/ec/farmProduct/listData" method="post" class="form-search">
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
		</div>
	</div>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable"></table>
<div id="setWechatNoDialog" class="vcat-dialog" style="width: 300px;">
    <div class="panel panel-info">
        <div class="panel-heading">
            <div class="panel-title" style="font-size: 14px;">
                设置服务微信号
                <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('setWechatNoDialog')"></a>
            </div>
        </div>
        <div class="panel-body">
            <table class="table table-bordered table-hover">
                <tr>
                    <th width="20%"><span class="pull-right">微信号：</span></th>
                    <td width="80%"><input id="wechatNo" type="text" class="input-medium" maxlength="50"></td>
                </tr>
            </table>
            <div class="form-actions">
                <input type="hidden" id="id">
                <input class="btn btn-primary" type="button" value="保存" onclick="updateWechat()"/>&nbsp;
                <input id="btnCancel" class="btn" type="button" value="返 回" onclick="showDialog('setWechatNoDialog')"/>
            </div>
        </div>
    </div>
</div>
</body>
</html>