<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>体验官参与情况</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
            $("#btnExport").click(function(){
                top.$.jBox.confirm("确认导出？","系统提示",function(v){
                    if(v=="ok"){
                        $("#searchForm").attr("action",ctx + "/ec/activity/exportParticipationList");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action",ctx + "/ec/activity/participationList");
                    }
                },{buttonsFocus:1});
                top.$('.jbox-body .jbox-icon').css('top','55px');
            });
        });
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}
        function viewReport(id,name,title){
            top.$.jBox.open("iframe:${ctx}/ec/activity/viewAnswerSheet?activityId=${activity.id}&customerId="+id, title + "【" + name + "】填写情况",$(top.document).width()-220,$(top.document).height()-180,{
                buttons: {"确定": true},
                loaded: function () {
                    $(".jbox-content", top.document).css("overflow-y", "hidden");
                }
            });
        }
	</script>
</head>
<body>
<sys:message content="${message}"/>
<form:form id="searchForm" modelAttribute="activity" action="${ctx}/ec/activity/participationList" method="post" class="form-search">
    <div class="panel panel-info">
        <div class="panel-heading">
            <div class="panel-title" style="font-size: 14px;">
                查询条件
            </div>
        </div>
        <div class="panel-body">
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <input name="id" type="hidden" value="${activity.id}">
            <form:select path="sqlMap['hasReport']" cssClass="input-small">
                <option value="">全部</option>
                <option value="1">已提交</option>
                <option value="2">未提交</option>
            </form:select>
            <label>用户名：</label>
            <form:input path="sqlMap['userName']" cssClass="input-medium"></form:input>&nbsp;
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;
            <input id="btnExport" class="btn btn-primary" type="button" value="导出"/>&nbsp;
        </div>
    </div>
</form:form>
<table id="contentTable" class="table table-bordered table-hover">
	<thead>
	<tr>
		<th width="100px">用户名</th>
        <th width="100px">真实姓名</th>
		<th width="100px">手机号</th>
        <th width="60px">QQ</th>
		<th width="100px">邮箱</th>
        <th width="60px">收货人</th>
        <th width="100px">收货电话</th>
        <th>详细地址</th>
        <th width="100px">是否提交报告</th>
		<th width="150px">参与时间</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${page.list}" var="parter">
		<tr>
			<td>${parter.name}</td>
            <td>${parter.realName}</td>
			<td>${parter.phone}</td>
            <td>${parter.qq}</td>
			<td>${parter.email}</td>
			<td>${parter.deliveryName}</td>
            <td>${parter.deliveryPhone}</td>
            <td>${parter.detailAddress}</td>
            <td>
                <c:if test="${parter.hasReport}">
                    已提交
                    <a href="javascript:void(0)" onclick="viewReport('${parter.customerId}','${parter.realName}','${parter.questionnaireTitle}')">查看报告</a>
                </c:if>
                <c:if test="${!parter.hasReport}">
                    未提交
                </c:if>
            </td>
            <td>${parter.date}</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>