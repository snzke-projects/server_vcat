<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Quartz任务管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(function() {
            $("#searchForm :text,:password").bind("keyup",function(){
                $("#isModify").val("true");
            });
		});
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

        function toInvoke(schedulerName,name,group,description,keyClass){
            $('#schedulerName').html(schedulerName);
            $('#jobName').html(name);
            $('#groupName').html(group);
            $('#description').html(description);
            if($(":input."+keyClass).length == 0){
                invoke();
                return;
            }

            var keyHtml = "";
            $('.jobParam').remove();
            $(":input."+keyClass).each(function (){
                var input = $(this);
                var name = input.attr("name");
                keyHtml += "<tr class='jobParam'>";
                keyHtml += "<td style='text-align: right;'>参数" + name + "：</td>";
                keyHtml += "<td colspan='3'><input type='text' name='${paramPrefix}" + name + "' value='" + input.val() + "' class='input-large' style='margin-bottom:0px;'></td>";
                keyHtml += "</tr>";
            });
            $("#hideTR").before(keyHtml);

            showDialog("jobDialog");
        }
        function invoke(){
            var invokeData = {
                schedulerName : $('#schedulerName').html(),
                name : $('#jobName').html(),
                groupName : $('#groupName').html()
            };
            $('.invokeTable :input').each(function(){
                var input = $(this);
                var name = input.attr("name");
                var value = input.val();
                if(value == ""){
                    alertx("参数" + name + " 不能为空！",function(){
                        input.focus();
                    });
                    return;
                }
                invokeData[name] = value;
            });
            confirmx("确认立即执行该任务？",function(){
                loading("请稍候...");
                $.ajax({
                    type : 'POST',
                    url : ctx + "/sys/quartz/invoke",
                    data : invokeData,
                    success : function(){
                        alertx("任务" + $('#jobName').html() + "执行成功！",function (){
                            location.href = location.href;
                        });
                    },
                    error: function(XMLHttpRequest) {
                        return doError(XMLHttpRequest);
                    }
                });
            });
        }
        function job(requestName,schedulerName,name,group){
            var title = "操作";
            if("pause" == requestName){
                title = "暂停";
            }else if("resume" == requestName){
                title = "恢复";
            }else if("delete" == requestName){
                title = "删除";
            }else if("reconnect" == requestName){
                title = "刷新";
            }
            loading("请稍候...");
            $.ajax({
                type : 'POST',
                url : ctx + "/sys/quartz/"+requestName,
                data : {
                    schedulerName : schedulerName,
                    name : name,
                    groupName : group
                },
                success : function(){
                    name = isNull(name) ? "" : "[" + name + "]";
                    alertx(title+"任务" + name + "执行成功！",function (){
                        location.href = location.href;
                    },600);
                },
                error: function(XMLHttpRequest) {
                    return doError(XMLHttpRequest);
                }
            });
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/quartz/list">任务列表</a></li>
		<shiro:hasPermission name="sys:quartz:add"><li><a href="${ctx}/sys/quartz/add">设置新任务</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
    <form:form id="searchForm" modelAttribute="connection" action="${ctx}/sys/quartz/list" method="post" class="form-search">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    查询条件
                </div>
            </div>
            <div class="panel-body">
                <label>IP：</label>
                <form:input path="host" maxlength="16" class="input-large" placeholder="要监控任务的IP"/>&nbsp;
                <label>端口：</label>
                <form:input path="port" maxlength="5" class="input-small" placeholder="JMX端口"/>&nbsp;
                <label>用户名：</label>
                <form:input path="username" maxlength="20" class="input-small" placeholder="用户名"/>&nbsp;
                <label>密码：</label>
                <form:password path="password" maxlength="20" class="input-small" placeholder="密码"/>&nbsp;
                <form:hidden path="isModify"></form:hidden>
                <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
                <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
                <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;
            </div>
        </div>
    </form:form>
    <table class="table table-bordered table-hover">
        <thead>
        <tr>
            <th width="20%" onclick=" loading('正在刷新，请稍候...');location.href = location.href + '?needReconnect=true'">名称</th>
            <th width="8%">所属组</th>
            <th width="8%">下次触发时间</th>
            <th width="8%">触发器</th>
            <th width="8%">所属Scheduler</th>
            <th width="30%">参数或备注</th>
            <th width="8%">状态</th>
            <shiro:hasPermission name="sys:quartz:list">
            <th width="10%">操作</th>
            </shiro:hasPermission></tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="job" varStatus="jobStatus">
            <tr>
                <td>${job.name}</td>
                <td>${job.group}</td>
                <td><fmt:formatDate value="${job.nextFireTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                <td>${job.triggerCount}</td>
                <td>${job.schedulerName}</td>
                <td>
                    <c:if test="${fn:length(job.jobDataMap) > 0}">
                        <c:forEach items="${job.jobDataMap}" var="entry">
                            <c:out value="${entry.key}" />=<c:out value="${entry.value}" /><br>
                            <input type="hidden" class="${job.id}_${jobStatus.index}" name="${entry.key}" value="${entry.value}">
                        </c:forEach>
                    </c:if>
                    <c:if test="${fn:length(job.jobDataMap) == 0}">${job.description}</c:if>
                </td>
                <td>${job.state}</td>
                <shiro:hasPermission name="sys:quartz:list">
                <td>
                    <a href="javascript:void(0)" onclick="toInvoke('${job.schedulerName}','${job.name}','${job.group}','${job.description}','${job.id}_${jobStatus.index}')">立即执行</a>
                    <c:if test="${job.state eq 'NORMAL'}">
                        <a href="javascript:void(0)" onclick="job('pause','${job.schedulerName}','${job.name}','${job.group}')">暂停</a>
                    </c:if>
                    <c:if test="${job.state eq 'PAUSED'}">
                        <a href="javascript:void(0)" onclick="job('resume','${job.schedulerName}','${job.name}','${job.group}')">恢复</a>
                        <a href="javascript:void(0)" onclick="job('delete','${job.schedulerName}','${job.name}','${job.group}')">删除</a>
                    </c:if>
                    <c:if test="${null == job.state || job.state eq ''}">
                        <a href="javascript:void(0)" onclick="job('delete','${job.schedulerName}','${job.name}','${job.group}')">删除</a>
                    </c:if>
                </td>
                </shiro:hasPermission>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <div class="pagination">${page}</div>
    <div id="jobDialog" class="vcat-dialog" style="width: 50%;">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    Quartz任务
                    <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('jobDialog')"></a>
                </div>
            </div>
            <div class="panel-body">
                <table class="table table-responsive table-hover invokeTable">
                    <tbody>
                    <tr>
                        <td width="25%" style='text-align: right;'>Scheduler：</td>
                        <td width="75%"><span id="schedulerName"></span></td>
                    </tr>
                    <tr>
                        <td style='text-align: right;'>任务名称：</td>
                        <td><span id="jobName"></span></td>
                    </tr>
                    <tr>
                        <td style='text-align: right;'>所属分组：</td>
                        <td><span id="groupName"></span></td>
                    </tr>
                    <tr>
                        <td style='text-align: right;'>备注：</td>
                        <td><pre id="description"></pre></td>
                    </tr>
                    <tr id="hideTR" class="hide"></tr>
                    </tbody>
                </table>
                <div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;">
                    <input type="hidden" id="specId">
                    <input onclick="invoke()" class="btn btn-success" type="button" value="执行"/>
                    <input onclick="showDialog('jobDialog')" class="btn btn-default" type="button" value="返回"/>
                </div>
            </div>
        </div>
    </div>
</body>
</html>