package com.vcat.module.json.order;

import com.vcat.api.service.OrderService;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.dto.RefundDto;
import com.vcat.module.ec.entity.OrderItem;
import com.vcat.module.ec.entity.Refund;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Code.Ai on 16/6/3.
 * Description:
 */
public class CheckRefundResponse extends MsgEntity {
    private Map<String, Object> refund = new HashMap<>();
    public void setExistRefund(OrderItem orderItem,OrderService orderService) {
        refund.put("orderNum",orderItem.getOrder().getOrderNumber());
        refund.put("orderItemId",orderItem.getId());
        refund.put("reason",orderItem.getRefund().getReturnReason());
        refund.put("productName",orderItem.getProductItem().getProduct().getName());
        refund.put("note","");
        refund.put("phoneNumber",orderItem.getShop().getCustomer().getPhoneNumber());
        // 收货状态 1. 未收货 2. 已收货 3. 不确定
        switch (orderItem.getOrder().getShippingStatus()) {
            case "0":  // 未发货
                refund.put("shippingStatus","1");
                break;
            case "2":  // 已收货
                refund.put("shippingStatus","2");
                break;
            default:
                refund.put("shippingStatus","3");
                break;
        }
        Map<String, Object> map             = orderService.getRefundCount(orderItem.getOrder().getId());
        long                 refundCount    = (long) map.get("refundCount");
        long                 orderItemCount = (long) map.get("orderItemCount");
        if((refundCount + 1 == orderItemCount) || orderItemCount == 1){ // 最后一个退款
            refund.put("amount",orderItem.getItemPrice().multiply(new BigDecimal(orderItem.getQuantity())).add(orderItem.getOrder().getFreightPrice())); // 订单项单价 * 数量 + 运费
        }else{ // 如果不是最后一个退款
            refund.put("amount",orderItem.getItemPrice().multiply(new BigDecimal(orderItem.getQuantity()))); // 订单项单价 * 数量
        }
    }
    public void setNewRefund(Map<String, Object> refund){
        this.refund = refund;
    }

    public Map<String, Object> getRefund() {
        return refund;
    }

    public CheckRefundResponse() {
        super(ApiMsgConstants.SUCCESS_MSG, ApiMsgConstants.SUCCESS_CODE);
    }
}
