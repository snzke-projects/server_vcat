package com.vcat.api.web.v2;

import com.alibaba.fastjson.JSONObject;
import com.vcat.api.exception.ApiException;
import com.vcat.api.service.*;
import com.vcat.api.web.validation.ValidateParams;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.express.ExpressUtils;
import com.vcat.common.lock.DistLockHelper;
import com.vcat.common.persistence.Pager;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.utils.ThreadPoolUtil;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import com.vcat.kuaidi100.pojo.TaskResponse;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.dto.*;
import com.vcat.module.ec.entity.*;
import com.vcat.module.json.order.*;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.redisson.core.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Code.Ai on 16/4/12.
 * Description:
 */
@RestController
@ApiVersion({1,2})
public class OrderController2 extends RestBaseController {
    @Autowired
    private OrderService      orderService;
    @Autowired
    private RefundService     refundService;
    @Autowired
    private ExpressApiService expressApiService;
    @Autowired
    private ExpressService    expressService;
    @Autowired
    private RefundLogService  refundLogService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private GroupBuySponsorService groupBuySponsorService;
    @Autowired
    private ReviewDetailService reviewDetailService;
    @Autowired
    private RatingSummaryService ratingSummaryService;
    @Autowired
    private AddressService addressService;

    /************************************************** 2016.05.16 订单重构 *******************************************************/
    /**
     * 2016.05.16
     * 获取订单信息数量，orderType为订单类型
     * 1 全部 2待支付 3待发货 4待收货 5已完成 6退款
     * @param token
     * @return
     */
    @ValidateParams
    //@RequiresRoles(value={"seller","buyer"}, logical = Logical.OR)
    @RequestMapping(value = "/api/getMyOrderCount",method = RequestMethod.POST)
    @ResponseBody
    public Object getMyOrderCount(
            @RequestHeader(value = "token", defaultValue = "") String token){
        String              buyerId = StringUtils.getCustomerIdByToken(token);
        Map<String, Object> map      = new HashMap<>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        map.put("result",orderService.getMyOrderCount(buyerId));
        return map;
    }


    /**
     * 获取订单列表
     * @param token
     * @param params
     * @return
     */
    @ValidateParams
    //@RequiresRoles(value={"seller","buyer"},logical = Logical.OR)
    @RequestMapping(value = "/api/getSellerOrderList", method = RequestMethod.POST)
    @ResponseBody
    public Object getSellerOrderList(@RequestHeader(value = "token", defaultValue = "") String token,
                                     @Valid @RequestBody GetSellerOrderListRequest params){
        String orderType = params.getOrderType();
        int pageNo  = params.getPageNo();
        String condition = params.getCondition();
        String buyerId = StringUtils.getCustomerIdByToken(token);
        if(StringUtils.isBlank(orderType)){
            orderType = "1";
        }
        int count = orderService.countBySellerId(buyerId,orderType,condition);
        // 组装分页信息
        Pager page = new Pager();
        page.setPageNo(pageNo);
        page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
        page.setRowCount(count);
        page.doPage();

        List<OrderDto> list = orderService.getSellerOrderList(buyerId,page,orderType,condition);
        for(OrderDto orderDto : list){
            List<ProductItemDto> productItemList = orderService.getOrderItemsInfo(orderDto.getId());
            orderDto.getProductItems().addAll(productItemList);
        }

        GetSellerOrderListResponse result = new GetSellerOrderListResponse();
        result.setOrderDtolist(list,orderType,orderService);
        result.setPage(page);
        return result;
    }

    /**
     * 获取订单详情
     * @param token
     * @param
     * @param
     * @return
     */
    @ValidateParams
    //@RequiresRoles(value={"seller","buyer"},logical = Logical.OR)
    @ResponseBody
    @RequestMapping(value = "/api/getSellerOrderDetail",method = RequestMethod.POST)
    public Object getSellerOrderDetail(
            @RequestHeader(value = "token", defaultValue = "") String token,
            @Valid @RequestBody GetSellerOrderDetailRequest params) {
        OrderDto order = new OrderDto();
        order.setId(params.getOrderId());
        order = orderService.getSellerOrderDetail(order,StringUtils.getCustomerIdByToken(token));
        if(order == null){
            return new MsgEntity(ApiMsgConstants.FAILED_MSG, ApiMsgConstants.FAILED_CODE);
        }
        order.getProductItems().addAll(orderService.getOrderItemsInfo(order.getId()));
        GetSellerOrderDetailResponse result = new GetSellerOrderDetailResponse();
        result.setOrder(order,expressApiService,orderService);
        return result;
    }

