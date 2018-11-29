<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>报告情况</title>
	<meta name="decorator" content="default"/>
    <script type="text/javascript">
        function viewAllAnswer(id,title){
            top.$.jBox.open("iframe:${ctx}/ec/activity/viewShortAnswerList?id="+id+"&title="+title+"&sqlMap['activityId']=${activity.id}", title + " 所有回答",$(top.document).width()-220,$(top.document).height()-180,{
                buttons: {"确定": true},
                loaded: function () {
                    $(".jbox-content", top.document).css("overflow-y", "hidden");
                }
            });
        }
        $(function(){
            $("#inputForm").validate({
                submitHandler: function(){
                    if(isNull($('#conclusion').val())){
                        alertx("请填写报告结论！",function(){$('#conclusion').focus()});
                        return;
                    }
                    if($(":checked").length < 5){
                        alertx("请选择5道需要报告的题目！");
                        return;
                    }
                    loading('正在提交，请稍等...');

                    $.getJSON(ctx + "/ec/activity/feedBack?"+$('#inputForm').serialize(),function(){
                        closeTip();
                        alertx("发布报告成功！",function(){
                            window.parent.window.jBox.close();
                        });
                    });
                }
            });
        });
    </script>
</head>
<body>
    <form:form id="inputForm" modelAttribute="feedback" action="${ctx}/ec/activity/feedBack" method="post" class="form-horizontal">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    发布 ${activity.title} 体验官 ${questionnaire.title}
                </div>
            </div>
            <div class="panel-body">
                <input type="hidden" name="activity.id" value="${activity.id}">
                <input type="hidden" name="id" value="${activity.id}">
                <input type="hidden" name="reportCount" value="${reportedCount}">
                <div class="control-group">
                    <label class="control-label">标题：</label>
                    <div class="controls">
                        <form:input path="title" cssClass="required input-large"></form:input>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">产品名称：</label>
                    <div class="controls">
                        <form:input path="productName" cssClass="required input-large"></form:input>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">商品类别：</label>
                    <div class="controls">
                        <form:input path="categoryName" cssClass="required input-large"></form:input>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">报告结论：</label>
                    <div class="controls">
                        <form:textarea path="conclusion" cssClass="required input-xxxlarge" rows="3"></form:textarea>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">提交报告总人数：</label>
                    <div class="controls">
                        <span>${reportedCount}人</span>
                    </div>
                </div>
                <div class="form-actions">
                    <input id="btnSubmit" class="btn btn-primary" type="submit" value="<c:if test="${null != feedback.id}">重新</c:if>发布报告"/>
                    <input id="btnCancel" class="btn" type="button" value="返 回" onclick="window.parent.window.jBox.close();"/>
                </div>
                <c:if test="${questionnaire.childList != null && fn:length(questionnaire.childList) > 0}">
                    <c:forEach items="${questionnaire.childList}" var="child" varStatus="childIndex">
                        <div class="panel panel-info">
                            <div class="panel-heading">
                                <div class="panel-title" style="font-size: 14px;">
                                    ${child.title}
                                </div>
                            </div>
                            <div class="panel-body">
                                <table id="childTable_${childIndex.index}" class="table table-bordered table-hover">
                                    <c:forEach items="${child.questionList}" var="question" varStatus="questionIndex">
                                        <tr>
                                            <td>
                                                <c:if test="${question.type == 1 || question.type == 2}">
                                                    <input id="question_${question.id}" type="checkbox" name="questionIdSet" value="${question.id}"<c:if test="${fn:indexOf(selectedQuestionId,question.id) >= 0}"> checked</c:if>/>
                                                </c:if>
                                                <label for="question_${question.id}">${questionIndex.index + 1}、${question.title}</label></br>
                                                <c:if test="${question.type == 1 || question.type == 2}">
                                                    <c:forEach items="${question.options}" var="option" varStatus="optionIndex">
                                                        <c:set var="key" value="${question.id}${option}"></c:set>
                                                        <span>${letterArray[optionIndex.index]}：${option}[${questionMap[key] == null ? 0 : questionMap[key]}人]</span><br>
                                                    </c:forEach>
                                                </c:if>
                                                <c:if test="${question.type == 4}">
                                                    <a href="javascript:void(0)" onclick="viewAllAnswer('${question.id}','${question.title}')">查看所有回答</a>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </table>
                            </div>
                        </div>
                    </c:forEach>
                </c:if>
                <c:if test="${questionnaire.childList == null}">
                    <table id="contentTable" class="table table-bordered table-hover">
                        <c:forEach items="${questionnaire.questionList}" var="question" varStatus="questionIndex">
                            <tr>
                                <td>
                                    <c:if test="${question.type == 1 || question.type == 2}">
                                        <input type="checkbox" name="questionIdSet" value="${question.id}"<c:if test="${fn:indexOf(selectedQuestionId,question.id) >= 0}"> checked</c:if>/>
                                    </c:if>
                                    <span>${questionIndex.index + 1}、${question.title}</span></br>
                                    <c:if test="${question.type == 1 || question.type == 2}">
                                        <c:forEach items="${question.options}" var="option" varStatus="optionIndex">
                                            <c:set var="key" value="${question.id}${option}"></c:set>
                                            <span>${letterArray[optionIndex.index]}：${option}[${questionMap[key] == null ? 0 : questionMap[key]}人]</span><br>
                                        </c:forEach>
                                    </c:if>
                                    <c:if test="${question.type == 4}">
                                        <a href="javascript:void(0)" onclick="viewAllAnswer('${question.id}','${question.title}')">查看所有回答</a>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:if>
            </div>
        </div>
    </form:form>
</body>
</html>