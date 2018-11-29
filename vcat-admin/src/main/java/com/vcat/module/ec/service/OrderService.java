package com.vcat.module.ec.service;

import com.vcat.common.express.ExpressUtils;
import com.vcat.common.persistence.Page;
import com.vcat.common.push.PushService;
import com.vcat.common.sms.SmsClient;
import com.vcat.common.utils.*;
import com.vcat.kuaidi100.pojo.TaskResponse;
import com.vcat.module.core.entity.FundOperLog;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.core.service.ServiceException;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.ec.dao.MessageOrderDao;
import com.vcat.module.ec.dao.OrderDao;
import com.vcat.module.ec.dao.ShopFundDao;
import com.vcat.module.ec.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;

/**
 * 订单Service
 */
@Service
@Transactional(readOnly = true)
public class OrderService extends CrudService<OrderDao, Order> {
	@Autowired
	private OrderItemService orderItemService;
    @Autowired
    private OrderLogService orderLogService;
	@Autowired
	private PaymentLogService paymentLogService;
	@Autowired
	private ShippingService shippingService;
	@Autowired
	private MessageOrderDao messageOrderDao;
    @Autowired
    private MessageService messageService;
    @Autowired
    private FundOperLogService fundOperLogService;
    @Autowired
    private ShopFundDao shopFundDao;
    @Autowired
    private ShopInfoService shopInfoService;

	@Override
	public Order get(Order entity) {
		Order order = super.get(entity);
		OrderItem orderItem = new OrderItem();
		orderItem.setOrder(order);
        OrderLog orderLog = new OrderLog();
        orderLog.setOrder(order);
        if(null == order){
            return null;
        }
		PaymentLog paymentLog = new PaymentLog();
		paymentLog.setPayment(order.getPayment());
        order.setItemList(orderItemService.findList(orderItem));
        order.setOrderLogList(orderLogService.findList(orderLog));
        if(null != order.getPayment() && StringUtils.isNotEmpty(order.getPayment().getId())){
            order.setPaymentLogList(paymentLogService.findList(paymentLog));
        }
        productDataScopeFilter(entity, null, "a");
		return order;
	}

	@Override
	public Order get(String id) {
		Order order = new Order();
		order.setId(id);
		return get(order);
	}

	@Override
	public List<Order> findList(Order entity) {
        String productIds = entity.getSqlMap().get("productIds");
        if(StringUtils.isNotBlank(productIds)){
            entity.setProductIdArray(productIds.split("\\|"));
        }
        productDataScopeFilter(entity, null, "a");
		return super.findList(entity);
	}

    public Page<ShipOrder> findShipOrderList(Page<ShipOrder> page, ShipOrder shipOrder){
        String productIds = shipOrder.getSqlMap().get("productIds");
        if(StringUtils.isNotBlank(productIds)){
            shipOrder.setProductIdArray(productIds.split("\\|"));
        }
        productDataScopeFilter(shipOrder, "distribution", null);
        shipOrder.setPage(page);
        List<ShipOrder> list = dao.findShipOrderList(shipOrder);
        page.setList(list);
        return page;
    }

	@Transactional(readOnly = false)
	public void save(Order order) {
		super.save(order);
	}

    /**
     * 确认订单
     * 1.如果所有订单项都为预购订单时则完成订单
     * 2.如果所有订单项商品都是虚拟商品时则完成订单
     * @param order
     * @return
     */
	@Transactional(readOnly = false)
	public Integer confirm(Order order, String batchNo) {
        Integer i = dao.confirm(order);
        Assert.isTrue(i == 1, "确认订单失败，该订单所属团购未结束或该订单退款流程未完结！");
        boolean confirmSucc = null != i && i == 1;
        String batchTitle = StringUtils.isNotBlank(batchNo) ? "批次号[" + batchNo + "]批量" : "";
        Order source = get(order);
        orderLogService.addLog(source, batchTitle + "确认订单", confirmSucc);
        if(confirmSucc){
            int itemSize = source.getItemList().size();
            int reserveSize = 0;
            int virtualProduct = 0;
            for(OrderItem orderItem : source.getItemList()){
                if(orderItem.getOrderType() == Order.ORDER_TYPE_RESERVE){
                    reserveSize++;
                }
                if(orderItem.getProductItem().getProduct().getIsVirtualProduct()){
                    virtualProduct++;
                }
            }
            if(reserveSize > 0 && reserveSize == itemSize){
                dao.completedByReserve(source);
            }else if(virtualProduct > 0 && virtualProduct == itemSize){
                dao.completedOrder(source);
            }
        }
		return i;
	}

