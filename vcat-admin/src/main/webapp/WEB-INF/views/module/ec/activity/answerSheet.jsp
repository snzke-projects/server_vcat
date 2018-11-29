<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>报告情况</title>
	<meta name="decorator" content="default"/>
</head>
<body>
    <div class="panel panel-info">
        <div class="panel-heading">
            <div class="panel-title" style="font-size: 14px;">
                ${questionnaire.title}
            </div>
        </div>
        <div class="panel-body">
            <c:if test="${questionnaire.answerSheet == null && questionnaire.childList != null && fn:length(questionnaire.childList) > 0}">
                <c:forEach items="${questionnaire.childList}" var="child" varStatus="childIndex">
                    <div class="panel panel-info">
                        <div class="panel-heading">
                            <div class="panel-title" style="font-size: 14px;">
                                ${child.title}
                            </div>
                        </div>
                        <div class="panel-body">
                            <table id="childTable_${childIndex.index}" class="table table-bordered table-hover">
                                <c:forEach items="${child.answerSheet.questionList}" var="question" varStatus="questionIndex">
                                    <tr>
                                        <td>
                                            <span>${questionIndex.index + 1}、${question.title}</span></br>
                                            <c:if test="${question.type == 1 || question.type == 2}">
                                                <c:set var="answer" value=",${question.answer}"></c:set>
                                                <c:forEach items="${question.options}" var="option" varStatus="optionIndex">
                                                    <c:set var="optionTemp" value=",${option}"></c:set>
                                                    <span<c:if test="${fn:indexOf(answer,optionTemp) >= 0}"> style="color: green"</c:if>>${letterArray[optionIndex.index]}：${option}　</span>
                                                </c:forEach>
                                            </c:if>
                                            <c:if test="${question.type == 4}">
                                                <span><pre style="color: green">${question.answer}</pre></span>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </table>
                        </div>
                    </div>
                </c:forEach>
            </c:if>
            <c:if test="${questionnaire.answerSheet != null}">
                <table id="contentTable" class="table table-bordered table-hover">
                    <c:forEach items="${questionnaire.answerSheet.questionList}" var="question" varStatus="questionIndex">
                        <tr>
                            <td>
                                <span>${questionIndex.index + 1}、${question.title}</span></br>
                                <c:if test="${question.type == 1 || question.type == 2}">
                                    <c:set var="answer" value=",${question.answer}"></c:set>
                                    <c:forEach items="${question.options}" var="option" varStatus="optionIndex">
                                        <c:set var="optionTemp" value=",${option}"></c:set>
                                        <span<c:if test="${fn:indexOf(answer,optionTemp) >= 0}"> style="color: green"</c:if>>${letterArray[optionIndex.index]}：${option}　</span>
                                    </c:forEach>
                                </c:if>
                                <c:if test="${question.type == 4}">
                                    <span><pre style="color: green">${question.answer}</pre></span>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </c:if>
        </div>
    </div>
</body>
</html>