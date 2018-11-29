function showRefundDetail(refundId,doAfterSucc){
    $.ajax({
        type : 'POST',
        url : ctx+"/ec/order/queryRefund",
        data : {
            id : refundId
        },
        success : function(refund){
            try{
                $('#refundDialog .panel-body span :not(.pull-right)').html("");
                $('#refundProductName').html(refund.product.name);
                $('#refundProductItemName').html(refund.orderItem.productItem.name);
                $('#refundApplyTime').html(refund.applyTime);
                $('#refundBuyerName').html(refund.customer.userName);
                $('#refundPhone').html(refund.phone);
                $('#refundOrderNumber').html(refund.orderItem.order.orderNumber);

                $('#refundShippingNumber').html(getDefault(refund.shippingNumber,"未填写"));
                if(!isNull(refund.shippingNumber)) {
                    $('#refundShippingNumber').unbind("click");
                    $('#refundShippingNumber').bind("click", function () {
                        if ("other" != refund.express.code) {
                            showExpressInfo(refund.express.code, refund.shippingNumber);
                        } else {
                            alertx('查询该物流详情失败！');
                        }
                    });
                }
                if(!isNull(refund.express)){
                    $('#returnExpressSpan').html(getDefault(refund.express.name,"未填写"));
                }else{
                    $('#returnExpressSpan').html("未填写");
                }

                $('#refundQuantity').html(refund.quantity + (isNull(refund.orderItem.promotionQuantity) ? 0 : refund.orderItem.promotionQuantity));

                $('#returnStatusSpan').html(refund.returnStatusLabel);
                $('#returnStatusSpan').css("color",refund.returnStatusColor);
                $('#refundAmount').html(refund.amount);
                $('#refundStatusSpan').html(refund.refundStatusLabel);
                $('#refundStatusSpan').css("color",refund.refundStatusColor);

                if(!isNull(refund.payment)){
                    if(!isNull(refund.payment.gateway)){
                        $('#returnGateway').html(getDefault(refund.payment.gateway.name,"未知方式"));
                    }else{
                        $('#returnGateway').html("未知方式");
                    }
                    $('#refundPaymentAmount').html(refund.payment.amount);
                    $('#refundTransNo').html(refund.payment.transactionNo);
                    $('#refundPaymentDate').html(new Date(refund.payment.paymentDate).pattern("yyyy-MM-dd HH:mm:ss"));
                }

                $('#isReceiptSpan').html(refund.isReceipt && refund.isReceipt == 1 ? '已收到货' : '未收到货');

                $('#returnReasonPre').html(refund.returnReason);
                $('.returnLog').remove();
                var logList = refund.logList;
                for(var i in logList){
                    var log = logList[i];
                    var trHTML = "<tr class='returnLog'>";
                    trHTML += "<td width='30%'>"+log.createDate+"</td>";
                    trHTML += "<td width='70%'>"+log.note+"</td>";
                    trHTML += "</tr>";
                    $('#returnNoteTable').append(trHTML);
                }

                if("function" == typeof(doAfterSucc)){
                    doAfterSucc.call(refund,refund);
                }
            }catch(e){
                console.log("获取退款信息失败："+e.message);
                return doError(e);
            }
        },
        error: function(XMLHttpRequest) {
            return doError(XMLHttpRequest);
        }
    });
}