    /**
     * 取消订单接口
     * @param token
     * @param
     * @return
     * 如果是团购订单 还原库存,更新锁定状态
     */
    @ValidateParams
    @ResponseBody
    //@RequiresRoles(value={"seller","buyer"},logical = Logical.OR)
    @RequestMapping(value = "/api/sellerCancelOrder", method = RequestMethod.POST)
    public Object sellerCancelOrder(@RequestHeader(value = "token", defaultValue = "") String token,
                                    @Valid @RequestBody SellerCancelOrderRequest params) {
        String buyerId = StringUtils.getCustomerIdByToken(token);
        String orderId = params.getOrderId();
        int    type    = params.getType();
        orderService.cancelOrder(orderId, buyerId, type);
        return new MsgEntity(ApiMsgConstants.CANCEL_ORDER,
                ApiMsgConstants.SUCCESS_CODE);
    }

    /**
     * 删除订单接口
     * @param token
     * @param
     * @return
     */
    @ValidateParams
    @ResponseBody
    //@RequiresRoles(value={"seller","buyer"},logical = Logical.OR)
    @RequestMapping(value = "/api/sellerDelOrder", method = RequestMethod.POST)
    public Object sellerDelOrder(@RequestHeader(value = "token", defaultValue = "") String token,
                                 @Valid @RequestBody SellerDelOrderRequest params){
        String buyerId = StringUtils.getCustomerIdByToken(token);
        String orderId = params.getOrderId();
        String type = params.getType();
        //1. 状态为交易成功，自动确认收货七天(已结算)，且无退款流程在进行的订单，显示“删除订单”按钮
        //2. 状态为交易关闭的订单，显示“删除订单”按钮
        //OrderDto order = new OrderDto();
        //order.setId(params.getOrderId());
        //order = orderService.getSellerOrderDetail(order,buyerId);
        if(!StringUtils.isBlank(type) && type.equals("seller")){
            orderService.sellerDelete(orderId);
        }else{
            orderService.buyerDelete(orderId);
        }
        return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
                ApiMsgConstants.SUCCESS_CODE);
    }

    /**
     * 卖家延迟收货
     * @param token
     * @param
     * @return
     */
    @ValidateParams
    @ResponseBody
    //@RequiresRoles("seller")
    @RequestMapping(value = "/api/extendReceiptDate", method = RequestMethod.POST)
    public Object extendReceiptDate(@RequestHeader(value = "token", defaultValue = "") String token,
                                    @Valid @RequestBody ExtendReceiptDateRequest params){
        String orderId = params.getOrderId();
        OrderDto order = new OrderDto();
        order.setId(params.getOrderId());
        order = orderService.getSellerOrderDetail(order,StringUtils.getCustomerIdByToken(token));
        if(order == null){
            return new MsgEntity(ApiMsgConstants.FAILED_MSG, ApiMsgConstants.FAILED_CODE);
        }
        orderService.extendReceiptDate(orderId);
        return new MsgEntity("延迟收货成功",
                ApiMsgConstants.SUCCESS_CODE);
    }

    /**
     * 卖家确认收货接口 增加卖家收入(返佣+分红)
     * 更新订单收货状态,记录日志
     * 如果有为正在退款中,关闭退款
     * @param token
     * @param
     * @return
     */
    @ValidateParams
    @ResponseBody
    //@RequiresRoles(value = {"seller","buyer"},logical = Logical.OR)
    @RequestMapping(value ="/api/sellerCheckShipping" , method = RequestMethod.POST)
    public Object sellerCheckShipping(@RequestHeader(value = "token", defaultValue = "") String token,
                                      @Valid @RequestBody SellerCheckShippingRequest params){
        String orderId = params.getOrderId();
        String buyerId = StringUtils.getCustomerIdByToken(token);
        Map<String,Object> resultMap  = new HashMap<>();
        try{
            resultMap =  orderService.sellerCheckShipping(buyerId,orderId);
        }catch(ApiException e){
            logger.error(e.getMessage(),e);
            resultMap.put(ApiConstants.CODE, ApiMsgConstants.FAILED_CODE);
            resultMap.put(ApiConstants.MSG,e.getMessage());
        }
        return resultMap;
    }


    /**
     * 合单支付/立即支付
     * @param token
     * @param params
     * @return
     */
    @ValidateParams
    @ResponseBody
    //@RequiresRoles("seller")
    @RequestMapping(value ="/api/checkPay" , method = RequestMethod.POST)
    public Object checkPay(@RequestHeader(value = "token", defaultValue = "") String token,
                           @Valid @RequestBody OrderIdListRequest params){
        List<MergePayRequest> orderIdList = params.getOrderIdList();
        if(orderIdList == null || orderIdList.size() == 0){
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
        }
        String customerId = StringUtils.getCustomerIdByToken(token);
        MergePayResponse result = new MergePayResponse();
        OrderDto order =  new OrderDto();
        boolean tag = false;
        for(MergePayRequest o : orderIdList){
            String orderId = o.getOrderId();
            order.setId(orderId);
            order = orderService.getSellerOrderDetail(order,customerId);
            if(order == null){
                return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
            }
            // 商品是否已下架
            List<ProductItemDto> itemList = orderService.getOrderItemsInfo(order.getId());
            for(ProductItemDto item : itemList){
                if(item.getArchived()){
                    return new MsgEntity(ApiMsgConstants.OUT_OF_STOCK,ApiMsgConstants.FAILED_CODE);
                }
            }
            if(order.getGroupBuySponsorId() != null){ // 团购商品
                GroupBuySponsorDto groupBuySponsor = new GroupBuySponsorDto();
                groupBuySponsor.setId(order.getGroupBuySponsorId());
                groupBuySponsor = groupBuySponsorService.getGroupBuySponsor(groupBuySponsor);
                Date groupBuyEndDate = groupBuySponsor.getEndDate();
                if((new Date()).getTime() >= groupBuyEndDate.getTime() || order.getAutoCancelDate().compareTo(new Date()) != 1|| groupBuySponsor.getStatus() == 4 || groupBuySponsor.getStatus() == 3){
                    return new MsgEntity(ApiMsgConstants.OVERTIME, ApiMsgConstants.FAILED_CODE);
                }
                tag = true;
            }
            if(order.getAutoCancelDate().compareTo(new Date()) != 1 || order.getOrderType().equals("7")){
                // 订单因超时自动取消，无法支付！
                return new MsgEntity(ApiMsgConstants.AUTO_CANEL,ApiMsgConstants.FAILED_CODE);
            }
        }
        String paymentId = "";
        if(orderIdList.size() == 1){
            // 如果是立即支付 则不重新生成支付单
            paymentId = order.getPaymentId();
            result.setOrderId(orderIdList.get(0).getOrderId());
        }else if(orderIdList.size() > 1 && tag){
            return new MsgEntity(ApiMsgConstants.MERGE_ERROR,ApiMsgConstants.FAILED_CODE);
        }else if(orderIdList.size() > 1 && !tag){
            // 合单支付 重新生成支付单
            paymentId = orderService.againCreatePayment(orderIdList).getId();
        }
        result.setPaymentId(paymentId);
        return result;
    }


    /**
     * 客户端获取店主开过的团购列表
     * @param token
     * @param params
     * @return
     */
    @ValidateParams
    @ResponseBody
    @RequiresRoles("seller")
    @RequestMapping(value ="/api/getGroupBuyActivityList" , method = RequestMethod.POST)
    public Object getGroupBuyActivityList(@RequestHeader(value = "token", defaultValue = "") String token,
                                            @Valid @RequestBody GetGroupBuyActivityListRequest params){
        String customerId = StringUtils.getCustomerIdByToken(token);
        int type = params.getType();
        int count = groupBuySponsorService.getGroupBuyActivityCount(customerId,type);
        // 组装分页信息
        Pager page = new Pager();
        page.setPageNo(params.getPageNo());
        page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
        page.setRowCount(count);
        page.doPage();
        List<Map<String,Object>> list = groupBuySponsorService.getMyGroupBuyActivityList(customerId,type,page);
        for(Map<String,Object> map : list){
            map.put("mainUrl", QCloudUtils.createThumbDownloadUrl((String) map.get("mainUrl"), ApiConstants.DEFAULT_PRODUCT_THUMBSTYLE));
        }
        Map<String,Object> map = new HashMap<>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        map.put("list",list);
        map.put("page",page);
        map.put("from","app");
        return map;
    }

    /**
     * 删除店主开过的团购列表
     * @param token
     * @param params
     * @return
     */
    @ValidateParams
    @ResponseBody
    @RequiresRoles("seller")
    @RequestMapping(value ="/api/delGroupBuyActivity" , method = RequestMethod.POST)
    public Object delGroupBuyActivity(@RequestHeader(value = "token", defaultValue = "") String token,
                                          @Valid @RequestBody DelGroupBuyActivityRequest params){
        String customerId = StringUtils.getCustomerIdByToken(token);
        String groupBuySponsorId = params.getGroupBuySponsorId();
        groupBuySponsorService.updateGroupBuyActivity(groupBuySponsorId);
        Map<String,Object> map = new HashMap<>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        return map;
    }

    /**
     * 评价商品
     * @param token
     * @param
     * @return
     */
    @ValidateParams
    @ResponseBody
    //@RequiresRoles(value = {"buyer","seller"}, logical = Logical.OR)
    @RequestMapping(value ="/api/sellerReviewProduct" , method = RequestMethod.POST)
    public Object sellerReviewProduct(@RequestHeader(value = "token", defaultValue = "") String token,
                                @Valid @RequestBody ReviewList params) {
        String buyerId = StringUtils.getCustomerIdByToken(token);
        String reviewText = "";
        Integer rating = null;
        List<SellerReviewProductRequest> reviewList = params.getReviewList();
        if(reviewList == null || reviewList.size() == 0){
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
        }
        for(SellerReviewProductRequest request : reviewList){
            rating = (request.getRating() > 5 || request.getRating() <= 0 ) ? 5 : request.getRating();
            reviewText = request.getReviewText();
            if(StringUtils.isBlank(reviewText)){
                return new MsgEntity(ApiMsgConstants.NOT_ALL_REVIEW, ApiMsgConstants.FAILED_CODE);
            }
            String orderItemId = request.getOrderItemId();
            //通过订单项查看订单是否已经确认收货
            Map<String,Object> map = orderService.getOrderByItemId(orderItemId);
            if(map == null){
                return new MsgEntity(ApiMsgConstants.REVIEW_ILLEGAL,
                        ApiMsgConstants.FAILED_CODE);
            }
            if((Long)map.get("orderItemCount") > reviewList.size()){
                return new MsgEntity(ApiMsgConstants.NOT_ALL_REVIEW, ApiMsgConstants.FAILED_CODE);
            }
            String productId = (String)map.get("productId");
            //判断此用户是否同一订单同一商品评价过此商品
            ReviewDetail  rd = reviewDetailService.getReviewByProduct(buyerId,productId,orderItemId);
            if(rd != null){
                return new MsgEntity(ApiMsgConstants.REVIEW_IS_EXIST,
                        ApiMsgConstants.FAILED_CODE);
            }
            ratingSummaryService.saveReview(buyerId,productId,orderItemId,rating,reviewText);
        }
        return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
                ApiMsgConstants.SUCCESS_CODE);
    }

    /**
     * 获取订单评价列表
     * @param token
     * @param
     * @return
     */
    @ValidateParams
    @ResponseBody
    //@RequiresRoles(value = {"buyer","seller"},logical = Logical.OR)
    @RequestMapping(value ="/api/getOrderReviewList" , method = RequestMethod.POST)
    public Object getOrderReviewList(@RequestHeader(value = "token", defaultValue = "") String token,
                                 @Valid @RequestBody GetOrderReviewListRequest params ){
        List<ReviewDto>     list = orderService.getReviewProductItemList(params.getOrderId());
        GetOrderReviewListResponse result = new GetOrderReviewListResponse();
        result.setList(list);
        return result;
    }

    /**
     * 获取退款订单项列表(退款单)
     * 所有有退款状态的订单项列表
     * @param token
     * @param params
     * @return
     */
    @ResponseBody
    @ValidateParams
    //@RequiresRoles(value = {"buyer","seller"},logical = Logical.OR)
    @RequestMapping(value = "/api/getSellerRefundOrderList", method = RequestMethod.POST)
    public Object getSellerRefundOrderList(@RequestHeader(value = "token", defaultValue = "") String token,
                                           @Valid @RequestBody GetSellerRefundOrderListRequest params){
        int pageNo  = params.getPageNo();
        String buyerId = StringUtils.getCustomerIdByToken(token);
        int count = orderService.countSellerRefundOrder(buyerId);
        // 组装分页信息
        Pager page = new Pager();
        page.setPageNo(pageNo);
        page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
        page.setRowCount(count);
        page.doPage();
        List<ProductItemDto> list = orderService.getSellerRefundOrderList(buyerId,page);
        GetSellerRefundOrderListResquest result = new GetSellerRefundOrderListResquest();
        result.setResultMap(list);
        result.setPage(page);
        return result;
    }



    /*************************************************** 退货退款 *****************************************************/

    /**
     * 用户点击退款按钮时调用,判断是否为最后一个退款,如果是 则返回 订单项单价 * 数量 + 邮费, 如果不是 返回 订单项单价 * 数量
     * @param token
     * @param params
     * @return 订单号 商品名 收货状态 退款金额 退款原因
     */
    @ValidateParams
    @ResponseBody
    //@RequiresRoles(value = {"buyer", "seller"},logical = Logical.OR)
    @RequestMapping(value = "/api/checkRefund", method = RequestMethod.POST)
    public Object checkRefund(@RequestHeader(value = "token", defaultValue = "") String token,
                               @Valid @RequestBody CheckRefundRequest params){
        String orderItemId = params.getOrderItemId();
        OrderItem orderItem   = new OrderItem();
        orderItem.setId(orderItemId);
        orderItem = orderItemService.get(orderItem);
        if (orderItem == null || !orderItem.getProductItem().getProduct().getCanRefund()) {
            return new MsgEntity(ApiMsgConstants.FAILED_MSG, ApiMsgConstants.FAILED_CODE);
        }

        CheckRefundResponse result = new CheckRefundResponse();
        Map<String, Object> newRefund = new HashMap<>();
        newRefund.put("phoneNumber",orderItem.getShop().getCustomer().getPhoneNumber());
        newRefund.put("productName",orderItem.getProductItem().getProduct().getName());
        newRefund.put("orderNum",orderItem.getOrder().getOrderNumber());
        newRefund.put("orderItemId",orderItem.getId());
        // 收货状态 1. 未收货 2. 已收货 3. 不确定
        String shippingStatus = "";
        switch (orderItem.getOrder().getShippingStatus()) {
            case "0":  // 未收货
                shippingStatus = "1";
                break;
            case "2":  // 已收货
                shippingStatus = "2";
                break;
            default:
                if(orderItem.getRefund() != null && orderItem.getRefund().getReceiptType().equals(0)){
                    shippingStatus = "1";
                }else if (orderItem.getRefund() != null && orderItem.getRefund().getReceiptType().equals(1)){
                    shippingStatus = "2";
                }else
                    shippingStatus = "3";
                break;
        }
        newRefund.put("shippingStatus",shippingStatus);
        // 总退款金额 如果是未收货,且不是此订单中最后一个退款 则 退款金额 = 退款数量 * 退款单价 如果是最后一个 则 退款金额 = 退款数量 * 退款单价 + 运费
        Map<String, Object> map             = orderService.getRefundCount(orderItem.getOrder().getId());
        long                 refundCount    = (long) map.get("refundCount");
        long                 orderItemCount = (long) map.get("orderItemCount");
        long                 hasFreightPriceCount = (long)map.get("hasFreightPriceCount");
        if(((refundCount + 1 == orderItemCount) && hasFreightPriceCount == 0) || orderItemCount == 1 ){ // 最后一个退款
            newRefund.put("amount",orderItem.getItemPrice().multiply(new BigDecimal(orderItem.getQuantity())).add(orderItem.getOrder().getFreightPrice())); // 订单项单价 * 数量 + 运费
            newRefund.put("hasFreightPrice","1");
        }else{ // 如果不是最后一个退款
            newRefund.put("amount",orderItem.getItemPrice().multiply(new BigDecimal(orderItem.getQuantity()))); // 订单项单价 * 数量
            newRefund.put("hasFreightPrice","0");
        }
        result.setNewRefund(newRefund);
        return result;
    }

    /**
     * 申请退款
     * @param token
     * @param
     * @return
     */
    @ValidateParams
    @ResponseBody
    //@RequiresRoles(value = {"buyer", "seller"},logical = Logical.OR)
    @RequestMapping(value = "/api/sellerRefund", method = RequestMethod.POST)
    public Object sellerRefund(@RequestHeader(value = "token", defaultValue = "") String token,
                               @Valid @RequestBody ReFundRequest params) {
        String buyerId     = StringUtils.getCustomerIdByToken(token);
        String orderItemId = params.getOrderItemId();
        String reason      = params.getReason();
        String phone       = params.getPhone();
        String describe    = params.getDescribe();
        String hasFreightPrice = params.getHasFreightPrice();
        // 金额 如果未收货 则为全款 如果收到货,则为协商价格
        BigDecimal amount = params.getAmount();
        // 收货状态 1.未收货 2.已收货
        int       receiptType = params.getReceiptType();
        OrderItem orderItem   = new OrderItem();
        orderItem.setId(orderItemId);
        orderItem = orderItemService.get(orderItem);
        if (orderItem == null || !orderItem.getProductItem().getProduct().getCanRefund()) {
            return new MsgEntity(ApiMsgConstants.FAILED_MSG, ApiMsgConstants.FAILED_CODE);
        }
        if(orderItem.getOrder().getShippingStatus().equals("0") && receiptType == 2){  // 服务器显示未发货 但是用户点击已收货
            return new MsgEntity("您还未收到快递!请选择\"未收到货\"", ApiMsgConstants.FAILED_CODE);
        }else if(orderItem.getOrder().getShippingStatus().equals("2") && receiptType == 1){ // 服务器显示已收货,但是用户点击未收货
            return new MsgEntity("您已确认收货!请选择\"已收到货\"", ApiMsgConstants.FAILED_CODE);
        }
        // 修改退货原因
        String refundId = params.getRefundId();
        Refund rf       = new Refund();
        rf.setId(refundId);
        //查询退款单是否可以更新
        Refund refund = refundService.get(rf);
        if (refund != null) { // 如果有退款记录 更新退款原因
            if (!Refund.REFUND_STATUS_NO_REFUND.equals(refund.getRefundStatus())) {
                return new MsgEntity(ApiMsgConstants.REFUND_NO_COMFIRE,
                        ApiMsgConstants.FAILED_CODE);
            }
            RefundLog log = new RefundLog();
            log.preInsert();
            log.setRefund(refund);
            log.setStatusNote("您的退款原因修改成功，请等待后台人员再次审核");
            refundLogService.insert(log);
            refund.setReturnReason(reason);
            // 金额
            refund.setAmount(amount);
            refund.setHasFreightPrice(hasFreightPrice);
            refund.setReceiptType(receiptType-1);
            refundService.updateRefund(refund);
        } else { // 如果没有退款记录,创建新的退款记录
            //判断此订单项是否可以申请退款
            OrderItemDto item = null;
            RLock        lock = DistLockHelper.getLock("sellerRefund");
            try {
                lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
                // 查询此订单项的退款状态
                item = orderItemService.checkReFund(orderItemId);
                if (item == null || StringUtils.isBlank(reason)) {
                    return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                            ApiMsgConstants.FAILED_CODE);
                }
                if (item.isRefundStatus()) { // 判断是否发起过申请
                    return new MsgEntity(ApiMsgConstants.REFUNDED,
                            ApiMsgConstants.FAILED_CODE);
                }
                // 删除之前的退款申请
                refundService.deleteRefund(item.getId(), "");
                // 获取买家信息
                Customer cus = customerService.get(buyerId);
                if (cus == null) {
                    return new MsgEntity(ApiMsgConstants.REFUNDED,
                            ApiMsgConstants.FAILED_CODE);
                }
                // 组装退款申请
                Refund ref = new Refund();
                ref.preInsert();
                ref.setPhone(phone);
                ref.setAmount(amount); // 退款总金额
                ref.setHasFreightPrice(hasFreightPrice);
                ref.setRefundStatus(Refund.REFUND_STATUS_NO_REFUND); // 默认未退款 0
                ref.setReturnStatus(Refund.RETURN_STATUS_UNTREATED); // 默认未退货 0
                // 退款数量默认为全部退
                ref.setQuantity((orderItem.getQuantity()) + (orderItem.getPromotionQuantity()));
                ref.setReturnReason(reason);
                ref.setOrderItem(new OrderItem(orderItemId));
                ref.setNote(cus.getUserName() + "申请退款成功");
                ref.setCustomer(cus);
                ref.setReceiptType(receiptType-1);
                refundService.save(ref);
                refundId = ref.getId();
                logger.debug(cus.getUserName() + "申请退款成功，退款单：" + refundId);
            } finally {
                if (lock.isLocked())
                    lock.unlock();
            }
        }
        // 退款申请成功后
        // 此订单项则在退款/退货中显示
        Map<String, Object> map = new HashMap<>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        //发送退款申请成功
        map.put("refundStatus", Refund.REFUND_STATUS_NO_REFUND);  // 未退款
        map.put("refundId", refundId);
        return map;
    }

    /**
     * 修改退款信息
     * 后台没审核时，买家修改退货原因
     * @param token
     * @param params
     * @return
     */
    //@ValidateParams
    //@ResponseBody
    //@RequiresRoles(value = {"buyer","seller"},logical = Logical.OR)
    //@RequestMapping(value ="/api/updateRefundReason" , method = RequestMethod.POST)
    //public Object updateRefundReason(@RequestHeader(value = "token", defaultValue = "") String token,
    //                                 @Valid @RequestBody UpdateRefundReasonRequest params){
    //    String refundId = params.getRefundId();
    //    String reason = params.getReason();
    //    Refund rf = new Refund();
    //    rf.setId(refundId);
    //    //查询退款单是否可以更新
    //    Refund refund = refundService.get(rf);
    //    if(refund==null||!Refund.REFUND_STATUS_NO_REFUND.equals(refund.getRefundStatus())){
    //        return new MsgEntity(ApiMsgConstants.REFUND_NO_COMFIRE,
    //                ApiMsgConstants.FAILED_CODE);
    //    }
    //    refund.setReturnReason(reason);
    //    refundService.updateReason(refund);
    //    return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
    //            ApiMsgConstants.SUCCESS_CODE);
    //}

    /**
     * 添加快递信息
     * 后台没审核通过并需要退货时，买家增加退款物流号
     * @param token
     * @param
     * @return
     */
    //@RequiresRoles(value = {"buyer","seller"},logical = Logical.OR)
    @ValidateParams
    @ResponseBody
    @RequestMapping(value ="/api/addRefundExpress" , method = RequestMethod.POST)
    public Object addRefundExpress(@RequestHeader(value = "token", defaultValue = "") String token,
                                   @Valid @RequestBody AddRefundExpressRequest params){
        String refundId = params.getRefundId();
        String shippingNum = params.getShippingNum();
        String expressCode = params.getExpressCode();

        // 订阅快递100 推送物流信息服务
        ThreadPoolUtil.execute(() -> {
            TaskResponse tr = ExpressUtils.subscribeExpress(expressCode,shippingNum);
            logger.info("退款单[" + refundId + "]订阅物流信息完成：" + tr.toString());
        });
        //查询退款单是否可以更新
        Refund rf = new Refund();
        rf.setId(refundId);
        // 只有允许退货，买家才可以添加退货单
        Refund refund = refundService.get(rf);
        if(refund==null||!Refund.REFUND_STATUS_REFUND.equals(refund.getReturnStatus())){
            return new MsgEntity(ApiMsgConstants.REFUND_NOT_COMFIRE,
                    ApiMsgConstants.FAILED_CODE);
        }
        List<Express> express = expressService.findByName(expressCode);
        if(express == null){
            return new MsgEntity("暂时不支持此快递公司",
                    ApiMsgConstants.FAILED_CODE);
        }
        if(express.size() != 1){
            return new MsgEntity("请输入完整快递公司名称",
                    ApiMsgConstants.FAILED_CODE);
        }
        refund.setExpress(express.get(0));
        refund.setShippingNumber(shippingNum);
        //设置退款单退货状态为退货中
        refund.setReturnStatus(Refund.RETURN_STATUS_RETURNS_IN);
        refund.setNote("买家退货单已添加");
        refundService.saveExpress(refund);
        return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
                ApiMsgConstants.SUCCESS_CODE);
    }

    /**
     * 撤销退款 (当退款申请未审核时可以撤销退款)
     * 状态变为退款已关闭
     * @param token
     * @param
     * @return
     */
    @ValidateParams
    @ResponseBody
    //@RequiresRoles(value = {"buyer","seller"},logical = Logical.OR)
    @RequestMapping(value ="/api/cancelReFund" , method = RequestMethod.POST)
    public Object cancelReFund(@RequestHeader(value = "token", defaultValue = "") String token,
                               @Valid @RequestBody CancelReFundRequest params){
        String refundId = params.getRefundId();
        Refund rf = new Refund();
        rf.setId(refundId);
        //查询退款单是否可以更新
        Refund refund = refundService.get(rf);
        refundService.sellerCancelRefund(refund);
        return new MsgEntity(ApiMsgConstants.SUCCESS_MSG,
                ApiMsgConstants.SUCCESS_CODE);
    }

    /**
     * 删除退款单 (退款已完成,退款已关闭,拼团失败退款已完成)
     * @param token
     * @param params
     * @return
     */
    @ValidateParams
    @ResponseBody
    //@RequiresRoles(value = {"buyer","seller"},logical = Logical.OR)
    @RequestMapping(value ="/api/sellerDelReFund" , method = RequestMethod.POST)
    public Object sellerDelReFund(@RequestHeader(value = "token", defaultValue = "") String token,
                                  @Valid @RequestBody SellerDelReFundRequest params){
        String refundId = params.getRefundId();
        Refund rf = new Refund();
        rf.setId(refundId);
        //查询退款单是否可以更新
        Refund refund = refundService.get(rf);
        //if(!Refund.REFUND_STATUS_COMPLETED.equals(refund.getRefundStatus())  // 已退款
        //        && !Refund.RETURN_STATUS_COMPLETED.equals(refund.getReturnStatus())){
        //    return new MsgEntity(ApiMsgConstants.RETURN_STATUS_APP_FAILED, ApiMsgConstants.FAILED_CODE);
        //}
        refundService.deleteRefund("",refundId);
        return new MsgEntity("已删除退款单!", ApiMsgConstants.SUCCESS_CODE);
    }

    /**
     * 卖家退款日志 (退款详情)
     * @param token
     * @param param
     * @return  allStatus: 1.退款中;2.退款完成;3.退款关闭;4.错误
     */
    //@RequiresRoles(value = {"buyer","seller"},logical = Logical.OR)
    @RequestMapping("/api/reFundLog")
    public Object reFundLog(@RequestHeader(value = "token", defaultValue = "") String token,
                            @RequestParam(value = "params", defaultValue = "") String param){
        JSONObject params = null;
        try {
            params = JSONObject.parseObject(param);
        } catch (Exception e) {
            logger.error("params 出错"+e.getMessage());
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (params == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String orderItemId = params.getString("orderItemId");
        if(StringUtils.isBlank(orderItemId)){
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }

        //获取退款单信息
        RefundDto	refundDto = refundService.getRefundInfo(orderItemId);
        //获取日志
        RefundLog log = new RefundLog();
        List<RefundLogDto> list = null;
        if(refundDto!=null){
            log.setOrderItemId(refundDto.getOrderItemId());
            list = refundLogService.findLogList(log);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        map.put("refund", refundDto);
        if(list!=null)
            map.put("list", list);
        return map;
    }

    /*************************************************** 退货退款 *****************************************************/

    /************************************************** 2016.05.16 订单重构 *******************************************************/

    /**
     * 根据订单Id获取订单地址
     */
    @ValidateParams
    @ResponseBody
    @RequestMapping(value ="/api/getOrderAddress" , method = RequestMethod.POST)
    public Object getOrderAddress(@RequestHeader(value = "token", defaultValue = "") String token,
                                  @Valid @RequestBody GetOrderAddressRequest params) {
        String  orderId = params.getOrderId();
        String  buyerId = StringUtils.getCustomerIdByToken(token);
        Address address = addressService.getOrderAddress(orderId, buyerId);
        if (address == null) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        resultMap.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        resultMap.put("address", address);
        return resultMap;
    }


    /**
     * 修改订单的收货地址
     * @param token
     * @param params
     * @return
     * @throws ParseException
     */
    @ValidateParams
    @ResponseBody
    @RequestMapping(value ="/api/changeOrderAddress" , method = RequestMethod.POST)
    public Object changeOrderAddress(@RequestHeader(value = "token", defaultValue = "") String token,
                                     @Valid @RequestBody ChangeOrderAddressRequest params) throws ParseException {
        OrderDto order = new OrderDto();
        order.setId(params.getOrderId());
        order = orderService.getSellerOrderDetail(order, StringUtils.getCustomerIdByToken(token));
        if (!order.getOrderType().equals("2") && !order.getOrderType().equals("3")) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
        }
        SimpleDateFormat sdf         = new SimpleDateFormat("HH:mm");
        String           currentDate = sdf.format(new Date());
        Date             currDate    = sdf.parse(currentDate);
        if (currDate.after(sdf.parse("17:00")) && currDate.before(sdf.parse("22:00"))) {
            return new MsgEntity("修改地址失败！请于17:00之前或22:00之后进行地址修改。", ApiMsgConstants.FAILED_CODE);
        }
        if (!order.getOrderStatus().equals("0")) {
            return new MsgEntity("修改地址失败！您购买的商品正在准备发货，无法修改地址。", ApiMsgConstants.FAILED_CODE);
        }
        orderService.changeOrderAddress(params, StringUtils.getCustomerIdByToken(token), order);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        resultMap.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        return resultMap;
    }
}
