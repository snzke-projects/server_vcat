package com.vcat.module.ec.service;

import com.alibaba.fastjson.JSON;
import com.alipay.entity.AlipayRefundResponseData;
import com.tencent.WXPay;
import com.tencent.business.RefundBusiness;
import com.tencent.protocol.refund_protocol.RefundReqData;
import com.tencent.protocol.refund_protocol.RefundResData;
import com.vcat.common.config.Global;
import com.vcat.common.persistence.Page;
import com.vcat.common.sms.SmsClient;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.FundOperLog;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.core.service.ServiceException;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.ec.dao.*;
import com.vcat.module.ec.entity.*;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 退款单Service
 */
@Service
@Transactional(readOnly = true)
public class RefundService extends CrudService<RefundDao, Refund> {
    @Autowired
    private ShopFundDao shopFundDao;
    @Autowired
    private FundOperLogService fundOperLogService;
    @Autowired
    private RefundLogDao refundLogDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private OrderLogService orderLogService;
    @Autowired
    private ProductItemService productItemService;
    private String wechatMchId = Global.getConfig("wechat.pay.app.mch.id");           // 微信商户号
    private String refundFeeType = "CNY";

    @Override
    public Refund get(Refund entity) {
        entity = super.get(entity);
        RefundLog log = new RefundLog();
        log.setRefund(entity);
        entity.setLogList(refundLogDao.findList(log));
        return entity;
    }

    @Override
    public Refund get(String id) {
        Refund refund = new Refund();
        refund.setId(id);
        return get(refund);
    }

    @Transactional(readOnly = false)
    public void verifyReturn(Refund refund){
        doFailureJob(refund);
        int i = dao.verifyReturn(refund);
        if(i == 1){
            refundLogDao.insert(RefundLog.create(refund));
        }else{
            throw new ServiceException("审核退货失败：退款单被撤销或已审核！");
        }
    }

    @Transactional(readOnly = false)
    public void confirmReturn(Refund refund){
        doFailureJob(refund);
        int i = dao.confirmReturn(refund);
        if(i == 1){
            refundLogDao.insert(RefundLog.create(refund));
        }else{
            throw new ServiceException("确认退货失败：退款单被撤销或已确认退货！");
        }
    }


    @Transactional(readOnly = false)
    public void verify(Refund refund){
        doFailureJob(refund);
        int i = dao.verify(refund);
        refund = dao.get(refund);
        if(i == 1){
            refundLogDao.insert(RefundLog.create(refund));
        }else{
            throw new ServiceException("审核退款失败：退款单被撤销或已审核！");
        }
    }

    /**
     * 执行退货/退款失败相关逻辑
     * @param refund
     */
    private void doFailureJob(Refund refund){
        if(Refund.REFUND_STATUS_FAILURE.equals(refund.getRefundStatus())
                || Refund.RETURN_STATUS_FAILURE.equals(refund.getReturnStatus())){
            // 发送退货/退款失败短信给买家
            String tplValue = "#name#="+refund.getPhone()+"&#order_no#="+refund.getOrderItem().getOrder().getOrderNumber()+"&#msg#="+refund.getSqlMap().get("verifyNote");
            SmsClient.tplSendSms(SmsClient.TPL_REFUND_FAILURE, tplValue, refund.getPhone());

            // 更新不应该包含邮费但是已包含邮费的退款单
            int updateCount = dao.updateHasFreightPriceRefundAmount(refund.getId());
            if(updateCount > 0){
                logger.warn("已更新["+updateCount+"]条不应该包含邮费但是已包含邮费的退款单！");
            }
        }
    }