	@Transactional(readOnly = false)
	public Integer delivery(Order order, String orderLogNote) {
        Assert.isTrue(dao.isToBeShipping(order), "订单[" + order.getOrderNumber() + "]已发货，不能重复发货！");

        // 删除运单号中的非法字符
        order.getShipping().setNumber(order.getShipping().getNumber().replaceAll("\\W",""));

		order.getShipping().setOrder(order);
		// 保存发货单
		shippingService.save(order.getShipping());
		// 更新订单状态
        dao.delivery(order);
		// 插入订单日志
        orderLogService.addLog(order.getId(), "确认发货", orderLogNote, true);

		// 订阅快递100 推送物流信息服务
        subscribeExpress(order.getShipping().getExpress().getCode(),
                order.getShipping().getNumber(),
                order.getOrderNumber());

        // 保存订单消息
        Order meOrder = get(order);

        OrderItem item = new OrderItem();
        item.setOrder(meOrder);
        List<OrderItem> itemList = orderItemService.findList(item);

        Set<String> shopPhoneSet = new HashSet<>();

        for (int j = 0; j < itemList.size(); j++) {
            OrderItem orderItem = itemList.get(j);

            if(null != orderItem.getShop()){
                shopPhoneSet.add(orderItem.getShop().getCustomer().getPhoneNumber());
            }

            // 如果订单为购买庄园商品，则扣减庄园卡片库存
            if(!orderItem.getProductItem().getProduct().getIsVirtualProduct()
                    && null != orderItem.getShopInfo()
                    && StringUtils.isNotBlank(orderItem.getShopInfo().getId())){
                shopInfoService.subtractInventory(orderItem.getShopInfo().getId());
            }

            // 如果是普通订单或拿样订单则推送订单消息到小店
            if(Order.ORDER_TYPE_NORMAL == orderItem.getOrderType() && false     // 因多个普通订单项(非同一店铺)会导致订单消息发送失败，故暂时取消发送订单消息
//                || Order.ORDER_TYPE_SAMPLE.equals(meOrder.getOrderType().toString())
                    ){
                ThreadPoolUtil.execute(() -> {
                    // 获取用户Token及设备型号(安卓OR苹果)
                    Customer customer = new Customer();
                    customer.setId(orderItem.getShop().getId());
                    // 推送订单消息
                    Map<String,Object> param = new HashMap<>();
                    param.put(PushService.MSG_TYPE, PushService.MSG_TYPE_ORDER);
                    param.putAll(messageService.queryNotReadMessageCount(customer.getId()));
                    String shopMSGContent = "老板，顾客购买的商品已经发货啦！";
                    String pushToken = customer.getPushToken();
                    boolean isAndroid = customer.isAndroid();

                    if(null != customer.getDeviceType() && StringUtils.isNotEmpty(customer.getPushToken())){
                            if(isAndroid){
                                PushService.pushSingleDevice(pushToken, PushService.createMessage(shopMSGContent, param));
                            }else{
                                PushService.pushSingleDevice(pushToken, PushService.createMessageIOS(shopMSGContent,param));
                            }
                        logger.info("PUSH-INFO-推送订单发货消息：推送目标[" + customer.getUserName() + "]推送时间[" + DateUtils.getDate("yyyy-MM-dd HH:mm:ss") + "]" + "标题[" + shopMSGContent + "]参数[" + param + "]");
                    }else{
                        logger.error("推送订单发货消息失败：" + customer.getUserName() + "用户设备类型[" + customer.getDeviceType() + "]或PushToken[" + customer.getPushToken() + "]为空");
                    }
                });

                MessageOrder messageOrder = new MessageOrder();
                messageOrder.preInsert();
                messageOrder.setOrderNumber(meOrder.getOrderNumber());
                messageOrder.setOrderTime(meOrder.getAddDate());
                messageOrder.setType(MessageOrder.TYPE_SHIPPED);
                messageOrder.setPaymentAmount(meOrder.getPayment().getAmount());
                messageOrder.setProductName(meOrder.getProductNames());
                messageOrder.setActualEarning(dao.getOrderEarning(meOrder.getId()));

                // 插入订单消息
                messageOrder.setShop(itemList.get(j).getShop());
                messageOrderDao.insert(messageOrder);
            }
        }

        if(!meOrder.getIsFund()){
            // 处理不可退款订单可提现收入
            handlerCannotRefundOrderSaleEarning(meOrder, itemList);
        }else{
            // 如果订单在发货时已经处理过佣金，则直接改为已完成状态
            dao.completedOrder(meOrder);
        }

        // 发送店铺订单发货短信
        ThreadPoolUtil.execute(() -> {
            if(!shopPhoneSet.isEmpty()
                    && null != order.getShipping()
                    && null != order.getShipping().getExpress()){
                shopPhoneSet.forEach(phone -> {
                    if(IdcardUtils.isPhoneNum(phone)){
                        String tplValue = "#order_no#="+order.getOrderNumber()+"&#express_name#="+order.getShipping().getExpress().getName()+"&#shipping_no#="+order.getShipping().getNumber();
                        SmsClient.tplSendSms(SmsClient.TPL_SHOP_SHIPPED, tplValue, phone);
                    }else{
                        logger.warn("发送店铺订单发货短信失败：店铺手机号不正确[" + phone + "]");
                    }
                });
            }else{
                logger.warn("发送店铺订单发货短信失败：该订单不是店铺订单或收货信息不完整");
            }
        });

        // 发送订单发货短信
        ThreadPoolUtil.execute(() -> {
            if (null != meOrder.getAddress()
                    && IdcardUtils.isPhoneNum(meOrder.getAddress().getDeliveryPhone())
                    && null != order.getShipping()
                    && null != order.getShipping().getExpress()) {
                String tplValue = "#name#=" + meOrder.getAddress().getDeliveryName() + "&#order_no#=" + order.getOrderNumber() + "&#express_name#=" + order.getShipping().getExpress().getName() + "&#shipping_no#=" + order.getShipping().getNumber();
                SmsClient.tplSendSms(SmsClient.TPL_SHIPPED, tplValue, meOrder.getAddress().getDeliveryPhone());
            } else {
                logger.warn("向" + meOrder.getAddress().getDeliveryName() + " 发送发货短信失败：收货信息不完整或为空");
            }
        });

		return 1;
	}

