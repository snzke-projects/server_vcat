<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>选择商品</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var openWindow = null;
        var selectedIds = "${selectedIds}";
        var selectedIdsKey = "${selectedIdsKey}" ? "${selectedIdsKey}" : "productIdSelectedIds";
		$(function() {
			if (top.mainFrame.cmsMainFrame){
				openWindow = top.mainFrame.cmsMainFrame;
			}else if($('.curholder',parent.document).find('iframe').length > 0){ // 如果开启了页签模式，则获取父窗口的激活iframe中的articleSelect
				openWindow = eval("parent.document." + $('.curholder',parent.document).find('iframe').attr('id'));
			}else{
				openWindow = top.mainFrame;
			}
            $("#contentTable").datagrid(getDatagridOption({
                idField: "id",
                singleSelect: ${radio},
                url: '${ctx}/ec/product/listData?' + $('#searchForm').serialize(),
                columns: [[
                    {field: 'categoryName', title: '分类', width: 140, formatter: function (value, row){return row.category.name}},
                    {field: 'brandName', title: '品牌', width: 140, formatter: function (value, row){return row.brand.name}},
                    {field: 'name', title: '商品名称', width: 500},
                    {field: 'price', title: '售价', width: 140},
                    {field: 'saleEarning', title: '佣金', width: 140},
                    {field: 'inventory', title: '库存', width: 80},
                    {field: 'archived', title: '上架状态', formatter: function (value, row){return value == 0 ? "上架" : "下架"}}
                ]],
                onSelect : function (index, row){
                    updateSelectedIds(row.id,true);
                    eval("openWindow.${selectedIdsKey} = $('#selectedIds').val()");
                },
                onUnselect : function (index, row){
                    updateSelectedIds(row.id,false);
                    eval("openWindow.${selectedIdsKey} = $('#selectedIds').val()");
                },
                onLoadSuccess : function (data){
                    if(isNull(selectedIds)){
                        return;
                    }
                    var idArray = selectedIds.split("|");
                    for(var i = 0;i < idArray.length; i++){
                        $('#contentTable').datagrid('selectRecord', idArray[i]);
                    }
                }
            }));
		});

		function clearForm(){
			$('#categoryId').val("");
			$('#categoryName').val("");
            $('#archived').val("");
            $('#archived').select2();
			$('#name').val("");
		}

        function updateSelectedIds(id,checked){
            var ids = $('#selectedIds').val();
            if('true' == '${radio}'){
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
		<form:form id="searchForm" modelAttribute="product" class="breadcrumb form-search">
            <input name="radio" type="hidden" value="${radio}"/>
            <input name="type" type="hidden" value="${type}"/>
            <input id="selectedIds" name="selectedIds" type="hidden" value="${selectedIds}"/>
            <input name="isVirtualProduct" type="hidden" value="${product.isVirtualProduct}"/>
            <input name="selectedIdsKey" type="hidden" value="${selectedIdsKey}">
			<label>分类：</label>
			<sys:treeselect id="category" name="category.id" value="${product.category.id}" labelName="category.name" labelValue="${product.category.name}"
							title="栏目" url="/ec/category/treeData" module="product" notAllowSelectRoot="false" cssClass="input-small"/>
            <label>是否上架：</label>
            <form:select id="archived" path="sqlMap['archived']" cssClass="input-medium">
                <form:option value="">全部</form:option>
                <form:option value="0">上架商品</form:option>
                <form:option value="1">下架商品</form:option>
            </form:select>&nbsp;
			<label>名称：</label>
			<form:input path="name" htmlEscape="false" maxlength="50" class="input-small"/>&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="reloadGrid('/ec/product/listData')"/>&nbsp;&nbsp;
			<input id="btnClear" class="btn btn-default" type="button" value="清空" onclick="clearForm()"/>&nbsp;&nbsp;
		</form:form>
		<table id="contentTable" style="width: 100%"></table>
	</div>
</body>
</html>