    @Transactional(readOnly = false)
    public void confirmRefundCompleted(Refund refund){
        int c = dao.confirmRefundCompleted(refund);
        if(c == 1){
            refund = dao.get(refund);
            refundLogDao.insert(RefundLog.create(refund));
        }else{
            throw new ServiceException("处理失败：退款单被撤销或已退款完成！");
        }

        // 验证该订单中未完成退款单是否为0，如果是，则完成该订单
        Integer count = orderItemDao.getUnCompletedRefundCountByOrderId(refund.getOrderItem().getOrder().getId());
        if(0 == count){
            //完成订单
            Integer i = orderDao.completedByRefund(refund.getOrderItem().getOrder());
            orderLogService.addLog(refund.getOrderItem().getOrder(), "退款完成订单自动确认收货", null != i && i > 0);
        }

        // 退款失败
        doFailureJob(refund);
        if(refund.REFUND_STATUS_COMPLETED.equals(refund.getRefundStatus())){
            // 获取退款相关订单项
            OrderItem orderItem = refund.getOrderItem();
            orderItem = orderItemDao.get(orderItem);

            // 增加商品数量
//            productItemService.addQuantityByRefund(orderItem.getProductItem().getId(),refund.getQuantity() + orderItem.getPromotionQuantity());

            String orderNumber = refund.getOrderItem().getOrder().getOrderNumber();

            if(null != refund.getShop() && StringUtils.isNotBlank(refund.getShop().getId())){
                ShopFund shopFund = new ShopFund();
                shopFund.setId(refund.getShop().getId());

                shopFund = shopFundDao.get(shopFund);

                // 待减去的销售待确认资金中是否需要加上销售分红
                boolean needSubtractBonusToSaleHold = (orderItem.getShop().getAdvancedShop() == 1   // 如果销售店铺是钻石小店
                        && (orderItem.getOrderType() == Order.ORDER_TYPE_NORMAL                     // 且订单类型为普通订单或买家开团/参团订单
                        || orderItem.getOrderType() == Order.ORDER_TYPE_ROUPBUY_BUYER));

                double sale = null == orderItem.getSaleEarning() ? 0 : orderItem.getSaleEarning().doubleValue();
                double bonus = null == orderItem.getBonusEarning() ? 0 : orderItem.getBonusEarning().doubleValue();

                // 扣除店铺销售待确认金额
                if(needSubtractBonusToSaleHold && (sale == 0 && bonus == 0)){
                    logger.warn("跳过扣除钻石小店下线订单[" + orderNumber + "]订单项[" + orderItem.getId() + "]退款待确认销售金额[" + (sale + bonus) + "]，原因：佣金为0！");
                }else if(!needSubtractBonusToSaleHold && sale == 0){
                    logger.warn("跳过扣除订单[" + orderNumber + "]订单项[" + orderItem.getId() + "]退款待确认销售金额[" + orderItem.getSaleEarning() + "]，原因：佣金为0！");
                }else if(!needSubtractBonusToSaleHold && orderItem.getOrderType() == Order.ORDER_TYPE_SAMPLE){
                    logger.warn("跳过扣除订单[" + orderNumber + "]订单项[" + orderItem.getId() + "]退款待确认销售金额[" + orderItem.getSaleEarning() + "]，原因：拿样订单！");
                }else{
                    // 获取该订单项的销售待确认金额(默认为退款订单项销售奖励乘以退货数量)
                    BigDecimal fund;

                    // 判断待减去的销售待确认资金中是否需要加上销售分红
                    if(needSubtractBonusToSaleHold){
                        fund = new BigDecimal((sale + bonus) * refund.getQuantity());
                    }else{
                        fund = new BigDecimal(sale * refund.getQuantity());
                    }

                    // 记录之前的销售待确认金额
                    BigDecimal saleHoldFund = shopFund.getSaleHoldFund();

                    // 设置待减去的销售待确认金额
                    shopFund.setSaleHoldFund(fund);

                    // 减去退款完成的销售待确认金额
                    shopFundDao.refundSuccess(shopFund);

                    String logMsg = "订单[" + orderNumber + "]订单项[" + orderItem.getId() + "]退款成功，减去销售待确认金额[" + fund + "]";

                    // 写入资金操作日志
                    fundOperLogService.insert(refund.getShop().getId(), FundFieldType.SALE_HOLD_FUND, BigDecimal.ZERO.subtract(fund), saleHoldFund.subtract(fund), refund.getId(), FundOperLog.RETURN, logMsg);
                }
                // 扣除上家店铺分红待确认金额
                if(null == orderItem.getParent() || StringUtils.isEmpty(orderItem.getParent().getId())){
                    logger.warn("扣除订单[" + orderNumber + "]订单项[" + orderItem.getId() + "]上家退款待确认分红金额失败：上家店铺[" + orderItem.getParent() + "]为空！");
                }else if(null == orderItem.getBonusEarning() || orderItem.getBonusEarning().compareTo(BigDecimal.ZERO) < 1){
                    logger.warn("跳过扣除订单[" + orderNumber + "]订单项[" + orderItem.getId() + "]上家退款待确认分红金额[" + orderItem.getBonusEarning() + "]，原因：分红金额为0！");
                }else if(orderItem.getOrderType() == Order.ORDER_TYPE_SAMPLE && orderItem.getShop().getAdvancedShop() == 1){
                    logger.warn("跳过扣除订单[" + orderNumber + "]订单项[" + orderItem.getId() + "]上家退款待确认分红金额[" + orderItem.getBonusEarning() + "]，原因：钻石小店拿样订单！");
                }else{
                    BigDecimal parentBonus = new BigDecimal(bonus * refund.getQuantity());
                    cancelBonus(orderItem.getParent().getId(), parentBonus, refund.getId(), "下线订单[" + orderNumber + "]订单项[" + orderItem.getId() + "]退款成功，减去分红待确认金额[" + parentBonus + "]");
                }
            }

            // 发送退款成功短信给买家
            String tplValue = "#name#=" + refund.getPhone() + "&#order_no#=" + orderNumber;
            SmsClient.tplSendSms(SmsClient.TPL_REFUND_SUCC, tplValue, refund.getPhone());
        }
    }

