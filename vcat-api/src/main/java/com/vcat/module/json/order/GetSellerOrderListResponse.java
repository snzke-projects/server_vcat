package com.vcat.module.json.order;

import com.vcat.api.service.GroupBuySponsorService;
import com.vcat.api.service.OrderService;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.persistence.Pager;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.dto.GroupBuySponsorDto;
import com.vcat.module.ec.dto.OrderDto;
import com.vcat.module.ec.dto.ProductItemDto;
import com.vcat.module.ec.entity.Refund;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Code.Ai on 16/5/16.
 * Description
 */
public class GetSellerOrderListResponse extends MsgEntity {
    private Pager page;
    private List<Map<String, Object>> list = new ArrayList<>();

    public GetSellerOrderListResponse() {
        super(ApiMsgConstants.SUCCESS_MSG, ApiMsgConstants.SUCCESS_CODE);
    }

    public void setPage(Pager page) {
        this.page = page;
    }

    public Pager getPage() {
        return page;
    }

    public List<Map<String, Object>> getlist() {
        return list;
    }

    public void setOrderDtolist(List<OrderDto> orderDtolist, String orderType, OrderService orderService) {
        for (OrderDto orderDto : orderDtolist) {
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
            orderMap.put("receivePhoneNum", orderDto.getReceivePhoneNum());             // 收货人电话
            orderMap.put("groupBuySponsorId", orderDto.getGroupBuySponsorId());         // 拼团Id
            orderMap.put("orderType", orderDto.getOrderType());                         // 返回的orderType
            orderMap.put("groupBuyId", orderDto.getGroupBuyId());                       //
            if (orderDto.getGroupBuySponsorId() != null) {
                orderMap.put("productType", orderDto.getProductItems().get(0).getProductType());
                orderMap.put("allreFundStatus",orderDto.getProductItems().get(0).getAllreFundStatus());
            }
            switch (orderType) {
                case "1":                                                 // 全部订单
                    String returnOrderType = orderDto.getOrderType();
                    switch (returnOrderType) {
                        case "2":                                         // 待支付
                            orderMap.put("orderStatus", "待支付");
                            break;

                        case "3":                                   // 待发货
                            if (StringUtils.isNullBlank(orderDto.getGroupBuySponsorId())) {                            // 不是团购商品
                                orderMap.put("orderStatus", "卖家待发货");
                            } else {// 是团购商品
                                ProductItemDto item = orderDto.getProductItems().get(0);
                                orderMap.put("allreFundStatus",item.getAllreFundStatus());
                                int type = orderService.returnGroupBuyStatus(orderDto);
                                orderMap.put("groupBuyStatus", type);
                                // 发货状态显示
                                if (orderDto.getGroupBuyStatus() == 1) {                              // 团购进行中
                                    orderMap.put("groupBuyStatus", 1);
                                    orderMap.put("orderStatus", "等待好友参团");
                                } else if (orderDto.getGroupBuyStatus() == 2) {                         // 团购成功
                                    orderMap.put("groupBuyStatus", 2);
                                    orderMap.put("orderStatus", "拼团成功,待发货");
                                } else if (orderDto.getGroupBuyStatus() == 4 && item.getAllreFundStatus().equals("2")) {
                                    orderMap.put("groupBuyStatus", 4);
                                    orderMap.put("orderStatus", "拼团失败,退款中");
                                }
                            }
                            break;

                        case "4":                                   // 待收货
                            orderMap.put("orderStatus", "待收货");
                            // 点击确认收货时状态判断 如果大于0 则弹框提示 等于0,直接调用接口
                            orderMap.put("reFundcount", orderDto.getReFundcount());
                            orderMap.put("deliveryNum", orderDto.getDeliveryNum());                     // 物流单号
                            orderMap.put("expressCode", orderDto.getExpressCode());                     // 物流编号
                            break;

                        case "5":                                   // 待评价
                            int index = 1;
                            for (ProductItemDto productItem : orderDto.getProductItems()) {
                                if (!productItem.getAllreFundStatus().equals("3")) {
                                    break;
                                } else
                                    index++;
                            }
                            if (index == orderDto.getProductItems().size() || orderDto.getReFundcount() == 0) {
                                orderMap.put("orderStatus", "交易成功");
                            } else
                                orderMap.put("orderStatus", "交易关闭");
                            // 判断是显示评论还是查看评论
                            orderMap.put("isReviewed", orderDto.getReviewed());                         // 是否评论
                            orderMap.put("deliveryNum", orderDto.getDeliveryNum());                     // 物流单号
                            orderMap.put("expressCode", orderDto.getExpressCode());                     // 物流编号
                            // 如果评论过 --> 显示删除订单
                            break;

                        case "7":  // 交易关闭
                            orderMap.put("orderStatus", "交易关闭"); // 未支付就时取消订单
                            // 删除订单按钮
                            break;
                        default:
                            orderMap.put("orderStatus", "交易关闭");
                            orderMap.put("orderType", "7");
                    }
                    break;

                case "2":                                          // 待发货
                    orderMap.put("orderStatus", "待支付");
                    break;

                case "3":                                          // 待发货
                    if (StringUtils.isNullBlank(orderDto.getGroupBuySponsorId())) {            // 不是团购商品
                        orderMap.put("orderStatus", "卖家待发货");
                    } else {                                                                   // 是团购商品
                        // 根据返回的 type 跳转到不同的拼团状态页
                        int type = orderService.returnGroupBuyStatus(orderDto);
                        orderMap.put("groupBuyStatus", type);
                        ProductItemDto item = orderDto.getProductItems().get(0);
                        if (orderDto.getGroupBuyStatus() == 1) {                              // 团购进行中
                            orderMap.put("groupBuyStatus", 1);
                            orderMap.put("orderStatus", "等待好友参团");
                        } else if (orderDto.getGroupBuyStatus() == 2) {                         // 团购成功
                            orderMap.put("groupBuyStatus", 2);
                            orderMap.put("orderStatus", "拼团成功,待发货");
                        } else if (orderDto.getGroupBuyStatus() == 4 && item.getAllreFundStatus().equals("2")) {
                            orderMap.put("groupBuyStatus", 4);
                            orderMap.put("orderStatus", "拼团失败,退款中");
                        }
                    }
                    break;

                case "4":
                    orderMap.put("orderStatus", "待收货");
                    // 点击确认收货时状态判断 如果大于0 则弹框提示 等于0,直接调用接口
                    orderMap.put("reFundcount", orderDto.getReFundcount());
                    orderMap.put("deliveryNum", orderDto.getDeliveryNum());                     // 物流单号
                    orderMap.put("expressCode", orderDto.getExpressCode());                     // 物流编号
                    break;

                case "5":
                    // 物流信息
                    orderMap.put("orderStatus", "交易完成");
                    orderMap.put("deliveryNum", orderDto.getDeliveryNum());                     // 物流单号
                    orderMap.put("expressCode", orderDto.getExpressCode());                     // 物流编号
                    orderMap.put("isReviewed", orderDto.getReviewed());                         // 是否评论
                    // 评价入口
                    break;
            }
            list.add(orderService.refactorOrderItem(orderMap, orderDto, "list"));
        }
    }
}