    private void subscribeExpress(String code,String number,String orderNumber){
        ThreadPoolUtil.execute(() -> {
            TaskResponse tr = ExpressUtils.subscribeExpress(code, number);
            logger.info("订单[" + orderNumber + "]订阅物流信息完成：" + tr.toString());
        });
    }

    @Transactional(readOnly = false)
    public void batchConfirm(String[] orderIds) {
        String batchNo = IdGen.getRandomNumber(6);
        for(String id : orderIds){
            Order order = new Order();
            order.setId(id);
            int i = confirm(order, batchNo);
            if(i == 0){
                throw new ServiceException("订单[" + id + "]确认失败，请单独确认订单！");
            }
        }
    }

    @Transactional(readOnly = false)
    public void updateShipping(Order order) {
        shippingService.save(order.getShipping());

        // 重新订阅物流信息
        subscribeExpress(order.getShipping().getExpress().getCode(),
                order.getShipping().getNumber(),
                order.getOrderNumber());
    }

    /**
     * 结算团队分红
     */
    @Transactional(readOnly = false)
    public void settlementTeamBonus() {
        List<Map<String,Object>> shopList = dao.getPendingSettlementShopList();
        if(shopList == null || shopList.isEmpty()){
            logger.info("团队分红待结算数据为空，退出结算团队分红");
            return;
        }
        Set<String> orderIdSet = new HashSet<>();
        shopList.forEach((Map<String,Object> shopMap)->{
            String shopId = shopMap.get("shopId").toString();
            String consumerId = shopMap.get("consumerId").toString();
            String consumerName = shopMap.get("consumerName").toString();
            BigDecimal bouns = new BigDecimal(shopMap.get("bouns").toString());
            String orderIds = shopMap.get("orderIds").toString();
            if(StringUtils.isNotBlank(orderIds)){
                orderIdSet.addAll(Arrays.asList(orderIds.split(",")));
            }

            pushEarningMsg(shopId, consumerName, bouns);

            updateBonus(consumerId, shopId, consumerName, bouns);
        });

        //更新团队分红结算标识
        dao.settlementTeamBonusOrder(orderIdSet);
        logger.info("结算团队分红完成，共结算[" + orderIdSet.size() + "]个订单");
    }

