<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>品牌管理</title>
	<meta name="decorator" content="default"/>
    <script type="application/javascript">
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
        function clearForm(){
            $('#supplierId').val("");
            $('#supplierId').select2();
            $('#name').val("");
        }
    </script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/ec/supplier/brandList">品牌列表</a></li>
		<shiro:hasPermission name="ec:supplier:add"><li><a href="${ctx}/ec/supplier/brandForm">品牌添加</a></li></shiro:hasPermission>
	</ul>
    <form:form id="searchForm" modelAttribute="brand" action="${ctx}/ec/supplier/brandList" method="post" class="breadcrumb form-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <label>供应商：</label>
        <form:select id="supplierId" path="supplier.id" class="input-xlarge">
            <form:option value="" label="全部"/>
            <form:options items="${supplierList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
        </form:select>
        <label>名称：</label>
        <form:input path="name" htmlEscape="false" maxlength="50" class="input-large" placeholder="供应商或品牌名称"/>&nbsp;
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
        <input id="btnClear" class="btn btn-default" type="button" value="清空" onclick="clearForm()"/>
    </form:form>
	<sys:message content="${message}"/>
    <table id="treeTable" class="table table-bordered table-hover">
        <tr>
            <th width="100px">所属供应商</th>
            <th width="100px">品牌名称</th>
            <th width="100px">邮费</th>
            <th>简介</th>
            <th width="50px">LOGO</th>
            <th width="80px">操作</th>
        </tr>
        <c:forEach items="${page.list}" var="entity">
            <tr>
                <td>${entity.supplier.name}</td>
                <td><a href="${ctx}/ec/supplier/brandForm?id=${entity.id}">${entity.name}</a></td>
                <td>${entity.freightCharge}</td>
                <td>${fns:abbr(entity.intro,100)}</td>
                <td><img src="${entity.logoUrlPath}" width="50px" title="${entity.name}" alt="未上传"></td>
                <shiro:hasPermission name="ec:brand:edit">
                    <td>
                        <a href="${ctx}/ec/supplier/brandForm?id=${entity.id}">修改</a>
                    </td>
                </shiro:hasPermission>
            </tr>
        </c:forEach>
    </table>
    <div class="pagination">${page}</div>
</body>
</html>