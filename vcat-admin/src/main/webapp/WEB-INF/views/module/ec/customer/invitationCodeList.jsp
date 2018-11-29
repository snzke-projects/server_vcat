<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>卖家等级列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function(){
            $('#contentTable').datagrid(getDatagridOption({
                url:ctx + "/ec/customer/invitationCodeListData",
                columns:[[
                    {field:'code',title:'邀请码',width:300},
                    {field:'statusLabel',title:'邀请码状态',width:140,formatter:function (value,row){
                        return '<span style="color:' + row.statusColor + '">' + value + '</span>';
                    }},
                    {field:'userName',title:'店铺名称',width:140,formatter:function (value,row){
                        return row.usedCustomer && row.usedCustomer.userName ? row.usedCustomer.userName : "无";
                    }},
                    {field:'phoneNumber',title:'店铺手机号',width:140,formatter:function (value,row){
                        return row.usedCustomer && row.usedCustomer.phoneNumber ? row.usedCustomer.phoneNumber : "无";
                    }},
                    {field:'action',title:'操作',width:100,formatter:function (value,row){
                        var actionTitle = row.status != 2 ? "停用" : "启用";
                        var a = '<a href="javascript:void(0)" onclick="stopCode(\'' + row.id + '\',\'' + row.status + '\',\'' + row.code + '\')">' + actionTitle + '邀请码</a>';
                        return a;
                    }}
                ]]
            }));
        })

        function stopCode(id,status,code){
            var isStop = status != '2';
            var msg = isStop ? "停用" : "启用";
            confirmx("确认" + msg + "邀请码【" + code + "】？",function (){
                $.get(ctx + "/ec/customer/stopCode?id=" + id + "&status=" + status,function (){
                    alertx(msg + "邀请码【" + code + "】成功！",function (){
                        location.href = location.href;
                    });
                });
            });
        }

        function generateCode(count){
            showLayer();
            loading();
            $.get(ctx + "/ec/customer/generateCode?count=" + count,function (){
                alertx("生成邀请码完成！",function (){closeTip();location.href = location.href;});
            });
        }
	</script>
</head>
<body>
    <form:form id="searchForm" modelAttribute="invitationCode" method="post" class="form-search">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    邀请码列表
                </div>
            </div>
            <div class="panel-body">
                <label>关键字：</label>
                <form:input path="sqlMap['keyWord']" cssClass="input-medium" placeholder="注册手机号，邀请码"></form:input>
                <label>状态：</label>
                <form:select path="status" class="input-medium">
                    <form:option value="" label="全部"/>
                    <form:option value="0" label="可使用"/>
                    <form:option value="1" label="已使用"/>
                    <form:option value="2" label="停用"/>
                </form:select>
                &nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="reloadGrid('/ec/customer/invitationCodeListData')"/>
            </div>
        </div>
    </form:form>
    <table id="contentTable"></table>
    <button class="btn" onclick="showDialog('invitationCodeDialog')">生成邀请码</button>
	<div id="invitationCodeDialog" class="vcat-dialog" style="width: 50%;">
		<div class="panel panel-info">
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					生成邀请码
					<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog($(this).parents('.vcat-dialog').attr('id'))"></a>
				</div>
			</div>
			<div class="panel-body">
				<table class="table table-responsive table-hover">
					<tbody>
					<tr>
						<td width="20%"><span class="pull-right">邀请码数量：</span></td>
						<td width="80%">
                            <select id="codeCount">
                                <option value="3000">3000</option>
                                <option value="5000">5000</option>
                                <option value="10000">10000</option>
                            </select>
                        </td>
					</tr>
					</tbody>
				</table>
				<div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;">
					<input onclick="generateCode($('#codeCount').val())" class="btn btn-success" type="button" value="生 成"/>
				</div>
			</div>
		</div>
	</div>
</body>
</html>