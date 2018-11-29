<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品管理</title>
	<meta name="decorator" content="default"/>
	<link href="${ctxStatic}/product/spec.css" type="text/css" rel="stylesheet" />
	<script type="text/javascript">include('product_spec','${ctxStatic}/product/',['spec.js?${version}']);</script>
	<script type="text/javascript">include('product_form','${ctxStatic}/product/',['product.js?${version}']);</script>
    <script type="text/javascript">
        $(function (){
            <c:if test="${isView}">
            $(':input').attr("readonly",true);
            </c:if>
            $('#propertyUl').sortable();
        });
    </script>
</head>
<body>
	<form:form id="inputForm" modelAttribute="product" action="${ctx}/ec/product/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
        <form:hidden path="couponAllUsable"/>
        <form:hidden path="couponPartUsable"/>
        <form:hidden path="reserveRecommend.startTime" id="reserveRecommendStartTime"></form:hidden>
		<sys:message content="${message}"/>
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    商品详情
                </div>
            </div>
            <div class="panel-body">
                <div class="control-group">
                    <label class="control-label">商品名称：</label>
                    <div class="controls">
                        <form:input path="name" maxlength="100" class="required input-xxlarge" placeholder="请输入商品名称" htmlEscape="false"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">所属分类：</label>
                    <div class="controls">
                        <sys:treeselect id="category" name="category.id" value="${product.category.id}" labelName="category.name" labelValue="${product.category.name}"
                             title="所属分类" url="/ec/category/treeData" cssClass="input-medium"/>
                        <shiro:hasPermission name="ec:category:add"><input type="button" class="btn btn-primary<c:if test="${isView}"> hide</c:if>" onclick="showAddCategory()" value="添加分类"></shiro:hasPermission>
                        <input type="hidden" id="oldCategoryId" name="sqlMap['oldCategoryId']" value="${product.category.id}">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">所属品牌：</label>
                    <div class="controls">
                        <form:select id="brand" path="brand.id" class="input-xxlarge">
                            <form:option value="" label="请选择品牌"/>
                            <form:options items="${brandList}" itemLabel="name" itemValue="id"/>
                        </form:select>
                        <shiro:hasPermission name="ec:brand:edit"><input type="button" class="btn btn-primary<c:if test="${isView}"> hide</c:if>" onclick="showAddSupplier()" value="添加品牌" style="margin-left: 5px;"></shiro:hasPermission>
                        <input type="hidden" id="oldBrandId" name="sqlMap['oldBrandId']" value="${product.brand.id}">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">所属配送方：</label>
                    <div class="controls">
                        <form:select id="distribution" path="distribution.id" class="input-xxlarge">
                            <form:option value="" label="请选择配送方"/>
                            <form:options items="${distributionList}" itemLabel="name" itemValue="id"/>
                        </form:select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">是否下架：</label>
                    <div class="controls">
                        <input type="hidden" name="oldArchived" value="${product.archived}">
                        <form:radiobuttons path="archived" items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" class="required" onclick="checkIsAutoLoad(this)" />
                        <span class="help-inline">如果为是，则标识该商品已下架</span>
                    </div>
                </div>
                <div class="control-group autoLoad<c:if test="${product.archived == '0'}"> hide</c:if>">
                    <label class="control-label">是否自动上架：</label>
                    <div class="controls">
                        <input type="hidden" name="sqlMap['oldAutoLoad']" value="${product.isAutoLoad}">
                        <form:radiobuttons path="isAutoLoad" items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" class="required" onclick="checkAutoLoad(this)" />
                    </div>
                </div>
                <div class="control-group autoLoadDate<c:if test="${product.isAutoLoad == '0'}"> hide</c:if>">
                    <label class="control-label">自动上架时间：</label>
                    <div class="controls">
                        <input type="hidden" name="sqlMap['oldAutoLoadDate']" value="${product.autoLoadDate}">
                        <input id="autoLoadDate" type="text" name="autoLoadDate" value="<fmt:formatDate value="${product.autoLoadDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" readonly="readonly" maxlength="20" class="input-date Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'});"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">是否爆品：</label>
                    <div class="controls">
                        <form:radiobuttons path="isHotSale" items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" class="required" onclick="checkIsHotSale(this)" />
                        <span class="help-inline">如果为是，则显示在APP【发现】-【爆品专区】中</span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">是否热销：</label>
                    <div class="controls">
                        <input type="hidden" name="hotRecommendId" value="${product.hotRecommendId}">
                        <form:radiobuttons path="isHot" items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" class="required" />
                        <span class="help-inline">表示该商品是否为热销，仅作为筛选项</span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">是否新品：</label>
                    <div class="controls">
                        <form:radiobuttons path="isNew" items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" class="required" />
                        <span class="help-inline">如果为是，则显示在APP【首页】-【找货源】-【开店必备货源中】</span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">是否可在普通市场购买：</label>
                    <div class="controls">
                        <form:radiobuttons path="retailUsable" items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" class="required"/>
                        <span class="help-inline">如果为是，则表示店主也可购买此商品，反之只有普通用户可购买</span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">是否支持退货：</label>
                    <div class="controls">
                        <form:radiobuttons path="canRefund" items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" class="required"/>
                        <span class="help-inline">如果为是，则该商品支持7天无理由退货，反之不支持退货</span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">是否为虚拟商品：</label>
                    <div class="controls">
                        <form:radiobuttons path="isVirtualProduct" items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" class="required" onchange="checkVirtual(this)"/>
                        <span class="help-inline">如果为是，则该商品订单不用发货，反之则需要发货</span>
                    </div>
                </div>
                <div class="control-group freeShipping<c:if test="${product.isVirtualProduct}"> hide</c:if>">
                    <label class="control-label">是否免邮费：</label>
                    <div class="controls">
                        <form:radiobuttons path="freeShipping" items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" class="required" onchange="checkFreeShipping(this)"/>
                        <span class="help-inline">如果为是，则该商品邮费为0</span>
                    </div>
                </div>
                <div class="control-group expressTemplate<c:if test="${product.freeShipping == 1}"> hide</c:if>">
                    <label class="control-label">邮费模板：</label>
                    <div class="controls">
                        <form:select id="expressTemplate" path="expressTemplate.id" class="input-large">
                            <form:option value="" label="请选择邮费模板"/>
                            <form:options items="${templateList}" itemLabel="name" itemValue="id"/>
                        </form:select>
                        <span class="help-inline">商品邮费根据用户选择的规格重量之和并使用邮费模板中的重量价格比进行计算所得</span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">排序：</label>
                    <div class="controls">
                        <form:input path="displayOrder" maxlength="10" class="required input-small" placeholder="请输入排序序号"/>
                        <span class="help-inline">数值越大越靠前</span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">市场价：</label>
                    <div class="controls">
                        <form:input path="externalPrice" maxlength="10" class="number input-small"/>
                        <span class="help-inline">页面展示效果【市场价：<del>100</del>】</span>
                    </div>
                </div>
                <div class="control-group${product.isNewRecord ? "" : " hide"}">
                    <label class="control-label">文案标题：</label>
                    <div class="controls">
                        <form:input path="sqlMap['copywriteTitle']" maxlength="100" cssClass="required input-xxlarge"/>
                    </div>
                </div>
                <div class="control-group${product.isNewRecord ? "" : " hide"}">
                    <label class="control-label">文案内容：</label>
                    <div class="controls">
                        <form:textarea path="sqlMap['copywriteContent']" rows="4" cssClass="required" cssStyle="width: 90%" />
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">商品图文详情：</label>
                    <div class="controls">
                        <form:textarea id="description" path="description" rows="4" class="input-xxlarge" />
                        <sys:kindeditor replace="description" height="300px" width="100%" />
                    </div>
                </div>
            </div>
        </div>
        <c:if test="${fn:length(product.propertyList) > 0 || null == product.id}">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    商品属性(拖拽属性标签可排序)
                    <span class="help-inline">仅作显示用</span>
                </div>
            </div>
            <div class="panel-body">
                <ul id="propertyUl" style="list-style: none;margin: 0 auto;">
                <c:forEach items="${product.propertyList}" var="pro" varStatus="status">
                    <li>
                    <div class="control-group property property_${pro.id}">
                        <label class="control-label propertyLabel">${pro.name}：</label>
                        <div class="controls">
                            <input type="hidden" name="propertyList[${status.index}].id" value="${pro.id}">
                            <input class="required input-large" type="text" name="propertyList[${status.index}].value" value="${pro.value}" maxlength="100">
                            <c:if test="${'true' eq pro.isCustom && !isView}"><span class="icon-remove" onclick="$('.property_${pro.id}').remove();" style="margin-left: 5px;"></span></c:if>
                        </div>
                    </div>
                    </li>
                </c:forEach>
                </ul>
                <div class="control-group newProductGroup<c:if test="${isView}"> hide</c:if>">
                    <label class="control-label">添加自定义商品属性：</label>
                    <div class="controls">
                        <sys:propertyList id="productProperty" callbackFunc="newProductProperty"></sys:propertyList>
                    </div>
                </div>
            </div>
        </div>
        </c:if>
        <%@include file="productSpec.jsp"%>

        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    商品图片
                </div>
            </div>
            <div class="panel-body">
                <sys:multiImageUpload id="productImages" name="productImages" sortable="true" size="5" value="${product.productImagesPath}" widthScale="1" heightScale="1"></sys:multiImageUpload>
                <span class="help-inline">推荐商品图片分辨率为500*500</span>
            </div>
        </div>

        <div class="panel panel-info hide">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    商品奖励
                </div>
            </div>
            <div class="panel-body">
                <h4>上架奖励</h4>
                <div class="control-group">
                    <label class="control-label">上架奖励金额：</label>
                    <div class="controls">
                        <input name="loadEarning.id" value="${product.loadEarning.id}" type="hidden">
                        <input name="loadEarning.oldFund" value="${product.loadEarning.fund}" type="hidden">
                        <input id="lef" name="loadEarning.fund" value="${product.loadEarning.fund}" placeholder="请输入奖励金额（元）" type="text" class="input-medium number">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">上架奖励获取额度：</label>
                    <div class="controls">
                        <input name="loadEarning.oldConvertFund" value="${product.loadEarning.convertFund}" type="hidden">
                        <input id="lecf" name="loadEarning.convertFund" value="${product.loadEarning.convertFund}" placeholder="请输入额度金额（元）" type="text" class="input-medium number">
                        <span class="help-inline">当小店内本商品的销售额达到此额度时，可获取上架奖励</span>
                    </div>
                </div>
            </div>
        </div>
		<div class="form-actions<c:if test="${isView}"> hide</c:if>">
			<shiro:hasAnyPermissions name="ec:product:edit,ec:product:add"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasAnyPermissions>
            <c:if test="${product != null && product.id != null && product.id ne ''}">
                <input id="btnQR" class="btn" type="button" value="查看二维码" onclick="showQRCode('${product.name}','${fns:getDictSingleValue('ec_seller_product_link_prefix','')}${product.id}')"/>
            </c:if>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="location.href=ctx+'/ec/product/list?useHisOrderParam=1&pageNo=${listOrder.page.pageNo}&pageSize=${listOrder.page.pageSize}'"/>
		</div>
	</form:form>
    <jsp:include page="../../common/qrCode.jsp"></jsp:include>
</body>
</html>