    /**
     * 扣除分红奖励
     * @param shopId 待扣除的店铺
     * @param bonus 扣除金额
     * @param refundId 对应退款ID
     * @param logTitle 扣除日志标题
     */
    private void cancelBonus(String shopId, BigDecimal bonus, String refundId, String logTitle){
        ShopFund shopFund = new ShopFund();
        shopFund.setId(shopId);

        shopFund = shopFundDao.get(shopFund);

        // 获取之前的分红待确认金额
        BigDecimal oldBonusHoldFund = shopFund.getBonusHoldFund();

        // 设置待减去的分红待确认金额
        shopFund.setBonusHoldFund(bonus);

        // 减去推荐人的分红待确认金额
        shopFundDao.addBonusBoldFund(shopFund);

        // 写入资金操作日志
        fundOperLogService.insert(shopId, FundFieldType.BONUS_HOLD_FUND, BigDecimal.ZERO.subtract(bonus), oldBonusHoldFund.subtract(bonus), refundId, FundOperLog.RETURN, logTitle);
    }

    public Page<Refund> findHistory(Page<Refund> page,Refund refund){
        refund.setPage(page);
        page.setList(dao.findHistory(refund));
        return page;
    }

    /**
     * 执行退款结果（支付宝）
     * @param alipayRefundResponseData
     */
    @Transactional(readOnly = false)
    public boolean executionRefundByAlipay(AlipayRefundResponseData alipayRefundResponseData){
        logger.info("执行退款结果（支付宝）alipayRefundResponseData = " + ReflectionToStringBuilder.toString(alipayRefundResponseData));

        String batchNo = alipayRefundResponseData.getBatch_no();
        Refund refund = dao.getRefundByBatchNo(batchNo);
        if(null == refund){
            logger.error("未找到批次号[" + batchNo + "]对应退款单");
            return false;
        }
        RefundInterface refundInterface = dao.getInterfaceData(refund.getId());
        if(null == refundInterface){
            logger.error("未找到批次号[" + batchNo + "]对应退款接口数据");
            return false;
        }
        String createName = refundInterface.getCreateBy().getName();

        List<AlipayRefundResponseData.RefundDetail> list = alipayRefundResponseData.getResultList();

        if(null != list && list.size() == 1){
            RefundInterface refundInterfaceResult = new RefundInterface();
            AlipayRefundResponseData.RefundDetail refundDetail = list.get(0);
            boolean isSuccess = "SUCCESS".equalsIgnoreCase(refundDetail.getResult());
            if(refund.getCanConfirm()){
                if(isSuccess){
                    refund.setNote("[" + createName + "]执行支付宝退款成功！");
                    refund.setRefundStatus(Refund.REFUND_STATUS_COMPLETED);
                    confirmRefundCompleted(refund);
                }else{
                    logger.warn("退款单[" + refund.getId() + "]支付宝接口退款失败");
                }
            }else{
                logger.warn("退款单[" + refund.getId() + "]已经手动退款完成");
            }

            refundInterfaceResult.setRefund(refund);
            refundInterfaceResult.setBatchNo(batchNo);
            refundInterfaceResult.setResult(isSuccess);
            refundInterfaceResult.setResponseData(JSON.toJSONString(alipayRefundResponseData));
            dao.updateInterfaceResult(refundInterfaceResult);
        }else{
            logger.warn("退款单[" + refund.getId() + "]回调数据[ResultList]" + JSON.toJSONString(list) + "不完整！");
        }
        return true;
    }

