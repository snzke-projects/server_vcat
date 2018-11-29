package com.vcat.api.service;

import com.vcat.api.service.mq.CloseGroupBuyReceive;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.common.sms.SmsClient;
import com.vcat.common.thirdparty.WeixinClient;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.ec.dao.GroupBuyDao;
import com.vcat.module.ec.dto.GroupBuyCustomerDto;
import com.vcat.module.ec.dto.GroupBuyDto;
import com.vcat.module.ec.dto.GroupBuySponsorDto;
import com.vcat.module.ec.dto.OrderItemDto;
import com.vcat.module.ec.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Code.Ai on 16/5/11.
 * Description:
 */
@Service
@Transactional(readOnly = true)
public class GroupBuyService extends CrudService<GroupBuy> {
    @Autowired
    private GroupBuyDao groupBuyDao;
    @Autowired
    private GroupBuySponsorService groupBuySponsorService;
    @Autowired
    private GroupBuyCustomerService groupBuyCustomerService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private CloseGroupBuyReceive closeGroupBuyReceive;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Override
    protected CrudDao<GroupBuy> getDao() {
        return groupBuyDao;
    }

    public GroupBuyDto getGroupBuy(GroupBuyDto groupBuyDto){
        return groupBuyDao.getGroupBuy(groupBuyDto);
    }

    /**
     * 根据商品id得到团购id
     * @param productId
     * @return
     */
    public String getGroupBuyByProductId(String productId){
        return groupBuyDao.getGroupBuyByProductId(productId);
    }