    /**
     * 更新团队分红资金表
     * @param consumerId
     * @param shopId
     * @param consumerName
     * @param bouns
     */
    @Transactional(readOnly = false)
    private void updateBonus(String consumerId, String shopId, String consumerName, BigDecimal bouns) {
        ShopFund shopFund = shopFundDao.get(shopId);
        shopFundDao.settlementTeamBonus(shopId, bouns);
        fundOperLogService.insert(shopFund.getId(), FundFieldType.BONUS_AVAILABLE_FUND, bouns, shopFund.getBonusAvailableFund().add(bouns), consumerId, FundOperLog.SETTLEMENT_TEAM_BONUS, "结算团队成员[" + consumerName + "]团队分红成功，增加分红可提现资金[" + bouns + "]");
    }

    /**
     * 推送团队分红财富消息
     * @param shopId
     * @param consumerName
     * @param bouns
     */
    @Transactional(readOnly = false)
    private void pushEarningMsg(String shopId, String consumerName, BigDecimal bouns) {
        MessageEarning messageEarning = new MessageEarning();
        Shop shop = new Shop();
        shop.setId(shopId);
        messageEarning.setType(MessageEarning.TYPE_TEAM_BONUS);
        messageEarning.setConsumer(consumerName);
        messageEarning.setEarning(bouns);
        messageEarning.setShop(shop);
        messageService.pushEarningMsg(messageEarning);
    }

    /**
     * 修改收货地址
     * @param address
     */
    @Transactional(readOnly = false)
    public void modifyAddress(Address address) {
        Order order = new Order();
        order.setId(address.getId());
        dao.modifyAddress(address);
        StringBuffer note = new StringBuffer();
        note.append(UserUtils.getUser().getName());
        note.append("将原地址[");
        note.append(address.getSqlMap().get("oldAddress"));
        note.append("]改为[");
        note.append(address.getDeliveryName() + " ");
        note.append(address.getDeliveryPhone() + " ");
        note.append(address.getProvince() + " ");
        note.append(address.getCity() + " ");
        note.append(address.getDistrict() + " ");
        note.append(address.getDetailAddress());
        note.append("]");
        orderLogService.addLog(address.getId(), "修改收货地址", note.toString(), true);
    }

    public Page<FarmOrder> findFarmOrderList(Page<FarmOrder> page, FarmOrder farmOrder) {
        farmOrder.setPage(page);
        if(StringUtils.isNotBlank(farmOrder.getProductIds())){
            farmOrder.setProductIdArray(farmOrder.getProductIds().split("\\|"));
        }
        List<FarmOrder> list = dao.findFarmOrderList(farmOrder);
        page.setList(list);
        return page;
    }

