<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>已选商品列表</title>
	<meta name="decorator" content="default"/>
    <script type="application/javascript">
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#selectedIds").val($("#selectedIds").val() + "&" + $(":checked").serialize());
            $("#searchForm").submit();
            return false;
        }
        function clearForm(){
            $('#categoryId').val("");
            $('#categoryName').val("");
            $('#supplierId').val("");
            $('#supplierId').select2();
            $('#brandId').val("");
            $('#brandId').select2();
            $('#name').val("");
            $('#isHotSale').val("");
            $('#isHotSale').select2();
            $('#isHot').val("");
            $('#isHot').select2();
            $('#isNew').val("");
            $('#isNew').select2();
            $('#archived').val("");
            $('#archived').select2();
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
                $.get(ctx + "/ec/topic/saveOrder?" + $('#orderForm').serialize(),function(){
                    top.$.jBox.tip("保存商品排序成功！");
                    $("#searchForm").submit();
                });
            }
        }
        function cancelSelected(productName,productId){
            confirmx('确认取消选择 '+productName+'？',function(){
                $.get(ctx + "/ec/topic/cancelSelect?sqlMap['topicId']="+$('#topicId').val()+"&id="+productId,function(){loading('取消选择完成！');$('#searchForm').submit()});
            });
            return false;
        }
        function batchCancelSelect(){
            if($('#contentTable :checked').length == 0 && $('#selectedIds').val().replace(/&/g,"").length == 0){
                top.$.jBox.tip("请选择商品！");
                return false;
            }
            var url = ctx + "/ec/topic/batchCancelSelect?topicId=" + $('#topicId').val();
            url += '&' + $('#contentTable :checked').serialize();
            url += '&' + $('#selectedIds').val();
            confirmx('确认批量取消选择商品？',function(){
                $.post(url,function(){loading('批量取消选择商品完成！');$('#searchForm').submit()});
                $("#selectedIds").val('');
            });
            return false;
        }
        function selectProduct(productName,productId){
            confirmx('确认选择 '+productName+'？',function(){
                $.get(ctx + "/ec/topic/selectProduct?sqlMap['topicId']="+$('#topicId').val()+"&id="+productId,function(){loading('选择完成！');$('#searchForm').submit()});
            });
            return false;
        }
        function batchSelectProduct(){
            if($('#contentTable :checked').length == 0 && $('#selectedIds').val().replace(/&/g,"").length == 0){
                top.$.jBox.tip("请选择商品！");
                return false;
            }
            var url = ctx + "/ec/topic/batchSelectProduct?topicId=" + $('#topicId').val();
            url += '&' + $('#contentTable :checked').serialize();
            url += '&' + $('#selectedIds').val();
            confirmx('确认批量选择商品？',function(){
                $.post(url,function(){loading('批量选择商品完成！');$('#searchForm').submit()});
                $("#selectedIds").val('');
            });
            return false;
        }
        function selectAllProduct(){
            confirmx('确认选择<span style="color: red">当前查询条件</span>下所有商品？',function(){
                $.post(ctx + "/ec/topic/selectAllProduct?"+$('#searchForm').serialize(),function(){loading('选择全部商品完成！');$('#searchForm').submit()});
            });
            return false;
        }
        function cancelAllSelect(){
            confirmx('确认<span style="color: red">取消选择全部</span>的商品？',function(){
                $.post(ctx + "/ec/topic/cancelAllSelect?topicId="+$('#topicId').val()+'&'+$('#contentTable :checked').serialize(),function(){loading('取消选择全部商品完成！');$('#searchForm').submit()});
            });
            return false;
        }
        function checkValue(value){
            $('#selectedIds').val($('#selectedIds').val().replace('selectProductId=' + value,''));
        }
    </script>
