<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品列表</title>
	<meta name="decorator" content="default"/>
    <script type="text/javascript">include('product_spec','${ctxStatic}/product/',['spec.js?${version}']);</script>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		function clearForm(){
			$('#categoryId').val("");
			$('#categoryName').val("");
			$('#name').val("");
			$(':radio:checked').attr("checked",false);
		}
        function saveOrder(){
            var couldSubmit = true;
            if($(':input[name=displayOrders]').length == 0){
                top.$.jBox.tip("无可排序的商品！");
                return false;
            }
            $(':input[name=displayOrders]').each(function (){
                var input = $(this);
                if(isNull(input.val()) || !isInteger(input.val())){
                    alertx("商品排序格式不正确！",function (){input.focus();});
                    couldSubmit = false;
                    return false;
                }
            });
            if(couldSubmit){
                loading("正在保存，请稍候...");
                $.ajax({
                    type : 'POST',
                    url : ctx + "/ec/product/saveOrder?" + $('#orderForm').serialize(),
                    success : function(){
                        try{
                            top.$.jBox.tip("商品排序保存成功！");
                            $("#searchForm").submit();
                        }catch(e){
                            console.log("保存商品排序失败："+e.message);
                            return doError(e);
                        }
                    },
                    error: function(XMLHttpRequest) {
                        return doError(XMLHttpRequest);
                    }
                });
            }
        }
        function batchArchived(archived){
            var archivedTitle = archived == "1" ? "下" : "上";
            if($('#contentTable :checked').length == 0){
                top.$.jBox.tip("请选择需要" + archivedTitle + "架的商品！");
                return false;
            }
            loading("正在批量" + archivedTitle + "架商品，请稍候...");
            $.ajax({
                type : 'POST',
                url : ctx + "/ec/product/batchArchived?archived=" + archived + "&" + $('#contentTable :checked').serialize(),
                success : function(){
                    try{
                        top.$.jBox.tip("批量" + archivedTitle + "架商品成功！");
                        $("#searchForm").submit();
                    }catch(e){
                        console.log("批量" + archivedTitle + "架商品失败："+e.message);
                        return doError(e);
                    }
                },
                error: function(XMLHttpRequest) {
                    return doError(XMLHttpRequest);
                }
            });
        }
        function archived(id,archived){
            archived = archived == '1' ? "0" : "1";
            var archivedTitle = archived == "1" ? "下" : "上";
            confirmx('确认'+archivedTitle+'架该商品？',function (){
                $.ajax({
                    type : 'POST',
                    url : ctx + "/ec/product/archived?id="+id+"&archived=" + archived,
                    success : function(){
                        try{
                            top.$.jBox.tip(archivedTitle + "架商品成功！");
                            $("#searchForm").submit();
                        }catch(e){
                            console.log(archivedTitle + "架商品失败："+e.message);
                            return doError(e);
                        }
                    },
                    error: function(XMLHttpRequest) {
                        return doError(XMLHttpRequest);
                    }
                });
            });
        }
        function showItemDialog(productId,productName){
            $.ajax({
                type : 'POST',
                url : ctx + "/ec/product/getProductItemList?id=" + productId,
                success : function(itemList){
                    try{
                        $('#itemTable tr:not(:first)').remove();
                        $('#itemProductName').html(productName + " ");
                        var trHTML = "";
                        for(var i = 0; i < itemList.length; i++){
                            var item = itemList[i];
                            trHTML += "<tr class='templateItemRow'>";
                            trHTML += '<td style="width: 100px;">';
                            trHTML += '<input type="hidden" name="itemList[' + i + '].id" value="' + item.id + '"/>';
                            trHTML += '<input type="hidden" name="itemList[' + i + '].itemSn" value="' + item.itemSn + '"/>';
                            trHTML += '<input type="hidden" name="itemList[' + i + '].name" value="' + item.name + '"/>';
                            trHTML += item.itemSn + '</td>';
                            trHTML += '<td>' + item.name + '</td>';

                            trHTML += getItemRowHTML(item.name,i,item);

                            trHTML += "</tr>";
                        }
                        $('#itemTable').append(trHTML);
                    }catch(e){
                        console.log("获取商品规格失败："+e.message);
                        return doError(e);
                    }
                },
                error: function(XMLHttpRequest) {
                    return doError(XMLHttpRequest);
                }
            });
            showDialog("itemDialog");
        }
        function saveItem(){
            if(checkSpec()){
                $.ajax({
                    type : 'POST',
                    url : ctx + "/ec/product/saveProductItem?" + $('#itemTable :input').serialize(),
                    success : function(){
                        try{
                            top.$.jBox.tip("保存规格成功！");
                            showDialog("itemDialog");
                            $("#searchForm").submit();
                        }catch(e){
                            console.log("获取商品规格失败："+e.message);
                            return doError(e);
                        }
                    },
                    error: function(XMLHttpRequest) {
                        return doError(XMLHttpRequest);
                    }
                });
            }
        }
        function stick(id,name){
            confirmx("确认置顶 " + name + "？",function (){
                $.getJSON(ctx + "/ec/product/stick",{id:id},function (){
                    alertx("置顶"+name+"成功！",function (){
                        location.href = location.href;
                    });
                });
            });
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li <c:if test="${type eq 'all'}">class="active"</c:if>><a href="${ctx}/ec/product/list?type=all">全部商品</a></li>
		<li <c:if test="${type eq 'sj'}">class="active"</c:if>><a href="${ctx}/ec/product/list?type=sj&archived=0">上架商品</a></li>
		<li <c:if test="${type eq 'xj'}">class="active"</c:if>><a href="${ctx}/ec/product/list?type=xj&archived=1">下架商品</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="product" action="${ctx}/ec/product/list?type=${type}&archived=${product.archived}&mode=${mode}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <label>供应商：</label>
        <form:select id="supplierId" path="sqlMap['supplierId']" class="input-xlarge">
            <form:option value="" label="全部"/>
            <form:options items="${supplierList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
        </form:select>
        <label>品牌：</label>
        <form:select id="brandId" path="sqlMap['brandId']" class="input-xlarge">
            <form:option value="" label="全部"/>
            <form:options items="${brandList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
        </form:select>
		<label>分类：</label>
        <sys:treeselect id="category" name="category.id" value="${product.category.id}" labelName="category.name" labelValue="${product.category.name}"
            title="栏目" url="/ec/category/treeData" module="product" notAllowSelectRoot="false" cssClass="input-small"/>
        <div style="margin-top: 5px;">
            <label>是否爆品：</label>
            <form:select id="isHotSale" path="sqlMap['isHotSale']" cssStyle="width: 70px">
                <form:option value="" label="全部"/>
                <form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
            </form:select>
            <label>名称：</label>
            <form:input path="name" htmlEscape="false" maxlength="50" class="input-large"/>&nbsp;
            <%--<label>推荐类型：</label>--%>
                <%--<form:radiobuttons path="sqlMap['recommendType']" items="${fns:getDictList('ec_recommend_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>--%>
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
            <input id="btnSaveOrder" class="btn btn-primary" type="button" value="保存排序" onclick="saveOrder()"/>
            <input id="btnSaveOrder" class="btn btn-primary" type="button" value="批量上架" onclick="batchArchived(0)"/>
            <input id="btnSaveOrder" class="btn btn-primary" type="button" value="批量下架" onclick="batchArchived(1)"/>
            <input id="btnClear" class="btn btn-default" type="button" value="清空" onclick="clearForm()"/>
        </div>
	</form:form>
	<sys:message content="${message}"/>
    <form:form id="orderForm">
	<table id="contentTable" class="table table-bordered table-condensed table-hover">
		<thead>
			<tr>
                <th style="width: 30px;"><a href="javascript:void(0)" onclick="$(':checkbox').attr('checked',true)" ondblclick="$(':checkbox').attr('checked',false)" title="全选/双击清空">选择</a></th>
				<th style="width: 80px;">分类</th>
                <th style="width: 120px;">品牌</th>
				<th style="max-width: 200px;">名称</th>
                <th style="width: 50px">销量</th>
				<th style="width: 100px;">售价</th>
				<th style="width: 100px;">佣金</th>
				<th style="width: 50px;">
                    <%--<a href="javascript:void(0);" onclick="$(':input[name=displayOrders]').each(function(){$(this).val(parseInt(getDefault(this.value,0))-1)})">减</a>--%>
                    排序
                    <%--<a href="javascript:void(0);" onclick="$(':input[name=displayOrders]').each(function(){$(this).val(parseInt(getDefault(this.value,0))+1)})">加</a>--%>
                </th>
                <th style="width: 30px;">爆品</th>
				<th style="width: 30px;">热销</th>
                <th style="width: 30px;">新品</th>
				<th style="width: 30px;">下架</th>
				<th style="width: 120px;">操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="product">
			<tr>
                <td><input type="checkbox" name="archivedProductId" value="${product.id}"></td>
				<td>
                    <a href="javascript:" onclick="$('#categoryId').val('${product.category.id}');$('#categoryName').val('${product.category.name}');$('#searchForm').submit();return false;" title="${product.category.name}">${fns:abbr(product.category.name,12)}</a>
                </td>
                <td>
                    <a href="javascript:" onclick="$('#brandId').val('${product.brand.id}');$('#searchForm').submit();return false;" title="${product.brand.name}">${fns:abbr(product.brand.name,20)}</a>
                </td>
				<td><a href="${ctx}/ec/product/form?id=${product.id}" title="${product.name}">${fns:abbr(product.name,100)}</a></td>
                <td>${product.saledNum}</td>
				<td><a href="javascript:void(0)" onclick="showItemDialog('${product.id}','${product.name}')" title="修改价格">${product.price}</a></td>
				<td>${product.saleEarning}</td>
				<td>
                    <input type="hidden" name="productIds" value="${product.id}"/>
                    <input type="text" name="displayOrders" value="${product.displayOrder}" class="input-mini" style="margin:0;padding:0;text-align:center;"/>
                </td>
                <td style="color: ${product.isHotSale eq 0 ? 'cornflowerblue':'green'};">${product.isHotSale eq 0 ? '否':'是'}</td>
				<td style="color: ${product.isHot eq 0 ? 'cornflowerblue':'green'};">${product.isHot eq 0 ? '否':'是'}</td>
                <td style="color: ${product.isNew eq 0 ? 'cornflowerblue':'green'};">${product.isNew eq 0 ? '否':'是'}</td>
				<td style="color: ${product.archived eq 0 ? 'green':'red'};">${product.archived eq 0 ? '否':'是'}</td>
				<td>
					<shiro:hasPermission name="ec:product:edit">
                        <a href="${ctx}/ec/product/form?id=${product.id}" title="修改${product.name}">修改</a>
                        <a href="javascript:void(0)" onclick="stick('${product.id}','${product.name}')" title="置顶${product.name}">置顶</a>
                        <a href="javascript:void(0)" onclick="archived('${product.id}','${product.archived}')">${product.archived eq 0 ? '下架':'上架'}</a>
                        <c:if test="${product.delFlag eq '0'}">
                            <a href="${ctx}/ec/product/delete?id=${product.id}&name=${product.name}" title="删除${product.name}" onclick="return confirmx('确认要删除 ${product.name} 吗？', this.href)">删除</a>
                        </c:if>
					</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
    </form:form>
	<div class="pagination">${page}</div>
    <div id="itemDialog" class="vcat-dialog" style="width: 1200px;">
        <div class="panel panel-info"><!-- 设置商品的规格 -->
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    <span id="itemProductName"></span>商品规格
                    <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('specDialog')"></a>
                </div>
            </div>
            <div class="panel-body">
                <table id="itemTable" class="table table-bordered table-hover">
                    <tbody>
                    <tr>
                        <th style="width: 150px;">货号</th>
                        <th>规格名称</th>
                        <th width="5%">销售价</th>
                        <th width="5%">结算价</th>
                        <th width="5%">销售佣金</th>
                        <th width="5%">销售分红</th>
                        <th width="5%">一级团队分红</th>
                        <th width="5%">二级团队分红</th>
                        <th width="5%">扣点金额</th>
                        <th width="30px">库存</th>
                        <th width="50px">重量</th>
                    </tr>
                    </tbody>
                </table>
                <div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;">
                    <input onclick="saveItem()" class="btn btn-success" type="button" value="保存"/>
                    <input onclick="showDialog('itemDialog')" class="btn btn-default" type="button" value="返回"/>
                </div>
            </div>
        </div>
    </div>
</body>
</html>