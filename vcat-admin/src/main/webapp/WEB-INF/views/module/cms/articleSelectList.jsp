<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>选择文章</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
            var callBackFuncName = "${callBackFuncName}" == "" ? "articleSelectAddOrDel" : "${callBackFuncName}";
			$("input[name=id]").each(function(){
				var articleSelect = null;
				var openWindow = null;
				if (top.mainFrame.cmsMainFrame){
					articleSelect = top.mainFrame.cmsMainFrame.articleSelect;
					openWindow = top.mainFrame.cmsMainFrame;
				}else if($('.curholder',parent.document).find('iframe').length > 0){ // 如果开启了页签模式，则获取父窗口的激活iframe中的articleSelect
					articleSelect = eval("parent.document." + $('.curholder',parent.document).find('iframe').attr('id') + ".articleSelect");
					openWindow = eval("parent.document." + $('.curholder',parent.document).find('iframe').attr('id'));
				}else{
					articleSelect = top.mainFrame.articleSelect;
					openWindow = top.mainFrame;
				}
                if(articleSelect){
                    for (var i=0; i<articleSelect.length; i++){
                        if (articleSelect[i][0]==$(this).val()){
                            this.checked = true;
                        }
                    }
                }
				$(this).click(function(){
					var id = $(this).val(), title = $(this).attr("title");
					if (openWindow){
						var result = eval("openWindow." + callBackFuncName + "(id, title)");
						if("function" == typeof(result)){
							result.call(this);
						}
					}
				});
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<div style="margin:10px;">
	<form:form id="searchForm" modelAttribute="article" action="${ctx}/cms/article/selectList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input name="lockCategory" type="hidden" value="${lockCategory}"/>
        <input name="selectType" type="hidden" value="${selectType}"/>
        <input name="callBackFuncName" type="hidden" value="${callBackFuncName}"/>
		<c:if test="${!lockCategory}">
		<label>栏目：</label><sys:treeselect id="category" name="category.id" value="${article.category.id}" labelName="category.name" labelValue="${article.category.name}"
					title="栏目" url="/cms/category/treeData" module="article" cssClass="input-small"/>
		</c:if>
		<c:if test="${lockCategory}">
			<input type="hidden" id="category" name="category.id" value="${article.category.id}"/>
		</c:if>
		<label>标题：</label><form:input path="title" htmlEscape="false" maxlength="50" class="input-small"/>&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;
	</form:form>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="text-align:center;">选择</th><th>栏目</th><th>标题</th><th>权重</th><th>点击数</th><th>发布者</th><th>更新时间</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="article">
			<tr>
				<td style="text-align:center;"><input type="${selectType == null || selectType eq '' ? 'checkbox' : selectType}" name="id" value="${article.id}" title="${fns:abbr(article.title,80)}" /></td>
				<td>${article.category.name}</td>
				<td>${fns:abbr(article.title,80)}</td>
				<td>${article.weight}</td>
				<td>${article.hits}</td>
				<td>${article.createBy.name}</td>
				<td><fmt:formatDate value="${article.updateDate}" type="both"/></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	</div>
</body>
</html>