</head>
<body>
    <form:form id="searchForm" modelAttribute="product" action="${ctx}/ec/topic/selectedProductList" method="post" class="breadcrumb form-search" onsubmit="loading()">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <input id="topicId" name="sqlMap['topicId']" type="hidden" value="${product.sqlMap.topicId}"/>
        <input id="selectedIds" name="selectedIds" type="hidden" value="${selectedIds}">
        <label>分类：</label>
        <sys:treeselect id="category" name="category.id" value="${product.category.id}" labelName="category.name" labelValue="${product.category.name}"
                        title="栏目" url="/ec/category/treeData" module="product" notAllowSelectRoot="false" cssClass="input-small"/>
        <label>品牌：</label>
        <form:select id="brandId" path="brand.id" class="input-large">
            <form:option value="" label="全部"/>
            <form:options items="${brandList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
        </form:select>
        <label>商品名称：</label>
        <form:input path="name" htmlEscape="false" maxlength="50" class="input-large" placeholder="商品名称"/>
        <label>爆品：</label>
        <form:select id="isHotSale" path="sqlMap['isHotSale']" style="width:70px">
            <form:option value="" label="全部"/>
            <form:option value="1" label="是"/>
            <form:option value="0" label="否"/>
        </form:select>
        <label>热销：</label>
        <form:select id="isHot" path="sqlMap['isHot']" style="width:70px">
            <form:option value="" label="全部"/>
            <form:option value="1" label="是"/>
            <form:option value="0" label="否"/>
        </form:select>
        <label>新品：</label>
        <form:select id="isNew" path="sqlMap['isNew']" style="width:70px">
            <form:option value="" label="全部"/>
            <form:option value="1" label="是"/>
            <form:option value="0" label="否"/>
        </form:select>
        <br>
        <div style="margin-top: 5px;text-align: right;">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
            <input id="btnSaveOrder" class="btn btn-primary" type="button" value="保存排序" onclick="saveOrder()"/>
            <input id="btnBatchSelect" class="btn btn-primary" type="button" value="批量选择" onclick="batchSelectProduct()"/>
            <input id="btnSelectAll" class="btn btn-primary" type="button" value="选择全部" onclick="selectAllProduct()"/>
            <input id="btnBatchCancelSelect" class="btn btn-primary" type="button" value="批量取消选择" onclick="batchCancelSelect()"/>
            <input id="btnCancelAllSelect" class="btn btn-primary" type="button" value="全部取消选择" onclick="cancelAllSelect()"/>
            <input id="btnClear" class="btn btn-default" type="button" value="清空" onclick="clearForm()"/>
        </div>
    </form:form>
    <sys:message content="${message}"/>
    <form:form id="orderForm">
    <table id="contentTable" class="table table-bordered table-hover">
        <tr>
            <th style="width: 30px;"><a href="javascript:void(0)" onclick="$(':checkbox').attr('checked',true)" ondblclick="$(':checkbox').attr('checked',false)" title="全选/双击清空">选择</a></th>
            <th style="width: 80px;">分类</th>
            <th style="width: 120px;">品牌</th>
            <th style="max-width: 200px;">名称</th>
            <th style="width: 50px">销量</th>
            <th style="width: 100px;">售价</th>
            <th style="width: 100px;">佣金</th>
            <th style="width: 30px;">爆品</th>
            <th style="width: 30px;">热销</th>
            <th style="width: 30px;">新品</th>
            <th style="width: 30px;">上架</th>
            <th style="width: 30px;">专题商品排序</th>
            <th style="width: 60px;">操作</th>
        </tr>
        <c:forEach items="${page.list}" var="product">
            <tr>
                <td><input type="checkbox" name="selectProductId" value="${product.id}" <c:if test="${fn:indexOf(selectedIds,product.id) > -1}">checked</c:if> onclick="checkValue(this.value)"></td>
                <td>
                    <a href="javascript:" onclick="$('#categoryId').val('${product.category.id}');$('#categoryName').val('${product.category.name}');$('#searchForm').submit();return false;" title="${product.category.name}">${fns:abbr(product.category.name,12)}</a>
                </td>
                <td>
                    <a href="javascript:" onclick="$('#brandId').val('${product.brand.id}');$('#searchForm').submit();return false;" title="${product.brand.name}">${fns:abbr(product.brand.name,20)}</a>
                </td>
                <td><a href="javascript:void(0)" title="${product.name}" onclick="$(this).parents('tr').find(':checkbox').attr('checked',!$(this).parents('tr').find(':checkbox').attr('checked'))">${fns:abbr(product.name,100)}</a></td>
                <td>${product.saledNum}</td>
                <td>${product.price}</td>
                <td>${product.saleEarning}</td>
                <td style="color: ${product.isHotSale eq 0 ? 'cornflowerblue':'green'};">${product.isHotSale eq 0 ? '否':'是'}</td>
                <td style="color: ${product.isHot eq 0 ? 'cornflowerblue':'green'};">${product.isHot eq 0 ? '否':'是'}</td>
                <td style="color: ${product.isNew eq 0 ? 'cornflowerblue':'green'};">${product.isNew eq 0 ? '否':'是'}</td>
                <td style="color: ${product.archived eq 0 ? 'green':'red'};">${product.archived eq 0 ? '是':'否'}</td>
                <td>
                    <c:if test="${product.topic != null && product.topic.displayOrder != null && product.topic.displayOrder ne ''}">
                        <input type="text" name="displayOrders" value="${product.topic.displayOrder}" class="input-mini" style="margin:0;padding:0;text-align:center;"/>
                        <input type="hidden" name="refIds" value="${product.topic.remarks}"/>
                    </c:if>
                </td>
                <td>
                    <c:if test="${product.topic == null}">
                        <a href="javascript:void(0)" title="选择 ${product.name}" onclick="return selectProduct('${product.name}','${product.id}')">选择</a>
                    </c:if>
                    <c:if test="${product.topic != null && product.topic.displayOrder != null && product.topic.displayOrder ne ''}">
                        <a href="javascript:void(0)" title="取消选择 ${product.name}" onclick="return cancelSelected('${product.name}','${product.id}')">取消选择</a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>
    </form:form>
    <div class="pagination">${page}</div>
</body>
</html>