    /**
     * 执行微信退款
     * @param refund
     * @return
     */
    @Transactional(readOnly = false)
    public boolean executionRefundByWeChat(Refund refund){
        refund = get(refund);
        if(refundRequestByWechat(refund, false)){
            return true;
        }
        return false;
    }

    /**
     * 执行微信移动端退款
     * @param refund
     * @return
     */
    @Transactional(readOnly = false)
    public boolean executionRefundByWeChatMobile(Refund refund) {
        refund = get(refund);
        if(refundRequestByWechat(refund, true)){
            return true;
        }
        return false;
    }

    /**
     * 执行微信退款
     * @param refund
     */
    @Transactional(readOnly = false)
    public boolean refundRequestByWechat(Refund refund, boolean fromMobile) {
        RefundBusiness.ResultListener resultListener = new RefundBusiness.ResultListener() {
            @Override
            public void onFailByReturnCodeError(RefundReqData refundReqData, RefundResData refundResData) {
                insertInterfaceDataByWechat(refund, refundReqData, refundResData);
            }

            @Override
            public void onFailByReturnCodeFail(RefundReqData refundReqData, RefundResData refundResData) {
                insertInterfaceDataByWechat(refund, refundReqData, refundResData);
            }

            @Override
            public void onFailBySignInvalid(RefundReqData refundReqData, RefundResData refundResData) {
                insertInterfaceDataByWechat(refund, refundReqData, refundResData);
            }

            @Override
            public void onRefundFail(RefundReqData refundReqData, RefundResData refundResData) {
                insertInterfaceDataByWechat(refund, refundReqData, refundResData);
            }

            @Override
            public void onRefundSuccess(RefundReqData refundReqData, RefundResData refundResData) {
                insertInterfaceDataByWechat(refund, refundReqData, refundResData);
            }
        };
        try {
            int paymentAmount = refund.getPayment().getAmount().multiply(new BigDecimal("100")).intValue();
            int refundAmount = refund.getAmount().multiply(new BigDecimal("100")).intValue();
            WXPay wxPay = new WXPay();
            if(fromMobile){
                wxPay.initConfigurationForMobile();
            }else {
                wxPay.initConfigurationForWap();
            }
            wxPay.doRefundBusiness(new RefundReqData(refund.getPayment().getTransactionNo(),refund.getPayment().getPaymentNo()
                    ,null,refund.getId(),paymentAmount,refundAmount,wechatMchId,refundFeeType,wxPay.getConfigure()),resultListener);
            return true;
        } catch (Exception e) {
            throw new ServiceException("请求微信退款失败：" + e.getMessage());
        }
    }

