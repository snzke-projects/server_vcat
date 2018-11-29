package com.vcat.module.json.order;

import com.alibaba.fastjson.JSONObject;
import com.vcat.api.service.ExpressApiService;
import com.vcat.api.service.GroupBuySponsorService;
import com.vcat.api.service.OrderService;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.utils.StringUtils;
import com.vcat.kuaidi100.pojo.NoticeRequest;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.dto.GroupBuySponsorDto;
import com.vcat.module.ec.dto.OrderDto;
import com.vcat.module.ec.dto.ProductItemDto;
import com.vcat.module.ec.entity.Refund;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by Code.Ai on 16/5/17.
 * Description:
 */
public class GetSellerOrderDetailResponse extends MsgEntity {
    private Map<String, Object> order = new HashMap<>();

    public Map<String, Object> getOrder() {
        return order;
    }

    public GetSellerOrderDetailResponse() {
        super(ApiMsgConstants.SUCCESS_MSG, ApiMsgConstants.SUCCESS_CODE);
    }

    public void setOrder(OrderDto orderDto,ExpressApiService expressApiService,OrderService orderService) {
        Map<String, Object> orderMap = new HashMap<>();
        // 共用的
        orderMap.put("id", orderDto.getId());                                       // 订单ID
        orderMap.put("orderNum", orderDto.getOrderNum());                           // 订单编号
        orderMap.put("orderType", orderDto.getOrderType());                         // 订单状态类型
        orderMap.put("paymentId", orderDto.getPaymentId());                         // 支付单Id
        orderMap.put("buyerId", orderDto.getBuyerId());                             // 买家Id
        orderMap.put("createDate", orderDto.getCreateDate());                       // 下单时间
        orderMap.put("totalProductNum", orderDto.getTotalProductNum());             // 商品总数
        orderMap.put("totalPrice", orderDto.getTotalPrice());                       // 实付款
        orderMap.put("freightPrice", orderDto.getFreightPrice());                   // 邮费
        orderMap.put("receiveUserName", orderDto.getReceiveUserName());             // 收货人
        orderMap.put("province", orderDto.getProvince());                           // 省
        orderMap.put("city", orderDto.getCity());                                   // 城市
        orderMap.put("district", orderDto.getDistrict());                           // 区域
        orderMap.put("receiveDetailAddress", orderDto.getReceiveDetailAddress());   // 详细收货地址
        orderMap.put("groupBuySponsorId", orderDto.getGroupBuySponsorId());         // 拼团Id
        orderMap.put("receivePhoneNum", orderDto.getReceivePhoneNum());             // 收货人电话
        orderMap.put("invoice", orderDto.getInvoice());                             // 发票
        orderMap.put("payWay", orderDto.getPayWay());                               // 支付方式
        orderMap.put("orderType", orderDto.getOrderType());                         // 返回的orderType
        orderMap.put("reFundcount",orderDto.getReFundcount());
        // 根据不同的状态项返回的结果中添加数据
        switch (orderDto.getOrderType()) {
            case "2":       // 待支付详情
                // 添加自动取消时间
                orderMap.put("autoCancelDate", orderDto.getAutoCancelDate()); // 取消订单时间
                if(new Date().getTime() >= orderDto.getAutoCancelDate().getTime()){
                    orderMap.put("orderStatus","交易关闭");
                    // 只显示 删除订单按钮
                }else{
                    orderMap.put("orderStatus","等待支付");
                    // 显示取消订单 和 立即支付
                }
                break;
            case "3":       // 待发货详情
                if(orderDto.getGroupBuySponsorId() == null){                            // 不是团购商品
                    orderMap.put("orderStatus", "卖家待发货");
                }
                else{  // 是团购商品
                    ProductItemDto         item         = orderDto.getProductItems().get(0);
                    int type = orderService.returnGroupBuyStatus(orderDto);
                    orderMap.put("groupBuyStatus", type);

                    if(orderDto.getGroupBuyStatus() == 1){                              // 团购进行中
                        orderMap.put("groupBuyStatus", 1);
                        orderMap.put("orderStatus", "等待好友参团");
                    }
                    else if(orderDto.getGroupBuyStatus() == 2){                         // 团购成功
                        orderMap.put("groupBuyStatus", 2);
                        orderMap.put("orderStatus", "拼团成功,待发货");
                    }
                    else if(orderDto.getGroupBuyStatus() == 4 && item.getAllreFundStatus().equals("2")){
                        orderMap.put("groupBuyStatus", 4);
                        orderMap.put("orderStatus", "拼团失败,退款中");
                    }
                }
                break;

            case "4":       // 待收货详情
                orderMap.put("orderStatus", "卖家已发货");
                long autoReceiptDate = orderDto.getAutoReceiptDate().getTime();
                if(orderDto.getExtended()){
                    orderMap.put("overtimeStatus", 1); // 已经延迟收货1次
                    autoReceiptDate +=  3 * 24 * 60 * 60 * 1000;
                }
                else if(new Date(orderDto.getAutoReceiptDate().getTime() -  3 * 24 * 60 * 60 * 1000).compareTo(new Date()) == 1){
                    orderMap.put("overtimeStatus", 2); // 离自动确认收货时间大于3天
                }else{
                    orderMap.put("overtimeStatus", 3); // 可以延迟收货
                }
                orderMap.put("autoReceiptDate", new Date(autoReceiptDate)); // 自动确认增加收货时间
                // 获取最新物流信息
                String deliveryNum = orderDto.getDeliveryNum();
                String expressCode = orderDto.getExpressCode();
                Map<String, Object> expressInfo = new HashMap<>();
                NoticeRequest requst;
                String        date   = expressApiService.query(expressCode, deliveryNum);
                if (date != null) {
                    requst = JSONObject.parseObject(date, NoticeRequest.class);
                    if (requst == null) {
                        requst = new NoticeRequest();
                    }
                    expressInfo.put("context", requst.getLastResult() == null
                            || requst.getLastResult().getData() == null
                            || requst.getLastResult().getData().isEmpty() ? "暂时没有物流信息！" : requst.getLastResult().getData().get(0)
                            .getContext());
                    expressInfo.put("time", requst.getLastResult() == null
                            || requst.getLastResult().getData() == null
                            || requst.getLastResult().getData().isEmpty() ? "" : requst.getLastResult().getData().get(0).getTime());
                }else{
                    expressInfo.put("context","暂时没有物流信息！");
                    expressInfo.put("time","");
                }
                orderMap.put("expressInfo", expressInfo);
                orderMap.put("isReviewed", orderDto.getReviewed());
                orderMap.put("deliveryNum", orderDto.getDeliveryNum());                     // 物流单号
                orderMap.put("expressCode", orderDto.getExpressCode());                     // 物流编号
                break;
            case "5":       // 待评价详情
                orderMap.put("orderStatus", "交易完成");
                orderMap.put("deliveryNum", orderDto.getDeliveryNum());                     // 物流单号
                orderMap.put("expressCode", orderDto.getExpressCode());                     // 物流编号
                orderMap.put("isReviewed",orderDto.getReviewed());
                break;
            case "7":
                orderMap.put("orderStatus", "交易关闭");
            default:
                orderMap.put("orderStatus", "交易关闭");
                orderMap.put("orderType", "7");
        }


        List<Map<String,Object>> itemMapList = new ArrayList<>();
        List<ProductItemDto> ps = orderDto.getProductItems();
        for(ProductItemDto item : ps){
            Map<String,Object> itemMap = new HashMap<>();
            itemMap.put("productItemId",item.getProductItemId());
            itemMap.put("groupBuyId",item.getGroupBuyId());
            itemMap.put("shopName",item.getShopName());
            itemMap.put("orderItemId",item.getOrderItemId());
            itemMap.put("productName",item.getProductName());
            itemMap.put("itemName",item.getItemName());
            itemMap.put("itemPrice",item.getItemPrice());
            itemMap.put("groupBuyProduct",item.getGroupBuyProduct());
            itemMap.put("refundId",item.getRefundId());
            itemMap.put("productId",item.getProductId());
            itemMap.put("mainUrl", item.getMainUrl());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("productType",item.getProductType());


            int allreFundStatus = Integer.parseInt(item.getAllreFundStatus());
            if((allreFundStatus == 1 && !orderMap.get("orderStatus").equals("等待好友参团"))){
                itemMap.put("refundStatus",1); // 显示退款按钮
            }else if(allreFundStatus == 2){
                itemMap.put("refundStatus",2); // 显示退款中按钮
            }else if(allreFundStatus == 4){
                itemMap.put("refundStatus",3); // 显示退款已关闭按钮
            }else if (allreFundStatus == 3){
                itemMap.put("refundStatus",4); // 显示退款完成

            }
            // 1. 可退款; 2: 退款中; 3:退款完成; 4:未知错误
            //itemMap.put("allreFundStatus",Integer.parseInt(item.getAllreFundStatus()));
            itemMapList.add(itemMap);
        }

        // 重构数据结构
        List<Map<String, Object>> productItems   = new ArrayList<>();
        int                       size           = orderDto.getProductItems().size();
        Map<String, Object>       productItemMap = new HashMap<>();
        List<Map<String, Object>>      itemList       = new ArrayList<>();
        Map<String, Object>  nextProductItemMap = null;
        List<Map<String, Object>> nextItemList       = null;
        // 先创建一个新节点
        productItemMap.put("shopId", orderDto.getProductItems().get(0).getShopId());
        productItemMap.put("shopName", orderDto.getProductItems().get(0).getShopName());
        itemList.add(itemMapList.get(0));
        productItemMap.put("itemList", itemList);
        productItems.add(productItemMap);
        orderMap.put("productItems", productItems);
        boolean flg   = true;
        int     index = 0;
        do {
            index++;
            if (index == size) {
                break;
            }
            String shopName       = orderDto.getProductItems().get(index - 1).getShopName();
            String shopId       = orderDto.getProductItems().get(index - 1).getShopId();
            String nextShopId   = orderDto.getProductItems().get(index).getShopId();
            String nextShopName = orderDto.getProductItems().get(index).getShopName();
            if (!shopName.equals(nextShopName)) {
                // 如果不同 则创建一个新节点
                nextProductItemMap = new HashMap<>();
                nextItemList = new ArrayList<>();
                nextProductItemMap.put("shopId", nextShopId);
                nextProductItemMap.put("shopName", nextShopName);
                nextItemList.add(itemMapList.get(index));
                nextProductItemMap.put("itemList", nextItemList);
                productItems.add(nextProductItemMap);
                flg = false;
            } else  {
                if (flg) {
                    itemList.add(itemMapList.get(index));
                    productItemMap.put("itemList", itemList);
                } else {
                    nextItemList.add(itemMapList.get(index));
                    nextProductItemMap.put("itemList", nextItemList);
                }
            }
        } while (index != size - 1);
        order = orderMap;
        //order = orderService.refactorOrderItem(orderMap,orderDto,"detail");
    }
}
