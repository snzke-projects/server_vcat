package com.vcat.module.json.order;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.persistence.Pager;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.dto.ProductItemDto;
import com.vcat.module.ec.entity.Refund;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Code.Ai on 16/5/18.
 * Description:
 */
public class GetSellerRefundOrderListResquest extends MsgEntity {
    private Pager page;
    public GetSellerRefundOrderListResquest(){
        super(ApiMsgConstants.SUCCESS_MSG,ApiMsgConstants.SUCCESS_CODE);
    }
    public void setPage(Pager page) {
        this.page = page;
    }
    public Pager getPage() {
        return page;
    }
    private List<Map<String,Object>> list = new ArrayList<>();

    public List<Map<String, Object>> getList() {
        return list;
    }
    public void setResultMap(List<ProductItemDto> productItemDtos){

        for(ProductItemDto item : productItemDtos){
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("productItemId",item.getProductItemId());
            resultMap.put("shopName",item.getShopName());
            resultMap.put("orderItemId",item.getOrderItemId());
            resultMap.put("productName",item.getProductName());
            resultMap.put("itemName",item.getItemName());
            resultMap.put("totalPrice",item.getItemPrice().multiply(new BigDecimal(item.getQuantity())));
            resultMap.put("itemPrice",item.getItemPrice()); //  数量(购买,不包括赠送的)乘以单价
            //resultMap.put("quantity",item.getQuantity() + item.getPromotionQuantity());  // 数量(赠送+购买的)
            resultMap.put("reQuantity",item.getReQuantity()); // 退货数量(默认全退)
            resultMap.put("amount",item.getAmount());   // 退款总金额
            resultMap.put("isGroupBuyProduct",item.getGroupBuyProduct());
            resultMap.put("returnStatus",item.getReturnStatus());
            resultMap.put("refundStatus",item.getReFundStatus());
            resultMap.put("refundId",item.getRefundId());
            resultMap.put("returnActivate",item.getReturnActivate());
            resultMap.put("productId",item.getProductId());
            resultMap.put("mainUrl", item.getMainUrl());
            String groupBuyStatus  = item.getGroupBuyStatus() == null ? "" :item.getGroupBuyStatus();
            resultMap.put("groupBuyStatus", groupBuyStatus);
            String allreFundStatus = item.getAllreFundStatus();
            resultMap.put("allreFundStatus", allreFundStatus);
            switch (allreFundStatus) {
                case "1":         // 用户退款未通过审核,状态还是为可退款,但是在退款单列表显示为退款关闭
                    resultMap.put("orderStatus", "退款已关闭");
                    resultMap.put("showDelButton", true);
                    break;
                case "2":         // 退款中
                    if (item.getGroupBuyProduct()) {          // 拼团订单
                        if (groupBuyStatus.equals("2")) {     // 拼团成功
                            resultMap.put("orderStatus", "拼团成功,退款中");
                        } else if (groupBuyStatus.equals("4")) {  //拼团失败
                            resultMap.put("orderStatus", "拼团失败,退款中");
                        }
                    } else {
                        resultMap.put("orderStatus", "退款中");
                    }
                    resultMap.put("showDelButton", false);
                    break;
                case "3":       // 退款完成
                    if (item.getGroupBuyProduct()) {
                        if (groupBuyStatus.equals("2")) {
                            resultMap.put("orderStatus", "拼团成功,退款已完成");
                        } else if (groupBuyStatus.equals("4")) {
                            resultMap.put("orderStatus", "拼团失败,退款已完成");
                        }
                    } else {
                        resultMap.put("orderStatus", "退款已完成");
                    }
                    resultMap.put("showDelButton", true);
                    break;
                case "4":    // 退款关闭
                    if (item.getGroupBuyProduct()) {
                        if (groupBuyStatus.equals("2")) {
                            resultMap.put("orderStatus", "拼团成功,退款已关闭");
                        }
                    } else {
                        resultMap.put("orderStatus", "退款已关闭");
                    }
                    resultMap.put("showDelButton", true);
                    break;
            }
            list.add(resultMap);
        }
    }
}
