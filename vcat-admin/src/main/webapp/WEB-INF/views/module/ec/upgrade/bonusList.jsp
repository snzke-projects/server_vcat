<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>伯乐奖励列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
            $("#contentTable").datagrid(getDatagridOption({
                fitColumns : true,
                url : ctx + '/ec/upgrade/bonusListData?'+$('#searchForm').serialize(),
                columns:[[
                    {field:'id',checkbox:true},
                    {field:'originalParentName',title:'用户名',width:100,formatter: function(value,row){return row.originalParent.name;}},
                    {field:'originalParentPhone',title:'手机号',width:110,formatter: function(value,row){return row.originalParent.customer.phoneNumber;}},
                    {field:'shopName',title:'已升级成员用户名',width:100,formatter: function(value,row){return row.shop.name;}},
                    {field:'shopPhone',title:'已升级成员手机号',width:110,formatter: function(value,row){return row.shop.customer.phoneNumber;}},
                    {field:'statusLabel',title:'奖励状态',width:60,styler:function(value,row){return 'color:' + row.statusColor}},
                    {field:'amount',title:'奖励金额',width:100},
                    {field:'operDate',title:'操作时间',width:160},
                    {field:'operBy',title:'操作账号',width:100,formatter: function(value,row){return row.operBy ? row.operBy.name : "";}},
                    {field:'note',title:'备注',width:300,formatter: function(value,row){return row.note ? '<span title="' + row.note + '">' + row.note + '</span>' : "";}},
                    {field:'oper',title:'操作',width:60
                        ,formatter: function(value,row){
                            var html = "";
                            if(row.status === 0){
                                html += '<a href="javascript:void(0)" onclick="stopBubble();showIssueBonus(\'' + row.id + '\',\'' + row.originalParent.name + '\',\'' + row.shop.name + '\')">发放奖励</a>';
                            }
                            return html;
                        }
                    }
                ]]
            }));
        });
        function showIssueBonus(id,originalParentName,shopName){
            $('#upgradeRequestId').val(id);
            $('#originalParentName').html(originalParentName);
            $('#shopName').html(shopName);
            showDialog('bonusDialog');
        }
        function issueBonus(id,status,originalParentName){
            var bonus = $('#bonus').val();
            var note = $('#note').val();
            if(isNull(bonus) || !isMoney(bonus) || parseFloat(bonus) < 0){
                alertx("奖励金额格式不正确！",function (){
                    $('#bonus').focus();
                });
                return false;
            }
            if(1 != status && parseFloat(bonus) != 0){
                alertx("奖励金额为零时才能拒绝发放！",function (){
                    $('#bonus').focus();
                });
                return false;
            }
            if(isNull(note)){
                alertx("请输入备注！",function (){
                    $('#note').focus();
                });
                return false;
            }
            var msg = 1 === status ? "发放" : "<span style='color:red'>拒绝发放</span>"
            confirmx("确认" + msg + "【" + bonus +  "】元伯乐奖励到【" + originalParentName + "】！？", function () {
                $.get(ctx + "/ec/upgrade/issueBonus", {
                    id: id,
                    status: status,
                    amount: bonus,
                    note: note
                }, function () {
                    alertx(msg + "该伯乐奖励成功！", function () {
                        showDialog('bonusDialog');
                        reloadGrid('/ec/upgrade/bonusListData');
                    });
                });
            });
        }
        function showBatchIssueBonus(){
            var checkedBox = $("#contentTable").datagrid('getChecked');
            if(checkedBox.length == 0){
                alertx("请勾选要发放的目标！");
                return false;
            }
            var idArray = [];
            $.each(checkedBox, function(index, item){
                if(item.status === 0){
                    idArray.push("idStrArray=" + item.id);
                }
            });
            if(idArray.length == 0){
                alertx("请勾选<span>未发放</span>的目标！");
                return false;
            }
            showDialog("batchBonusDialog");
        }
        function batchIssueBonus(status){
            var idArray = [];
            $.each($("#contentTable").datagrid('getChecked'), function(index, item){
                if(item.status === 0){
                    idArray.push("idStrArray=" + item.id);
                }
            });
            var idStrArray = idArray.join("&");
            var msg = 1 === status ? "发放" : "拒绝发放";
            var bonus = $('#batchBonus').val();
            var note = $('#batchNote').val();
            if(isNull(bonus) || !isMoney(bonus) || parseFloat(bonus) < 0){
                alertx("奖励金额格式不正确！",function (){
                    $('#batchBonus').focus();
                });
                return false;
            }
            if(1 != status && parseFloat(bonus) != 0){
                alertx("奖励金额为零时才能拒绝发放！",function (){
                    $('#batchBonus').focus();
                });
                return false;
            }
            if(isNull(note)){
                alertx("请输入备注！",function (){
                    $('#batchNote').focus();
                });
                return false;
            }
            confirmx("确认<span style='color:red'>批量" + msg + "</span>伯乐奖励！？", function () {
                $.get(ctx + "/ec/upgrade/batchIssueBonus?" + idStrArray, {
                    status: status,
                    amount: bonus,
                    note: note
                }, function () {
                    alertx("批量" + msg + "伯乐奖励成功！", function () {
                        showDialog('batchBonusDialog');
                        reloadGrid('/ec/upgrade/bonusListData');
                    });
                });
            });
        }
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="upgradeBonus" action="${ctx}/ec/upgrade/list" method="post" class="form-search" onsubmit="loading()">
	<div class="panel panel-info">
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				查询条件
			</div>
		</div>
		<div class="panel-body">
            <label>关键字：</label>
            <form:input path="sqlMap['keyWord']" htmlEscape="false" maxlength="50" class="input-large" placeholder="用户名、手机号"/>
            <label>奖励状态：</label>
            <form:select path="status" cssStyle="width: 80px;">
                <form:option value="">全部</form:option>
                <form:option value="0">未发放</form:option>
                <form:option value="1">已发放</form:option>
                <form:option value="2">不发放</form:option>
            </form:select>
            <label>操作时间：</label>
            <input id="st" name="sqlMap['st']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width:135px;"
                   value="${customer.sqlMap['st']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
            <label style="margin-left: 0px;">至</label>
            <input id="et" name="sqlMap['et']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width:135px;"
                   value="${customer.sqlMap['et']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
            <input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="reloadGrid('/ec/upgrade/bonusListData')"/>
            <input onclick="showBatchIssueBonus()" class="btn btn-primary" type="button" value="批量发放"/>
		</div>
	</div>
	</form:form>
    <table id="contentTable"></table>
	<div id="bonusDialog" class="vcat-dialog" style="width: 50%;">
		<div class="panel panel-info">
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
                    发放奖励
					<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog($(this).parents('.vcat-dialog').attr('id'))"></a>
				</div>
			</div>
			<div class="panel-body">
				<table class="table table-bordered table-hover">
					<tr>
						<td width="20%"><span class="pull-right">用户名：</span></td>
						<td width="80%"><span id="originalParentName"></span></td>
					</tr>
                    <tr>
                        <td><span class="pull-right">已升级成员用户名：</span></td>
                        <td><span id="shopName"></span></td>
                    </tr>
					<tr>
						<td><span class="pull-right">奖励金额：</span></td>
						<td><input id="bonus" type="text"/></td>
					</tr>
                    <tr>
                        <td><span class="pull-right">备注：</span></td>
                        <td><textarea id="note" rows="3" style="width:98%;"></textarea></td>
                    </tr>
				</table>
                <div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;padding: 5px 15px 5px;">
                    <input type="hidden" id="upgradeRequestId"/>
                    <input onclick="issueBonus($('#upgradeRequestId').val(),1,$('#originalParentName').html())" class="btn btn-success" type="button" value="确认发放"/>
                    <input onclick="issueBonus($('#upgradeRequestId').val(),2,$('#originalParentName').html())" class="btn btn-danger" type="button" value="拒绝发放"/>
                    <input onclick="showDialog('bonusDialog')" class="btn" type="button" value="关闭"/>
                </div>
			</div>
		</div>
	</div>
    <div id="batchBonusDialog" class="vcat-dialog" style="width: 50%;">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    批量发放奖励
                    <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog($(this).parents('.vcat-dialog').attr('id'))"></a>
                </div>
            </div>
            <div class="panel-body">
                <table class="table table-bordered table-hover">
                    <tr>
                        <td><span class="pull-right">奖励金额：</span></td>
                        <td><input id="batchBonus" type="text"/></td>
                    </tr>
                    <tr>
                        <td><span class="pull-right">备注：</span></td>
                        <td><textarea id="batchNote" rows="3" style="width:98%;"></textarea></td>
                    </tr>
                </table>
                <div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;padding: 5px 15px 5px;">
                    <input onclick="batchIssueBonus(1)" class="btn btn-success" type="button" value="确认发放"/>
                    <input onclick="batchIssueBonus(2)" class="btn btn-danger" type="button" value="拒绝发放"/>
                    <input onclick="showDialog('batchBonusDialog')" class="btn" type="button" value="关闭"/>
                </div>
            </div>
        </div>
    </div>
</body>
</html>