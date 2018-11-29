<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Quartz任务管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
            init();
        });
        function init(){
            if($('#jobClassSelect option:not(:first)').length == 0){
                $('#s2id_jobClassSelect').hide();
                $('#jobClassInput').show();
            }else{
                $('#s2id_jobClassSelect').show();
                $('#jobClassInput').hide();
            }
            if($(':radio').length > 0){
                $(':radio:first').attr("checked",true);
            }
        }
        function getJobDetail(className){
            $(".jobDetail").remove();

            $.ajax({
                type : "POST",
                url : ctx + "/sys/quartz/getJobDetail?className="+className,
                success : function (job){
                    if(isNull(job)){
                        return;
                    }

                    var paramHTML = "";

                    if(!isNull(job.description)) {
                        paramHTML += '<tr class="jobDetail"><td style="text-align: right;">备注：</td><td><pre>' + job.description + '</pre></td></tr>';
                    }

                    if(!isNull(job.jobDataMap)){
                        for(var entry in job.jobDataMap){
                            paramHTML += '<tr class="jobDetail">';
                            paramHTML += '<td style="text-align: right;">' + entry + '：</td>';
                            paramHTML += "<td><input type='text' name='${paramPrefix}" + entry + "' class='input-large'></td>";
                            paramHTML += '</tr>';
                        }
                    }

                    $("#hideTR").before(paramHTML);
                },
                error: function(XMLHttpRequest) {
                    return doError(XMLHttpRequest);
                }
            });
        }
        function addCustomParam(){
            var paramHTML = "";

            var uuid = 'customerParam_' + new Date().getMilliseconds();

            paramHTML += '<tr class="jobDetail ' + uuid + '">';

            paramHTML += '<td style="text-align: right">参数<input type="text" class="input-small" onkeyup="changeName(this.value,\'' + uuid + '\')"/>：</td>';
            paramHTML += '<td><input id="' + uuid + '" type="text" class="input-large"/><i class="icon-remove" onclick="$(\'.' + uuid + '\').remove();" style="margin-left: 5px;"></i></td>';

            paramHTML += '</tr>';

            $("#hideTR").before(paramHTML);
        }
        function changeName(value,valueDomId){
            $("#"+valueDomId).attr("name","${paramPrefix}" + value);
        }
        function customJobClass(isWrite){
            $(".jobDetail").remove();
            if(isWrite){
                $('#s2id_jobClassSelect').hide();
                $('#jobClassInput').show();
                $('#manualWriteJobClassBtn').hide();
                $('#manualChooseJobClassBtn').show();
            }else{
                $('#s2id_jobClassSelect').show();
                $('#jobClassInput').hide();
                $('#manualWriteJobClassBtn').show();
                $('#manualChooseJobClassBtn').hide();
            }
        }
        function invoke(){
            var invokeData = {};

            if($(".sourceJobTargetTR").is(":hidden")){
                if(isNull($("#host").val())){
                    alertx("任务目标IP不能为空",function (){
                        $("#host").focus();
                    });
                    return false;
                }
                if(!isInteger($("#port").val())){
                    alertx("任务目标端口格式不正确",function (){
                        $("#port").focus();
                    });
                    return false;
                }
                invokeData.host = $("#host").val();
                invokeData.port = $("#port").val();
                invokeData.username = $("#username").val();
                invokeData.password = $("#password").val();
            }

            if(isNull($("#jobName").val())){
                alertx("任务名称不能为空",function (){
                    $("#jobName").focus();
                });
                return false;
            }
            invokeData.jobName = $("#jobName").val();
            invokeData.nextFireTime = $("#nextFireTime").val();
            if($("#jobClassInput").is(":hidden")){
                if(isNull($("#jobClassSelect").val())){
                    alertx("请选择任务Class",function (){
                        $("#jobClassSelect").select2("open");
                    });
                    return false;
                }
                invokeData.jobClass = $("#jobClassSelect").val();
            }else{
                if(isNull($("#jobClassInput").val())){
                    alertx("请填写任务Class",function (){
                        $("#jobClassInput").focus();
                    });
                    return false;
                }
                invokeData.jobClass = $("#jobClassInput").val();
            }
            $('.invokeTable :input[name*=${paramPrefix}]').each(function(){
                var input = $(this);
                var name = input.attr("name");
                if(isNull(name)){
                    return true;
                }
                var value = input.val();
                if(isNull(value)){
                    alertx("参数" + name + " 不能为空！",function(){
                        input.focus();
                    });
                    return;
                }
                invokeData[name] = value;
            });
            invokeData.schedulerName = $(":radio[name=schedulerName]:checked").val();
            confirmx("确认设置该任务？",function(){
                loading("请稍候...");
                $.ajax({
                    type : 'POST',
                    url : ctx + "/sys/quartz/setJob",
                    data : invokeData,
                    success : function(){
                        alertx("任务" + $('#jobName').val() + ("" == $("#nextFireTime").val() ? "执行成功！" : "设置成功，将在[" + $("#nextFireTime").val() + "]执行！"),function (){
                            location.href = ctx + "/sys/quartz/list?needReconnect=true";
                        },500);
                    },
                    error: function(XMLHttpRequest) {
                        return doError(XMLHttpRequest);
                    }
                });
            });
        }

        function reconnect(){

            var invokeData = {};

            if($(".sourceJobTargetTR").is(":hidden")){
                if(isNull($("#host").val())){
                    alertx("任务目标IP不能为空",function (){
                        $("#host").focus();
                    });
                    return false;
                }
                if(!isInteger($("#port").val())){
                    alertx("任务目标端口格式不正确",function (){
                        $("#port").focus();
                    });
                    return false;
                }
                invokeData.host = $("#host").val();
                invokeData.port = $("#port").val();
                invokeData.username = $("#username").val();
                invokeData.password = $("#password").val();
            }

            loading("请稍候...");
            $.ajax({
                type : 'POST',
                url : ctx + "/sys/quartz/reconnect",
                data : invokeData,
                success : function(result){
                    var schedulerNameArray = result.schedulerNameArray;
                    var classSet = result.classSet;
                    if(!isNull(schedulerNameArray) && schedulerNameArray.length > 0){
                        showTip("连接成功！");
                        buildSchedulerNameRadios(schedulerNameArray);
                        buildSchedulerClass(classSet);
                        $('#hostSpan').html($('#host').val());
                        $('#portSpan').html($('#port').val());
                        init();
                        changJobTarget(false);
                    }else{
                        showTip("连接失败，该连接可执行任务目标为空！");
                    }
                },
                error: function(XMLHttpRequest) {
                    return doError(XMLHttpRequest);
                }
            });
        }

        function buildSchedulerNameRadios(schedulerNameArray){
            $('#targetTD').html("");
            var html = "";
            for(var i = 0;i < schedulerNameArray.length;i++){
                var schedulerName = schedulerNameArray[i];
                html += '<label for="'+schedulerName+'_'+i+'">'+schedulerName+'</label>';
                html += '<input id="'+schedulerName+'_'+i+'" type="radio" name="schedulerName" value="'+schedulerName+'">';
            }
            $('#targetTD').append(html);
        }

        function buildSchedulerClass(classArray){
            $('#jobClassSelect option:not(:first)').remove();
            var html = "";
            for(var i = 0;i < classArray.length;i++){
                var className = classArray[i];
                html += '<option value="'+className+'">'+className+'</option>';
            }
            $('#jobClassSelect').append(html);
            $('#jobClassSelect').select2();
        }

        function changJobTarget(isCustom){
            if(isCustom){
                $('#targetTR').hide();
                $('.customJobTargetTR').show();
                $('.sourceJobTargetTR').hide();
                $('#jobClassSelect option:not(:first)').remove();
            }else{
                $('#targetTR').show();
                $('.customJobTargetTR').hide();
                $('.sourceJobTargetTR').show();
                $('#host').val("");
                $('#port').val("");
                $('#username').val("");
                $('#password').val("");
            }
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/quartz/list">任务列表</a></li>
		<shiro:hasPermission name="sys:quartz:add"><li class="active"><a href="${ctx}/sys/quartz/add">设置新任务</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}" type="warning"/>
    <table class="table table-bordered table-hover invokeTable">
        <tbody>
        <tr class="sourceJobTargetTR <c:if test="${currentConnection == null || currentConnection.host == null}"> hide</c:if>">
            <td style="text-align: right;" width="20%">任务连接：</td>
            <td width="80%">
                <span id="hostSpan">${currentConnection.host}</span><span>:</span><span id="portSpan">${currentConnection.port}</span>
                <button id="changToCustomJobTargetBtn" class="btn btn-default" onclick="changJobTarget(true)" style="margin-left: 5px;">更换连接</button>
            </td>
        </tr>
        <tr class="customJobTargetTR<c:if test="${currentConnection != null && currentConnection.host != null}"> hide</c:if>">
            <td style='text-align: right;' width="20%">任务连接：</td>
            <td width="80%">
                <input type="text" id="host" maxlength="16" class="input-medium"/>：
                <input type="text" id="port" maxlength="5" class="input-mini"/>
                <button id="reconnectTargetBtn" class="btn btn-default" onclick="reconnect()" style="margin-left: 5px;">测试连接</button>
                <c:if test="${currentConnection != null && currentConnection.host != null}">
                    <button id="changToCurrentJobTargetBtn" class="btn btn-default" onclick="changJobTarget(false)" style="margin-left: 5px;">返回原连接</button>
                </c:if>
            </td>
        </tr>
        <tr id="targetTR">
            <td style='text-align: right;'>任务目标：</td>
            <td id="targetTD">
                <c:forEach items="${schedulerNameSet}" var="schedulerName" varStatus="schedulerNameStatus">
                    <label for="${schedulerName}_${schedulerNameStatus.index}">${schedulerName}</label>
                    <input id="${schedulerName}_${schedulerNameStatus.index}" type="radio" name="schedulerName" value="${schedulerName}">
                </c:forEach>
            </td>
        </tr>
        <tr class="customJobTargetTR<c:if test="${currentConnection != null && currentConnection.host != null}"> hide</c:if>">
            <td style='text-align: right;'>目标用户名：</td>
            <td><input type="text" id="username" maxlength="20" class="input-medium"/></td>
        </tr>
        <tr class="customJobTargetTR<c:if test="${currentConnection != null && currentConnection.host != null}"> hide</c:if>">
            <td style='text-align: right;'>目标用户密码：</td>
            <td><input type="password" id="password" maxlength="50" class="input-medium"/></td>
        </tr>
        <tr>
            <td style='text-align: right;'>任务名称：</td>
            <td><input type="text" id="jobName" maxlength="60" class="input-xxlarge"/></td>
        </tr>
        <tr>
            <td style='text-align: right;'>任务执行时间：</td>
            <td>
                <input id="nextFireTime" type="text" readonly="readonly" maxlength="20" class="input-date Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'});"/>
                <span class="help-inline">为空则立即执行</span>
            </td>
        </tr>
        <tr>
            <td style='text-align: right;'>任务Class：</td>
            <td>
                <select id="jobClassSelect" name="jobClass" onchange="getJobDetail(this.value)" class="input-xxlarge">
                    <option value="">请选择</option>
                    <c:forEach items="${classSet}" var="className">
                        <option value="${className}">${className}</option>
                    </c:forEach>
                </select>
                <input id="jobClassInput" name="jobClass" class="input-xxlarge" type="text"<c:if test="${fn:length(classSet) > 0}"> style="display: none;"</c:if>>
                <button id="manualWriteJobClassBtn" class="btn btn-default<c:if test="${fn:length(classSet) == 0}"> hide</c:if>" onclick="customJobClass(true)" style="margin-left: 5px;">手动填写任务Class</button>
                <button id="manualChooseJobClassBtn" class="btn btn-default hide" onclick="customJobClass(false)" style="margin-left: 5px;">手动选择任务Class</button>
                <button class="btn btn-default" onclick="addCustomParam()" style="margin-left: 5px;">添加自定义参数</button>
            </td>
        </tr>
        <tr id="hideTR" class="hide"></tr>
        </tbody>
    </table>
    <div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;">
        <input type="hidden" id="specId">
        <input onclick="invoke()" class="btn btn-success" type="button" value="执行"/>
        <input onclick="history.go(-1)" class="btn btn-default" type="button" value="返回"/>
    </div>
</body>
</html>