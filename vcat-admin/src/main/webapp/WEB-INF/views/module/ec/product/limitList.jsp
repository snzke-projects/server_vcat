<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>限购商品列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}
		function deleteLimit(id,name,itemName){
			confirmx("确认删除限购商品【" + name + "】【" + itemName + "】？",function (){
				$.get(ctx + "/ec/product/deleteLimit?id="+id,function(){alertx("删除成功！");$("#searchForm").submit();});
			});
		}

        function clearForm(){
            $('#productType').val("");
            $('#productType').select2();
            $('#keyWord').val("");
            $('#st').val("");
            $('#et').val("");
        }
	</script>
</head>
<body>
<form:form id="searchForm" modelAttribute="productLimit" action="${ctx}/ec/product/limitList" method="post" class="breadcrumb form-search">
	<div class="panel panel-info">
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				查询条件
			</div>
		</div>
		<div class="panel-body">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <label>限购区域：</label>
            <form:select path="productType" cssClass="input-small">
                <form:option value="" label="全部"/>
                <form:options items="${fns:getDictList('ec_product_limit_type')}" itemValue="value" itemLabel="label"></form:options>
            </form:select>
            <label>限购目标：</label>
            <form:select path="userType" cssClass="input-medium">
                <form:option value="" label="全部"/>
                <form:options items="${fns:getDictList('ec_product_limit_target')}" itemValue="value" itemLabel="label"></form:options>
            </form:select>
			<label>关键字：</label>
			<form:input id="keyWord" path="sqlMap['keyWord']" placeholder="商品名称、规格名称"/>&nbsp;
			<label>限购时间：</label>
			<input id="st" name="sqlMap['st']" type="text" readonly="readonly" maxlength="16" class="input-medium Wdate"
				   value="${productLimit.sqlMap['st']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
			<label>至</label>
			<input id="et" name="sqlMap['et']" type="text" readonly="readonly" maxlength="16" class="input-medium Wdate"
				   value="${productLimit.sqlMap['et']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;
            <input id="btnClear" class="btn" type="button" value="清空" onclick="clearForm()"/>
		</div>
	</div>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-bordered table-hover">
	<thead>
	<tr>
		<th width="60px">限购区域</th>
        <th width="80px">限购目标</th>
		<th>商品名称</th>
		<th width="200px">商品规格名称</th>
		<th width="100px">限购次数</th>
		<th width="260px">限购时间段</th>
		<th width="69px">操作</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${page.list}" var="productLimit">
		<tr>
			<td><a href="javascript:void(0);" onclick="$('#productType').val('${productLimit.productType}');$('#searchForm').submit();return false;">${fns:getDictLabel(productLimit.productType, 'ec_product_limit_type', '未知类型')}</a></td>
            <td><a href="javascript:void(0);" onclick="$('#userType').val('${productLimit.userType}');$('#searchForm').submit();return false;">${fns:getDictLabel(productLimit.userType, 'ec_product_limit_target', '未知类型')}</a></td>
			<td title="${productLimit.productItem.product.name}"><a href="javascript:void(0);" onclick="$('#keyWord').val('${productLimit.productItem.product.name}');$('#searchForm').submit();return false;">${fns:abbr(productLimit.productItem.product.name,50)}</a></td>
			<td title="${productLimit.productItem.name}"><a href="${ctx}/ec/product/limitForm?id=${productLimit.id}">${fns:abbr(productLimit.productItem.name,30)}</a></td>
			<td><c:if test="${productLimit.interval != null && productLimit.interval != 0}">${productLimit.interval}&nbsp;天&nbsp;</c:if>${productLimit.times}&nbsp;次</td>
			<td><fmt:formatDate value="${productLimit.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/>&nbsp;至&nbsp;<fmt:formatDate value="${productLimit.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			<td>
				<shiro:hasPermission name="ec:product:limit:edit">
                    <a href="${ctx}/ec/product/limitForm?id=${productLimit.id}">修改</a>
					<a href="javascript:void(0)" onclick="deleteLimit('${productLimit.id}','${productLimit.productItem.product.name}','${productLimit.productItem.name}')">删除</a>
				</shiro:hasPermission>
			</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>