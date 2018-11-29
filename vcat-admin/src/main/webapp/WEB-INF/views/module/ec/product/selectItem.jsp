<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>选择商品规格</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var openWindow = null;
        var selectedItemIdsName = "${selectedItemIdsName}" == "" ? "selectedItemIds" : "${selectedItemIdsName}";
        var radio = "${radio}" != "false" ? true : false;
		$(function() {
			if (top.mainFrame.cmsMainFrame){
				openWindow = top.mainFrame.cmsMainFrame;
			}else if($('.curholder',parent.document).find('iframe').length > 0){ // 如果开启了页签模式，则获取父窗口的激活iframe
				openWindow = eval("parent.document." + $('.curholder',parent.document).find('iframe').attr('id'));
			}else{
				openWindow = top.mainFrame;
			}
            $("input[name=object]").click(function(){
                updateSelectedIds(this.value,this.checked);
                eval("openWindow." + selectedItemIdsName + " = $('#selectedIds').val()");
            });
		});

		function clearForm(){
			$('#categoryId').val("");
			$('#categoryName').val("");
            $('#brandId').val("");
            $('#brandId').select2();
			$('#keyWord').val("");
		}

		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }

        function updateSelectedIds(id,checked){
            var ids = $('#selectedIds').val();
            if(radio){
                ids="|"+id;
            }else if(checked){
                if(ids.indexOf(id) < 0){
                    ids+="|"+id;
                }
            }else{
                ids = ids.replace("|"+id,"");
            }
            $('#selectedIds').val(ids);
        }
	</script>
</head>
<body>
	<div style="margin:10px;">
		<form:form id="searchForm" modelAttribute="productItem" action="${ctx}/ec/product/selectItem" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <input name="radio" type="hidden" value="${radio}"/>
            <input name="selectedItemIdsName" type="hidden" value="${selectedItemIdsName}"/>
            <input id="selectedIds" name="selectedIds" type="hidden" value="${selectedIds}">
			<label>分类：</label>
			<sys:treeselect id="category" name="product.category.id" value="${productItem.product.category.id}" labelName="product.category.name" labelValue="${productItem.product.category.name}"
							title="栏目" url="/ec/category/treeData" module="product" notAllowSelectRoot="false" cssClass="input-small"/>
            <label>品牌：</label>
            <form:select id="brandId" path="product.brand.id" class="input-medium">
                <form:option value="" label="全部"/>
                <form:options items="${brandList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
            </form:select>
			<label>关键字：</label>
			<form:input id="keyWord" path="sqlMap['keyWord']" htmlEscape="false" maxlength="50" class="input-large" placeholder="商品名称，规格名称"/>&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;
			<input id="btnClear" class="btn btn-default" type="button" value="清空" onclick="clearForm()"/>&nbsp;&nbsp;
		</form:form>
		<table id="contentTable" class="table table-bordered table-hover">
			<thead>
			<tr>
				<th style="width: 30px;">选择</th>
                <th style="width: 100px;">规格类型</th>
				<th style="width: 100px;">分类</th>
                <th style="width: 120px;">品牌</th>
				<th>商品名称</th>
                <th style="width: 120px;">规格名称</th>
                <th style="width: 60px;">售价</th>
                <th style="width: 60px;">结算价</th>
                <th style="width: 60px;">佣金</th>
                <th style="width: 60px;">分红</th>
                <th style="width: 60px;">一级团队分红</th>
                <th style="width: 60px;">二级团队分红</th>
                <th style="width: 60px;">扣点</th>
                <th style="width: 60px;">可使用V猫币</th>
                <th style="width: 40px;">库存</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${page.list}" var="productItem" varStatus="status">
				<tr>
					<td><input id="product_item_${status.index}" type="${radio ? 'radio' : 'checkbox'}" name="object" index="${status.index}" value="${productItem.id}" title="${fns:abbr(productItem.name,80)}" <c:if test="${fn:indexOf(selectedIds,productItem.id) > -1}">checked</c:if>/></td>
                    <td>${productItem.typeLabel}</td>
					<td><a href="javascript:void(0)" onclick="$('#categoryId').val('${productItem.product.category.id}');$('#categoryName').val('${productItem.product.category.name}');$('#searchForm').submit();return false;">${productItem.product.category.name}</a></td>
                    <td><a href="javascript:void(0)" onclick="$('#brandId').val('${productItem.product.brand.id}');$('#searchForm').submit();return false;" title="${productItem.product.brand.name}">${fns:abbr(productItem.product.brand.name,16)}</a></td>
					<td><label for="product_item_${status.index}" title="${productItem.product.name}" style="width: 100%">${fns:abbr(productItem.product.name,30)}</label></td>
					<td><label for="product_item_${status.index}" title="${productItem.name}" style="width: 100%">${fns:abbr(productItem.name,16)}</label></td>
					<td>${productItem.retailPrice}</td>
                    <td>${productItem.purchasePrice}</td>
                    <td>${productItem.saleEarning}</td>
                    <td>${productItem.bonusEarning}</td>
                    <td>${productItem.firstBonusEarning}</td>
                    <td>${productItem.secondBonusEarning}</td>
                    <td>${productItem.point}</td>
                    <td>${productItem.couponValue}</td>
                    <td>${productItem.inventory}</td>

				</tr>
			</c:forEach>
			</tbody>
		</table>
		<div class="pagination">${page}</div>
	</div>
</body>
</html>