    /**
     * 插入支付宝退款接口数据
     * @param refund
     * @param data
     */
    @Transactional(readOnly = false)
    public void insertInterfaceDataByAlipay(Refund refund,Map<String,String> data){
        checkInterface(refund);

        RefundInterface refundInterface = new RefundInterface();
        refundInterface.preInsert();
        refundInterface.setRefund(refund);
        refundInterface.setBatchNo(data.get("batch_no"));
        refundInterface.setRequestData(JSON.toJSONString(data));

        dao.insertInterfaceDataByAlipay(refundInterface);
    }

    /**
     * 插入微信退款接口数据
     * @param refund
     * @param refundResData
     */
    @Transactional(readOnly = false)
    public void insertInterfaceDataByWechat(Refund refund, RefundReqData refundReqData, RefundResData refundResData) {
        checkInterface(refund);

        RefundInterface refundInterface = new RefundInterface();
        refundInterface.preInsert();
        refundInterface.setRefund(refund);
        refundInterface.setBatchNo(refundResData.getRefund_id());
        refundInterface.setRequestData(JSON.toJSONString(refundReqData));
        refundInterface.setResponseData(JSON.toJSONString(refundResData));
        boolean result = "SUCCESS".equalsIgnoreCase(refundResData.getResult_code());
        refundInterface.setResult(result);

        dao.insertInterfaceDataByAlipay(refundInterface);
        if(result){
            refund.setNote("[" + UserUtils.getUser().getName() + "]执行微信退款成功！");
            refund.setRefundStatus(Refund.REFUND_STATUS_COMPLETED);
        }else{
            refund.setNote("[" + UserUtils.getUser().getName() + "]执行微信退款失败！");
            refund.setRefundStatus(Refund.REFUND_STATUS_FAILURE);
        }
        confirmRefundCompleted(refund);
    }

    /**
     * 检查退款接口
     * @param refund
     */
    private void checkInterface(Refund refund) {
        RefundInterface history = dao.getInterfaceData(refund.getId());
        if(null != history){
            // 取消历史失败接口请求数据有效性
            dao.cancelHistoryFailInterfaceData(refund.getId());
            //验证该退款是否包含接口退款历史，没有历史或退款失败才能继续请求接口
            if(null != history.getResult() && history.getResult()){
                throw new ServiceException("该退款已成功执行，不能重复执行退款接口！");
            }
        }
    }

    /**
     * 创建退款单
     * @param refund
     */
    @Transactional(readOnly = false)
    public void createRefund(Refund refund) {
        String refundCountString = refund.getSqlMap().get("refundCount");
        String freightPriceString = refund.getSqlMap().get("freightPrice");
        int refundCount = 0;
        BigDecimal freightPrice = BigDecimal.ZERO;
        if(StringUtils.isNotBlank(refundCountString)){
            refundCount = Integer.parseInt(refundCountString);
        }
        if(StringUtils.isNotBlank(freightPriceString)){
            freightPrice = new BigDecimal(freightPriceString);
        }
        // 减去邮费
        refund.setOrderItem(orderItemDao.get(refund.getOrderItem()));
        refund.preInsert();
        refund.setAmount(refund.getOrderItem().getItemPrice().multiply(new BigDecimal(refund.getOrderItem().getQuantity())).add(freightPrice));
        refund.setHasFreightPrice(freightPrice.compareTo(BigDecimal.ZERO) > 0 ? "1" : "0");
        refund.setNote(refund.getCurrentUser().getName() + " 手动创建退款单成功！");
        refund.setReturnStatus(Refund.RETURN_STATUS_UNTREATED);
        refund.setRefundStatus(Refund.REFUND_STATUS_NO_REFUND);

        dao.insert(refund);
        if(refundCount > 0 && freightPrice.compareTo(BigDecimal.ZERO) > 0){
            dao.updateHasFreightPriceRefundAmount(refund.getId());
        }

        refundLogDao.insert(RefundLog.create(refund));
    }

    /**
     * 撤销退款单
     * @param refund
     */
    @Transactional(readOnly = false)
    public void revocation(Refund refund) {
        refund.setNote(refund.getCurrentUser().getName() + "手动撤销退款单成功！");
        dao.revocation(refund);
        refund = get(refund);
        refundLogDao.insert(RefundLog.create(refund));
    }
}
