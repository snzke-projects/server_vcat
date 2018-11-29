<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>建议反馈管理</title>
	<meta name="decorator" content="default"/>
    <script type="application/javascript">
        $(function (){
            $("#btnSubmit").click(function(){
                reloadGrid('/ec/suggest/listData');
            });
            $('#contentTable').datagrid(getDatagridOption({
                url:ctx + "/ec/suggest/listData",
                columns:[[
                    {field:'contact',title:'联系方式',width:140,formatter:function(value,row){
                        return '<a href="javascript:void(0)" onclick="show(\'' + row.id + '\',\'' + row.processed + '\')">' + row.contact + '</a>';
                    }},
                    {field:'content',title:'反馈内容'},
                    {field:'createDate',title:'反馈时间',width:130},
                    {field:'processLabel',title:'处理情况',width:100,styler:function(value,row){return 'color:' + row.processColor}},
                    {field:'processUser',title:'处理人',width:70},
                    {field:'processDate',title:'处理时间',width:130},
                    {field:'action',title:'操作',width:70,formatter:function(value,row){
                        return '<a href="javascript:void(0)" onclick="show(\'' + row.id + '\',\'' + row.processed + '\')">查看</a>';
                    }}
                ]]
            }));
        });
        function show(id,processed){
            $.ajax({
                type : 'POST',
                url : ctx + "/ec/suggest/get",
                data : {id : id},
                success : function(suggest){
                    try{
                        $('#suggestId').val(suggest.id);
                        $('#contact').html(getDefault(suggest.contact,'未填写'));
                        $('#content').html(getDefault(suggest.content,'未填写'));
                        $('#createDate').html(getDefault(suggest.createDate,'未填写'));
                        $('#processed').html(getDefault(suggest.processLabel,'未填写'));
                        $('#processDate').html(getDefault(suggest.processDate,'未填写'));
                        $('#processUser').html(getDefault(suggest.processUser,'未填写'));
                        if(suggest.processed){
                            $('#remarks').html(suggest.remarks);
                            $('#remarks').show();
                            $('.process').hide();
                        }else{
                            $('#remarks').hide();
                            $('#remarksText').show();
                            $('.process').show();
                        }
                        showDialog('suggestDialog');
                    }catch(e){
                        console.log("获取建议反馈详情失败："+e.message);
                        return doError(e);
                    }
                },
                error: function(XMLHttpRequest) {
                    return doError(XMLHttpRequest);
                }
            });
        }
        function process(id){
            if($('#remarksText').val() == ''){
                alertx("请填写处理详情！",function (){
                    $('#remarksText').focus();
                });
                return;
            }
            confirmx("确认已处理该反馈？",function(){
                $.get(ctx + "/ec/suggest/process",{
                    id : id
                    ,remarks : $('#remarksText').val()
                },function(){
                    alertx("处理反馈成功！",function(){
                        showDialog("suggestDialog");
                        reloadGrid('/ec/suggest/listData');
                    });
                });
            });
        }
    </script>
</head>
<body>
	<sys:message content="${message}"/>
    <form:form id="searchForm" modelAttribute="suggest" method="post" class="form-search">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    查询条件
                </div>
            </div>
            <div class="panel-body">
                <label>关键字：</label>
                <form:input id="keyWord" path="sqlMap['keyWord']" maxlength="50" class="input-xlarge" placeholder="联系方式、反馈内容"/>&nbsp;
                <label>排序：</label>
                <form:select path="sqlMap['orderType']" cssClass="input-large">
                    <form:option value="">处理情况正序，反馈时间正序</form:option>
                    <form:option value="1">处理情况正序，反馈时间倒序</form:option>
                    <form:option value="2">处理情况倒序，反馈时间正序</form:option>
                    <form:option value="3">处理情况倒序，反馈时间倒序</form:option>
                </form:select>
                &nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
            </div>
        </div>
    </form:form>
    <sys:message content="${message}"/>
    <table id="contentTable"></table>
    <div id="suggestDialog" class="vcat-dialog" style="width: 80%;">
        <div class="panel panel-info"><!-- 退货单 -->
            <div class="panel-heading">
                <div class="panel-title">
                    反馈详情
                    <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('suggestDialog')"></a>
                </div>
            </div>
            <div class="panel-body">
                <table class="table table-responsive table-hover">
                    <tbody>
                    <tr>
                        <td width="15%">联系方式：</td>
                        <td width="85%"><span id="contact"></span></td>
                    </tr>
                    <tr>
                        <td>反馈内容：</td>
                        <td><span id="content"></span></td>
                    </tr>
                    <tr>
                        <td>反馈时间：</td>
                        <td><span id="createDate"></span></td>
                    </tr>
                    <tr>
                        <td>处理情况：</td>
                        <td><span id="processed"></span></td>
                    </tr>
                    <tr>
                        <td>处理人：</td>
                        <td><span id="processUser"></span></td>
                    </tr>
                    <tr>
                        <td>处理时间：</td>
                        <td><span id="processDate"></span></td>
                    </tr>
                    <tr>
                        <td>处理详情：</td>
                        <td>
                            <pre id="remarks" class="hide"></pre>
                            <shiro:hasPermission name="ec:suggest:process">
                            <textarea id="remarksText" class="hide" rows="3" style="width: 95%"></textarea>
                            </shiro:hasPermission>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;padding: 5px 15px 5px;">
                    <input type="hidden" id="suggestId">
                    <shiro:hasPermission name="ec:suggest:process">
                        <input onclick="process($('#suggestId').val())" class="btn btn-success process" type="button" value="处 理"/>
                    </shiro:hasPermission>
                    <input onclick="showDialog('suggestDialog')" class="btn btn-default" type="button" value="返 回"/>
                </div>
            </div>
        </div>
    </div>
</body>
</html>