    /**
     * 根据发货单发货
     * @param invoices
     */
    @Transactional(readOnly = false)
    public void deliveryByImportInvoices(Invoices invoices) {
        Order order;
        if(StringUtils.isNotBlank(invoices.getOrderNumber())){
            Order queryOrder = new Order();
            String orderNumber = invoices.getOrderNumber();
            orderNumber = orderNumber.trim().replaceAll("\\r|\\n|\\t","");
            queryOrder.setOrderNumber(orderNumber);
            order = get(queryOrder);
            Assert.notNull(order, "未找到订单号为[" + invoices.getOrderNumber() + "]的待发货订单");
            delivery(invoices, order);
        }else if(StringUtils.isNotBlank(invoices.getProductName())
                && StringUtils.isNotBlank(invoices.getDeliveryName())
                && StringUtils.isNotBlank(invoices.getDeliveryPhone())
                && StringUtils.isNotBlank(invoices.getDeliveryAddress())){
            order = dao.getOrderByInvoices(invoices);
            Assert.notNull(order, "未找到收件人为[" + invoices.getDeliveryName() + "|" + invoices.getDeliveryPhone() + "]的待发货订单");
            delivery(invoices, order);
        }else{
            throw new IllegalArgumentException("发货单信息不完整，发货失败！");
        }
    }

    /**
     * 根据发货单和订单发货
     * @param invoices
     * @param order
     */
    @Transactional(readOnly = false)
    private void delivery(Invoices invoices, Order order) {
        String note = UserUtils.getUser().getName() + "导入发货单[" + invoices.getExpress().getName() + "][" + invoices.getShippingNo() + "]成功";
        Shipping shipping = new Shipping();
        shipping.setOrder(order);
        shipping.setExpress(invoices.getExpress());
        shipping.setFreightCharge(order.getFreightPrice());
        shipping.setNumber(invoices.getShippingNo().trim().replaceAll("\\r|\\n|\\t",""));
        shipping.setShippingDate(new Date());
        shipping.setNote(note);
        order.setShipping(shipping);
        delivery(order, note);
    }

    /**
     * 确认全部订单
     * @param order
     */
    @Transactional(readOnly = false)
    public void confirmAllOrder(Order order) {
        String batchNo = IdGen.getRandomNumber(7);
        findList(order).forEach(o -> confirm(o, batchNo));
    }

