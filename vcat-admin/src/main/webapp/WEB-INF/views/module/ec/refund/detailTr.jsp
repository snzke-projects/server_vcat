<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script type="text/javascript">include('refund','${ctxStatic}/refund/',['refund.js']);</script>
<tr>
	<td width="15%"><span class="pull-right">退款买家名称：</span></td>
	<td width="35%"><span id="refundBuyerName"></span></td>
	<td width="15%"><span class="pull-right">退款联系电话：</span></td>
	<td width="35%"><span id="refundPhone"></span></td>
</tr>
<tr>
	<td><span class="pull-right">买家支付方式：</span></td>
	<td><span id="returnGateway"></span></td>
	<td><span class="pull-right">买家支付金额：</span></td>
	<td><span id="refundPaymentAmount"></span></td>
</tr>
<tr>
	<td><span class="pull-right">支付平台交易号：</span></td>
	<td><span id="refundTransNo"></span></td>
	<td><span class="pull-right">支付时间：</span></td>
	<td><span id="refundPaymentDate"></span></td>
</tr>
<tr>
	<td><span class="pull-right">退货商品：</span></td>
	<td><span id="refundProductName"></span></td>
	<td><span class="pull-right">退货商品规格：</span></td>
	<td><span id="refundProductItemName"></span></td>
</tr>
<tr>
	<td><span class="pull-right">退货数量：</span></td>
	<td><span id="refundQuantity"></span></td>
	<td><span class="pull-right">退货(款)申请时间：</span></td>
	<td><span id="refundApplyTime"></span></td>
</tr>
<tr>
	<td><span class="pull-right">所属订单：</span></td>
	<td><span id="refundOrderNumber"></span></td>
	<td><span class="pull-right">退货状态：</span></td>
	<td><span id="returnStatusSpan"></span></td>
</tr>
<tr>
	<td><span class="pull-right">退货物流公司：</span></td>
	<td><span id="returnExpressSpan"></span></td>
	<td><span class="pull-right">退货运单号：</span></td>
	<td><a id="refundShippingNumber" href="javascript:void(0);" class="link"></a></td>
</tr>
<tr>
	<td><span class="pull-right">退款金额：</span></td>
	<td><span id="refundAmount"></span></td>
	<td><span class="pull-right">退款状态：</span></td>
	<td><span id="refundStatusSpan"></span></td>
</tr>
<tr>
    <td><span class="pull-right">买家收货状态：</span></td>
    <td colspan="3"><span id="isReceiptSpan"></span></td>
</tr>
<tr>
	<td><span class="pull-right">收货人退货原因：</span></td>
    <td colspan="3"><pre id="returnReasonPre" style="font-size: 13px;"></pre></td>
</tr>
<tr>
	<td><span class="pull-right">退货审核说明：</span></td>
    <td colspan="3">
        <table id="returnNoteTable" class="table table-hover table-bordered"></table>
    </td>
</tr>