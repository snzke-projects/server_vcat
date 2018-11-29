<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品提前结算</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        var selectedProductItemArray = null;
        var selectedItemIds;
        $(function (){
            $('#btnAdd').bind("click",function(){
                showDialog('productItemDialog');
            });
        });
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}
        function selectItem(){
            top.$.jBox.open("iframe:${ctx}/ec/product/selectItem?sqlMap[\'noReconciliation\']=true", "选择商品规格",$(top.document).width()-220,$(top.document).height()-180,{
                buttons: {"确定": 'ok',"关闭": true},
                loaded: function () {
                    $(".jbox-content", top.document).css("overflow-y", "hidden");
                },submit : function(buttonId){
                    if(buttonId == "ok"){
                        setItemArrayHTML();
                    }
                }
            });
        }
        function setItemArrayHTML(){
            $('.selectItemTR').remove();
            $.get(ctx + "/ec/product/getProductItemListByIds?ids="+selectedItemIds,function(array){selectedProductItemArray = array});
            var html = '';
            if(selectedProductItemArray && selectedProductItemArray.length > 0){
                for(var i = 0;i < selectedProductItemArray.length;i++){
                    var trHtml = '<tr class="selectItemTR" id="selectItemTR_'+i+'">';
                    var item = selectedProductItemArray[i];
                    var itemTitle = item.product.show+' - '+item.name;
                    trHtml += '<td><span title="'+item.product.show+'">'+abbr(item.product.show,60)+'</span></td>';
                    trHtml += '<td><span title="'+item.name+'">'+abbr(item.name,18)+'</span></td>';
                    trHtml += '<td>'+item.retailPrice+'</td>';
                    trHtml += '<td>'+item.purchasePrice+'</td>';
                    trHtml += '<td>'+item.saleEarning+'</td>';
                    trHtml += '<td>'+item.bonusEarning+'</td>';
                    trHtml += '<td>'+item.firstBonusEarning+'</td>';
                    trHtml += '<td>'+item.secondBonusEarning+'</td>';
                    trHtml += '<td>'+item.point+'</td>';
                    trHtml += '<td>'+item.couponValue+'</td>';
                    trHtml += '<td>'+item.inventory+'</td>';
                    trHtml += '<td><input name="surplusQuantityArray" type="text" class="surplusQuantity input-min" title="'+itemTitle+'"></td>';
                    trHtml += '<td><i class="icon-remove" onclick="$(\'#selectItemTR_'+i+'\').remove()"></i>';
                    trHtml += '<input name="itemIdArray" type="hidden" value="'+item.id+'">';
                    trHtml += '<input name="productIdArray" type="hidden" value="'+item.product.id+'"></td>';
                    trHtml += '</tr>';
                    html += trHtml;
                }
            }
            $('.itemTable').append(html);
        }
        function reconciliation(){
            var hasError = false;
            $('.itemTable .surplusQuantity').each(function (){
                var input = $(this);
                if(isNull(input.val()) || !isNumber(input.val())){
                    alertx("提前结算数量格式不正确",function (){
                        input.focus();
                    });
                    hasError = true;
                    return false;
                }
            });

            if(hasError){
                return false;
            }

            $.getJSON(ctx + "/ec/supplier/saveProductReconciliation?" + $('.itemTable :input').serialize(),function(){
                $("#searchForm").submit();
                alertx('保存成功！');
            });
        }
        function toAddReconciliationCount(itemId,productName,surplusQuantity,usedQuantity){
            $('.addReconciliationTable tr:not(:first)').remove();
            $.getJSON(ctx + "/ec/product/getProductItem?id=" + itemId,function (item){
                $('#productName').text(productName + " ");
                var trHTML = "";
                trHTML += "<tr class='reconciliationItemTR'>";
                trHTML += '<td><span title="'+productName+'">'+abbr(productName,60)+'</span></td>';
                trHTML += '<td><span title="'+item.name+'">'+abbr(item.name,18)+'</span></td>';
                trHTML += '<td>'+item.retailPrice+'</td>';
                trHTML += '<td>'+item.purchasePrice+'</td>';
                trHTML += '<td>'+item.saleEarning+'</td>';
                trHTML += '<td>'+item.bonusEarning+'</td>';
                trHTML += '<td>'+item.firstBonusEarning+'</td>';
                trHTML += '<td>'+item.secondBonusEarning+'</td>';
                trHTML += '<td>'+item.point+'</td>';
                trHTML += '<td>'+item.couponValue+'</td>';
                trHTML += '<td>'+item.inventory+'</td>';
                trHTML += '<td>'+surplusQuantity+'</td>';
                trHTML += '<td>'+usedQuantity+'</td>';
                trHTML += '<td><input name="surplusQuantityArray" type="text" class="surplusQuantity input-min" title="' + productName + ' - ' + item.name + '">';
                trHTML += '<input name="itemIdArray" type="hidden" value="'+item.id+'"></td>';
                trHTML += "</tr>";
                $('.addReconciliationTable').append(trHTML);
            });

            showDialog('addReconciliationDialog');
        }
        function addReconciliation(){
            var hasError = false;
            $('.addReconciliationTable .surplusQuantity').each(function (){
                var input = $(this);
                if(isNull(input.val()) || !isNumber(input.val())){
                    alertx("提前结算数量格式不正确",function (){
                        input.focus();
                    });
                    hasError = true;
                    return false;
                }
            });

            if(hasError){
                return false;
            }

            $.getJSON(ctx + "/ec/supplier/addProductReconciliation?" + $('.addReconciliationTable :input').serialize(),function(){
                $("#searchForm").submit();
                alertx('保存成功！');
            });
        }
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="productReconciliation" action="${ctx}/ec/supplier/productReconciliation" method="post" class="form-search">
		<div class="panel panel-info">
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					查询条件
				</div>
			</div>
			<div class="panel-body">
				<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
				<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
                <label>分类：</label>
                <sys:treeselect id="category" name="productItem.product.category.id" value="${productReconciliation.productItem.product.category.id}" labelName="productItem.product.category.name" labelValue="${productReconciliation.productItem.product.category.name}"
                                title="栏目" url="/ec/category/treeData" module="product" notAllowSelectRoot="false" cssClass="input-small"/>
				<label>品牌：</label>
				<form:select id="brandId" path="productItem.product.brand.id" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${brandList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
				</form:select>
                <label>关键字：</label>
                <form:input path="sqlMap['keyWord']" htmlEscape="false" maxlength="50" class="input-large" placeholder="商品名称，规格名称"/>&nbsp;
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
                &nbsp;<input id="btnAdd" class="btn btn-primary" type="button" value="提前结算"/>
			</div>
		</div>
	</form:form>
	<sys:message content="${message}"/>
    <table id="contentTable" class="table table-bordered table-condensed table-hover">
        <thead>
        <tr>
            <th style="width: 60px;">分类</th>
            <th style="width: 100px;">品牌</th>
            <th>商品名称</th>
            <th style="width: 100px;">规格名称</th>
            <th style="width: 40px;">售价</th>
            <th style="width: 40px;">结算价</th>
            <th style="width: 40px;">佣金</th>
            <th style="width: 40px;">分红</th>
            <th style="width: 40px;">一级团队分红</th>
            <th style="width: 40px;">二级团队分红</th>
            <th style="width: 40px;">扣点</th>
            <th style="width: 40px;">可使用V猫币</th>
            <th style="width: 30px;">库存</th>
            <th style="width: 100px;">剩余提前结算数量</th>
            <th style="width: 100px;">已提前结算数量</th>
            <th style="width: 30px;">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="reconciliation">
            <tr>
                <td><a href="javascript:void(0)" onclick="$('#categoryId').val('${reconciliation.productItem.product.category.id}');$('#categoryName').val('${reconciliation.productItem.product.category.name}');$('#searchForm').submit();return false;">${reconciliation.productItem.product.category.name}</a></td>
                <td><a href="javascript:void(0)" onclick="$('#brandId').val('${reconciliation.productItem.product.brand.id}');$('#searchForm').submit();return false;">${fns:abbr(reconciliation.productItem.product.brand.name,80)}</a></td>
                <td title="${reconciliation.productItem.product.name}">${fns:abbr(reconciliation.productItem.product.name,40)}</td>
                <td>${reconciliation.productItem.name}</td>
                <td>${reconciliation.productItem.retailPrice}</td>
                <td>${reconciliation.productItem.purchasePrice}</td>
                <td>${reconciliation.productItem.saleEarning}</td>
                <td>${reconciliation.productItem.bonusEarning}</td>
                <td>${reconciliation.productItem.firstBonusEarning}</td>
                <td>${reconciliation.productItem.secondBonusEarning}</td>
                <td>${reconciliation.productItem.point}</td>
                <td>${reconciliation.productItem.couponValue}</td>
                <td>${reconciliation.productItem.inventory}</td>
                <td>${reconciliation.surplusQuantity}</td>
                <td>${reconciliation.usedQuantity}</td>
                <td>
                    <a href="javascript:void(0)" onclick="toAddReconciliationCount('${reconciliation.productItem.id}','${reconciliation.productItem.product.name}','${reconciliation.surplusQuantity}','${reconciliation.usedQuantity}')" title="增加提前结算数量">增加</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
	<div class="pagination">${page}</div>

    <div id="productItemDialog" class="vcat-dialog" style="width: 90%;">
        <div class="panel panel-info"><!-- 保存图片 -->
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    提前结算商品
                    <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog($(this).parents('.vcat-dialog').attr('id'))"></a>
                </div>
            </div>
            <div class="panel-body">
                <div>
                    <label>选择商品规格：</label>
                    <button onclick="selectItem()" class="btn">选择规格</button>
                </div>
                <table class="table table-bordered itemTable" style="margin-top: 5px;">
                    <tr>
                        <th>所属商品</th>
                        <th style="width: 100px;">所属规格</th>
                        <th style="width: 40px;">售价</th>
                        <th style="width: 40px;">结算价</th>
                        <th style="width: 40px;">佣金</th>
                        <th style="width: 40px;">分红</th>
                        <th style="width: 40px;">一级团队分红</th>
                        <th style="width: 40px;">二级团队分红</th>
                        <th style="width: 40px;">扣点</th>
                        <th style="width: 40px;">可使用V猫币</th>
                        <th style="width: 40px;">库存</th>
                        <th style="width: 40px;">提前结算数量</th>
                        <th style="width: 30px;">删除</th>
                    </tr>
                </table>
                <div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;">
                    <input onclick="reconciliation()" class="btn btn-success" type="button" value="保 存"/>
                    <input onclick="showDialog('productItemDialog')" class="btn" type="button" value="返 回"/>
                </div>
            </div>
        </div>
    </div>

    <div id="addReconciliationDialog" class="vcat-dialog" style="width: 90%;">
        <div class="panel panel-info"><!-- 保存图片 -->
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    <span id="productName"></span>增加提前结算数量
                    <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog($(this).parents('.vcat-dialog').attr('id'))"></a>
                </div>
            </div>
            <div class="panel-body">
                <table class="table table-bordered addReconciliationTable">
                    <tr>
                        <th>所属商品</th>
                        <th style="width: 100px;">所属规格</th>
                        <th style="width: 40px;">售价</th>
                        <th style="width: 40px;">结算价</th>
                        <th style="width: 40px;">佣金</th>
                        <th style="width: 40px;">分红</th>
                        <th style="width: 40px;">一级团队分红</th>
                        <th style="width: 40px;">二级团队分红</th>
                        <th style="width: 40px;">扣点</th>
                        <th style="width: 40px;">可使用V猫币</th>
                        <th style="width: 40px;">库存</th>
                        <th style="width: 100px;">剩余提前结算数量</th>
                        <th style="width: 100px;">已提前结算数量</th>
                        <th style="width: 40px;">增加数量</th>
                    </tr>
                </table>
                <div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;">
                    <input onclick="addReconciliation()" class="btn btn-success" type="button" value="保 存"/>
                    <input onclick="showDialog('addReconciliationDialog')" class="btn" type="button" value="返 回"/>
                </div>
            </div>
        </div>
    </div>
</body>
</html>