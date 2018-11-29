<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>对账单</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
            $("#btnExport").click(function(){
                top.$.jBox.confirm("确认导出？","系统提示",function(v){
                    if(v=="ok"){
                        $("#searchForm").attr("action","${ctx}/ec/supplier/exportStatementOfAccount");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action","${ctx}/ec/supplier/statementOfAccount");
                    }
                },{buttonsFocus:1});
                top.$('.jbox-body .jbox-icon').css('top','55px');
            });
        });
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}
        function showDetail(id){
            top.$.jBox.open("iframe:${ctx}/ec/order/form?isView=true&id="+id, "订单详情",$(top.document).width()-220,$(top.document).height()-180,{
                buttons: {"确定": true},
                loaded: function () {
                    $(".jbox-content", top.document).css("overflow-y", "hidden");
                }
            });
        }
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="supplier" action="${ctx}/ec/supplier/statementOfAccount" method="post" class="form-search">
		<div class="panel panel-info">
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					查询条件
				</div>
			</div>
			<div class="panel-body">
				<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
				<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
				<label>供应商：</label>
				<form:select path="id" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${supplierList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
				</form:select>
                <label>品牌：</label>
                <form:select path="sqlMap['brandId']" class="input-small">
                    <form:option value="" label="全部"/>
                    <form:options items="${brandList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
                </form:select>
				<label>订单类型：</label>
				<form:select id="orderType" path="sqlMap['orderType']" class="input-small">
					<form:option value="" label="全部"/>
                    <c:forEach items="${fns:getDictList('ec_order_type')}" var="orderType">
                        <c:if test="${orderType.value ne '3'}">
                            <form:option value="${orderType.value}" label="${orderType.label}"/>
                        </c:if>
                    </c:forEach>
				</form:select>
                <label>关键字：</label>
                <form:input id="keyWord" path="sqlMap['keyWord']" htmlEscape="false" maxlength="50" class="input-xlarge" placeholder="订单号"/>&nbsp;
                <br>
                <div style="margin-top: 10px;">
                    <label>支付日期：</label>
                    <input id="pst" type="text" name="sqlMap['pst']" value="${supplier.sqlMap['pst']}" readonly="readonly" maxlength="10" class="Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" style="width:100px;"/>
                    <span>&nbsp;至&nbsp;</span>
                    <input id="pet" type="text" name="sqlMap['pet']" value="${supplier.sqlMap['pet']}" readonly="readonly" maxlength="10" class="Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" style="width:100px;"/>
                    <label>完成日期：</label>
                    <input id="sst" type="text" name="sqlMap['sst']" value="${supplier.sqlMap['sst']}" readonly="readonly" maxlength="10" class="Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" style="width:100px;"/>
                    <span>&nbsp;至&nbsp;</span>
                    <input id="set" type="text" name="sqlMap['set']" value="${supplier.sqlMap['set']}" readonly="readonly" maxlength="10" class="Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" style="width:100px;"/>
                    &nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
                    &nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
                </div>
			</div>
		</div>
	</form:form>
	<sys:message content="${message}"/>
    <div id="tableDiv" style="width: 100%;overflow-x: auto;">
        <table class="table table-hover table-bordered" style="width: 2500px">
            <thead>
            <tr>
                <th>订单类型</th>
                <th>供应商</th>
                <th>品牌</th>
                <th>订单号</th>
                <th>下单时间</th>
                <th>商品名称</th>
                <th>商品规格</th>
                <th>商品数量</th>
                <th>买家昵称</th>
                <th>买家手机号</th>
                <th>支付单号</th>
                <th>支付时间</th>
                <th>支付状态</th>
                <th>订单状态</th>
                <th>发货状态</th>
                <th>配送方式</th>
                <th>快递公司</th>
                <th>快递单号</th>
                <th title="订单快递费">快递费</th>
                <th title="订单总金额（佣金 + 分红 + 扣点 + 使用V猫币 + 结算费） - 退款金额">订单金额</th>
                <th title="规格销售佣金 * (购买数量 - 退货数量)">销售佣金</th>
                <th title="规格分红奖励 * (购买数量 - 退货数量)">销售分红</th>
                <th title="一级团队分红 * (购买数量 - 退货数量)">一级团队分红</th>
                <th title="二级团队分红 * (购买数量 - 退货数量)">二级团队分红</th>
                <th title="平台扣点金额 * (购买数量 - 退货数量)">平台扣点金额</th>
                <th title="使用V猫币 * (购买数量 - 退货数量)">使用V猫币</th>
                <th title="单项结算费 * (购买数量 - 退货数量)">结算费</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${page.list}" var="balance">
                <tr>
                    <td>${balance.orderType}</td>
                    <td title="${balance.supplierName}">${fns:abbr(balance.supplierName,20)}</td>
                    <td title="${balance.brandName}">${fns:abbr(balance.brandName,20)}</td>
                    <td><a href="javascript:void(0)" onclick="showDetail('${balance.orderId}')">${balance.orderNumber}</a></td>
                    <td>${balance.orderTime}</td>
                    <td><span title="${balance.productName}">${fns:abbr(balance.productName,20)}</span></td>
                    <td><span title="${balance.itemName}">${fns:abbr(balance.itemName,20)}</span></td>
                    <td>${balance.productCount}</td>
                    <td>${balance.buyerName}</td>
                    <td>${balance.buyerPhone}</td>
                    <td>${balance.paymentNumber}</td>
                    <td>${balance.paymentTime}</td>
                    <td>${balance.paymentStatus}</td>
                    <td>${balance.orderStatus}</td>
                    <td>${balance.shippingStatus}</td>
                    <td>${balance.deliveryMethod}</td>
                    <td>${balance.expressName}</td>
                    <td>${balance.shippingNumber}</td>
                    <td>${balance.freightPrice}</td>
                    <td>${balance.orderTotalPrice}</td>
                    <td>${balance.saleEarning}</td>
                    <td>${balance.bonusEarning}</td>
                    <td>${balance.firstBonusEarning}</td>
                    <td>${balance.secondBonusEarning}</td>
                    <td>${balance.point}</td>
                    <td>${balance.singleCoupon}</td>
                    <td>${balance.balance}</td>
                </tr>
            </c:forEach>
            <c:forEach items="${totalList}" var="total">
                <tr>
                    <td>${total.orderType}</td>
                    <td>${total.supplierName}</td>
                    <td>${total.brandName}</td>
                    <td>${total.orderNumber}</td>
                    <td>${total.orderTime}</td>
                    <td>${total.productName}</td>
                    <td>${total.itemName}</td>
                    <td>${total.productCount}</td>
                    <td>${total.buyerName}</td>
                    <td>${total.buyerPhone}</td>
                    <td>${total.paymentNumber}</td>
                    <td>${total.paymentTime}</td>
                    <td>${total.paymentStatus}</td>
                    <td>${total.orderStatus}</td>
                    <td>${total.shippingStatus}</td>
                    <td>${total.deliveryMethod}</td>
                    <td>${total.expressName}</td>
                    <td>${total.shippingNumber}</td>
                    <td>${total.freightPrice}</td>
                    <td>${total.orderTotalPrice}</td>
                    <td>${total.saleEarning}</td>
                    <td>${total.bonusEarning}</td>
                    <td>${total.firstBonusEarning}</td>
                    <td>${total.secondBonusEarning}</td>
                    <td>${total.point}</td>
                    <td>${total.singleCoupon}</td>
                    <td>${total.balance}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
	<div class="pagination">${page}</div>
</body>
</html>