    /**
     * 如果是团购订单,支付成功后即开团成功
     * @param payment
     */
    @Transactional(readOnly = false)
    public void updateGroupBuyOrderStatus(Payment payment, String openId) {
        logger.debug("更新团购状态...");
        List<Map<String,Object>> mapList = groupBuyCustomerService.getGroupBuyInfo(payment.getId());
        if(mapList != null && mapList.size() == 1){
            Map<String,Object> map = mapList.get(0);
            logger.debug("处理GroupBuyInfo:"+map);
            String orderItemType =  (String) map.get("orderItemType");
            logger.debug("得到orderItemType:"+orderItemType);
            // 卖家开团支付成功 或者 买家开团支付成功 修改支付状态和时间
            if(orderItemType.equals(ApiConstants.GROUPBUY_SELLER) || orderItemType.equals(ApiConstants.GROUPBUY_BUYER)){
                String groupBuyCustomerId = (String) map.get("groupBuyCustomerId");
                String groupBuySponsorId = (String) map.get("groupBuySponsorId");
                GroupBuySponsorDto groupBuySponsorDto = new GroupBuySponsorDto();
                groupBuySponsorDto.setId(groupBuySponsorId);
                groupBuySponsorDto = groupBuySponsorService.getGroupBuySponsor(groupBuySponsorDto);
                logger.debug("得到groupBuySponsorDto："+groupBuySponsorDto);
                // 如果是开团 更新开团时间
                if((Boolean)map.get("isSponsor")){
                    groupBuySponsorDto.setStatus(1);
                    Date startDate = new Date();
                    groupBuySponsorDto.setStartDate(startDate);
                    Date endDate = new Date(startDate.getTime() + (Integer)map.get("period") * 24 * 60 * 60 * 1000);
                    if(groupBuySponsorDto.getGroupBuyDto().getEndDate().getTime() < endDate.getTime()){
                        endDate = groupBuySponsorDto.getGroupBuyDto().getEndDate();
                    }
                    groupBuySponsorDto.setEndDate(endDate);
                    logger.debug("开团人支付,更新开团状态和时间");
                    groupBuySponsorService.updateGroupBuySponsor(groupBuySponsorDto);
                }
                // 更新支付状态
                GroupBuyCustomerDto groupBuyCustomerDto = new GroupBuyCustomerDto();
                groupBuyCustomerDto.setId(groupBuyCustomerId);
                groupBuyCustomerDto = groupBuyCustomerService.getGroupBuyCustomer(groupBuyCustomerDto);
                groupBuyCustomerDto.setStatus(1);
                groupBuyCustomerService.updateGroupBuyCustomer(groupBuyCustomerDto);
                logger.debug("更新支付状态"+groupBuyCustomerDto);

                // 如果此团购支付人数 == 需要团购人数, 则更新状态为拼团成功
                GroupBuySponsorDto  groupBuy = new GroupBuySponsorDto();
                groupBuy.setId(groupBuySponsorId);
                groupBuy = groupBuySponsorService.getGroupBuySponsor(groupBuy);
                logger.debug("重新获取groupBuy："+groupBuy);
                if(!(Boolean)map.get("isSponsor") && Objects.equals(groupBuy.getJoinedCount() + 1, groupBuy.getGroupBuyDto().getNeededPeople())){
                    groupBuy.setStatus(2);
                    logger.debug("开团成功更新状态");
                    groupBuySponsorService.updateGroupBuySponsor(groupBuy);
                    // 给拼团者发送拼团成功短信
                    // 参团者信息
                    List<Map<String, Object>> joinedCustomers = groupBuyCustomerService.getJoinedCustomers(groupBuySponsorId);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for(Map<String, Object> customerInfo :joinedCustomers){
                        String phone = (String) customerInfo.get("phone");
                        String productName = StringUtils.StringFilter((String)customerInfo.get("productName"));
                        String text = "【V猫小店】亲爱的喵主，恭喜您，您拼团的商品\"" + productName + "\"已拼团成功！参团时间[" + simpleDateFormat.format((Date)customerInfo.get("joinTime")) + "]";
                        try {
                            SmsClient.sendSms(text,phone);
                        } catch (IOException e) {
                            logger.error("向[" + phone + "]发送短信[" + text + "]失败：" + e.getMessage(),e);
                        }
                        //todo
                        // 如果是微信支付,给微信支付的参团者发送微信消息
                        Payment pay = new Payment();
                        pay.setId((String)customerInfo.get("paymentId"));
                        List<Map<String, Object>> results = orderService.getWeixinInfoByPayment(pay);
                        for(Map<String, Object> result : results) {
                            logger.debug("查询getWeixinInfoByPayment:" + result);
                            if (StringUtils.isNullBlank(openId)) {
                                openId = (String) result.get("openId");
                            }
                            if (result != null && result.size() != 0 && !StringUtils.isBlank(openId) &&
                                    !StringUtils.isBlank((String) result.get("groupBuySponsorId"))) {
                                WeixinClient.sendGroupBuySuccessMsg(openId,
                                        (String) result.get("productName"),
                                        (String) result.get("id"),
                                        (String) result.get("orderNumber"),
                                        String.valueOf(result.get("price")));
                            }
                        }
                    }

                    // 根据 groupBuySponsorId 找出所有参团者订单
                    //List<Map<String, Object>> joinedCustomerList = groupBuyCustomerService.getJoinedCustomers(groupBuySponsorId);
                    //logger.debug("得到所有参团者："+joinedCustomerList);
                    //Product                   product            = new Product();
                    //product.setId(groupBuySponsorDto.getGroupBuyDto().getProductId());
                    //product = productService.get(product);
                    //// 如果是拼团成功且是不可退款的订单,拼团成功后将待确认收入变为可提现收入
                    //if(joinedCustomerList != null && !product.getCanRefund()){
                    //    logger.debug("该商品不可退款，转换待确认到可提现");
                    //    // 拼团成功待发货时将待确认收入变为可提现收入
                    //    joinedCustomerList.stream().filter(joinedCustoemr -> !StringUtils.isBlank((String) joinedCustoemr.get("orderId")))
                    //            .forEach(joinedCustoemr -> {
                    //                // 拼团成功待发货时将待确认收入变为可提现收入
                    //                logger.debug("拼团成功待发货时将待确认收入变为可提现收入");
                    //                orderService.checkShippingEarning((String) joinedCustoemr.get("buyerId"), (String) joinedCustoemr.get("orderId"), 3);
                    //            });
                    //}
                }
            }
        }

    }