    /**
     * 处理不可退款商品订单可提现销售收入
     * @param order
     * @param orderItemList
     */
    private void handlerCannotRefundOrderSaleEarning(Order order, List<OrderItem> orderItemList) {
        // 如该订单第一个订单项为不可退款商品，则视为整个订单为不可退款订单
        if(!orderItemList.get(0).getCanRefund() && !order.getIsFund()) {
            for (OrderItem orderItem : orderItemList) {
                // 如果该订单包含待处理退款单，则不能处理佣金
                Assert.isTrue(orderItem.getRefund() == null || orderItem.getRefund().getIsFinish(), "该订单包含待处理退款单，发货失败！");

                // 获取待结算商品数量，减去退款成功数量
                Integer refundQuantity = 0;
                if (null != orderItem.getRefund() && Refund.REFUND_STATUS_COMPLETED.equals(orderItem.getRefund().getRefundStatus())) {
                    refundQuantity = orderItem.getRefundCount();
                }
                BigDecimal earningQuantity = new BigDecimal(orderItem.getQuantity() - refundQuantity);

                // 处理不可退款订单销售佣金
                if (orderItem.getSaleEarning().compareTo(BigDecimal.ZERO) == 1) {
                    BigDecimal saleEarning = orderItem.getSaleEarning();
                    if (orderItem.getShop().getAdvancedShop() == 1) {
                        saleEarning = saleEarning.add(orderItem.getBonusEarning());
                    }
                    BigDecimal subtractSaleHold = saleEarning.multiply(earningQuantity);

                    ShopFund shopFund = shopFundDao.get(orderItem.getShop().getId());

                    // 修改金额
                    shopFundDao.handlerSaleEarning(orderItem.getShop().getId(), subtractSaleHold);

                    // 插入日志
                    fundOperLogService.insert(orderItem.getShop().getId(), FundFieldType.SALE_HOLD_FUND, subtractSaleHold, shopFund.getSaleHoldFund().subtract(subtractSaleHold), order.getId(), FundOperLog.NORMAL_INCOME, "不可退款订单[" + order.getOrderNumber() + "]发货成功，减去销售待确认资金[" + subtractSaleHold + "]");
                    fundOperLogService.insert(orderItem.getShop().getId(), FundFieldType.SALE_AVAILABLE_FUND, subtractSaleHold, shopFund.getSaleAvailableFund().add(subtractSaleHold), order.getId(), FundOperLog.NORMAL_INCOME, "不可退款订单[" + order.getOrderNumber() + "]发货成功，增加销售可提现资金[" + subtractSaleHold + "]");

                    // 推送财富消息
                    messageService.pushEarningMsg(MessageEarning.create(orderItem.getShop(), null, subtractSaleHold,
                            orderItem.getProductItem().getProduct().getName(), earningQuantity.intValue(), order.getOrderNumber(), MessageEarning.TYPE_SALE));
                }

                // 处理非钻石小店上家分红奖励
                if(orderItem.getShop().getAdvancedShop() != 1
                        && null != orderItem.getParent()
                        && orderItem.getBonusEarning().compareTo(BigDecimal.ZERO) == 1){
                    BigDecimal subtractBonusHold = orderItem.getBonusEarning().multiply(earningQuantity);

                    ShopFund shopFund = shopFundDao.get(orderItem.getParent().getId());

                    // 修改金额
                    shopFundDao.handlerBonusEarning(orderItem.getParent().getId(), subtractBonusHold);

                    // 插入日志
                    fundOperLogService.insert(orderItem.getParent().getId(), FundFieldType.BONUS_HOLD_FUND, subtractBonusHold, shopFund.getBonusHoldFund().subtract(subtractBonusHold), order.getId(), FundOperLog.NORMAL_INCOME, "下级不可退款订单[" + order.getOrderNumber() + "]发货成功，减去分红待确认资金[" + subtractBonusHold + "]");
                    fundOperLogService.insert(orderItem.getParent().getId(), FundFieldType.BONUS_AVAILABLE_FUND, subtractBonusHold, shopFund.getBonusAvailableFund().add(subtractBonusHold), order.getId(), FundOperLog.NORMAL_INCOME, "下级不可退款订单[" + order.getOrderNumber() + "]发货成功，增加分红可提现资金[" + subtractBonusHold + "]");

                    // 推送财富消息
                    messageService.pushEarningMsg(MessageEarning.create(orderItem.getParent(), orderItem.getShop().getName(), subtractBonusHold,
                            orderItem.getProductItem().getProduct().getName(), earningQuantity.intValue(), order.getOrderNumber(), MessageEarning.TYPE_BONUS));
                }
            }
            // 更新订单佣金处理状态
            dao.updateIsFund(order.getId());
        }
    }

    /**
     * 修改订单备注
     * @param order
     */
    @Transactional(readOnly = false)
    public void updateNote(Order order) {
        String oldNote = order.getSqlMap().get("oldNote");
        String logNote = order.getCurrentUser().getName();
        if(StringUtils.isNotBlank(oldNote) && StringUtils.isBlank(logNote)){
            logNote += " 清除原备注[" + oldNote + "]";
        }else if(StringUtils.isNotBlank(oldNote)){
            logNote += " 将订单备注[" + oldNote + "]修改为[" + order.getNote() + "]";
        }else{
            logNote += " 新增订单备注[" + order.getNote() + "]";
        }
        orderLogService.addLog(order.getId(), "修改订单备注", logNote, true);
        dao.updateNote(order);
    }
}