    /**
     * 插入拼团信息
     * @param groupBuyDto
     * @param customerId
     * @param item
     * @param type
     * @return
     */
    @Transactional(readOnly = false)
    public String addGroupBuyInfo(GroupBuyDto groupBuyDto, String customerId, OrderItemDto item, int type){
        int newGroupBuySponsorStatus = GroupBuySponsor.NO_PAY;
        int groupBuyCustomerStatus = GroupBuyCustomer.NO_PAY;
        String orderId = null;
        String orderItemId = null;
        if(type == ApiConstants.ZERO_LAUNCH){
            groupBuyCustomerStatus = GroupBuyCustomer.YET_PAY; // 零元开团默认已支付
            newGroupBuySponsorStatus = GroupBuySponsor.PROCESS; // 零元开团默认开团即进行中
        }else if(type == ApiConstants.SELLER_LAUNCH || type == ApiConstants.BUYER_LAUNCH){
            orderId = item.getOrderId();
            orderItemId = item.getId();
            type = type == ApiConstants.SELLER_LAUNCH ? 1 : 2;  // 开团类型
        }
        GroupBuySponsorDto newGroupBuySponsorDto = new GroupBuySponsorDto();
        newGroupBuySponsorDto.setId(IdGen.uuid());
        newGroupBuySponsorDto.setGroupBuyDto(groupBuyDto);
        newGroupBuySponsorDto.setSponsorId(customerId);
        newGroupBuySponsorDto.setLocked(false);
        newGroupBuySponsorDto.setStatus(newGroupBuySponsorStatus);
        newGroupBuySponsorDto.setType(type); // 0,1,2
        Date startDate = new Date();
        newGroupBuySponsorDto.setStartDate(startDate);
        int period = groupBuyDto.getPeriod();
        Date endDate = new Date(startDate.getTime() + period * 24 * 60 * 60 * 1000);
        // 获取团购结束时间
        Date groupBugEndTime = groupBuyDto.getEndDate();
        if(groupBugEndTime.getTime() < endDate.getTime()){
            endDate = groupBugEndTime;
        }
        newGroupBuySponsorDto.setEndDate(endDate);
        //退款延后10分钟
        Date delayEndDate = new Date(endDate.getTime() + 10 * 60 * 1000);
        groupBuySponsorService.insertGroupBuySponsor(newGroupBuySponsorDto);
        // 向 ec_group_buying_customer 插入记录

        GroupBuyCustomerDto groupBuyCustomerDto = new GroupBuyCustomerDto();
        groupBuyCustomerDto.setId(IdGen.uuid());
        groupBuyCustomerDto.setGroupBuySponsorId(newGroupBuySponsorDto.getId());
        groupBuyCustomerDto.setJoinedDate(startDate);
        groupBuyCustomerDto.setCustomerId(newGroupBuySponsorDto.getSponsorId());
        groupBuyCustomerDto.setSponsor(true);
        groupBuyCustomerDto.setStatus(groupBuyCustomerStatus);
        groupBuyCustomerDto.setOrderId(orderId);
        groupBuyCustomerDto.setOrderItemId(orderItemId);
        groupBuyCustomerService.insertGroupBuyCustomer(groupBuyCustomerDto);

        // 设置拼团结束关闭拼团以及自动退款任务
        closeGroupBuyReceive.addCloseGroupBuyJob(newGroupBuySponsorDto.getId(), delayEndDate);

        return newGroupBuySponsorDto.getId();
    }

    /**
     * 拼团超时，关闭拼团活动
     * @param sponsorId
     */
    @Transactional(readOnly = false)
    public void closeWithTimeOut(String sponsorId){
        GroupBuySponsor sponsor = groupBuySponsorService.get(sponsorId);

        if(null == sponsor){
            logger.error("拼团活动[" + sponsorId + "]为空，跳过关闭拼团活动任务");
        }else if(null == sponsor.getStatus()){
            logger.warn("拼团活动[" + sponsorId + "]状态为空，跳过关闭拼团活动任务");
        }else if(sponsor.getStatus() == 0){
            groupBuySponsorService.closeSponsor(sponsorId);
        }else if(sponsor.getStatus() == 1 && sponsor.getCount() < sponsor.getGroupBuy().getNeededPeople()){
            groupBuySponsorService.closeSponsor(sponsorId);

            groupBuyCustomerService.findListBySponsorId(sponsorId).forEach(c -> {
                if(null != c.getOrder() && null != c.getStatus() && c.getStatus() == 1){
                    refundService.createReturn(c.getOrder().getId(), "拼团失败，系统自动创建退款单");
                }
            });
        }else{
            logger.warn("拼团活动[" + sponsorId + "]状态正常，跳过关闭拼团活动任务");
        }
    }
}
