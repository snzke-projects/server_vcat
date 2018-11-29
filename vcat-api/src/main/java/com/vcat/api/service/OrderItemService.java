package com.vcat.api.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.vcat.common.constant.ApiConstants;
import com.vcat.common.lock.DistLockHelper;
import com.vcat.common.utils.IdGen;
import com.vcat.module.ec.dto.*;
import com.vcat.module.ec.entity.*;
import org.redisson.core.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.OrderItemDao;

@Service
@Transactional(readOnly = true)
public class OrderItemService extends CrudService<OrderItem> {

	@Autowired
	private OrderItemDao            orderItemDao;
	@Autowired
	private ShopService             shopService;
	@Autowired
	private ProductItemService      productItemService;
	@Autowired
	private GroupBuySponsorService  groupBuySponsorService;
	@Autowired
	private GroupBuyCustomerService groupBuyCustomerService;
	@Autowired
	private GroupBuyService         groupBuyService;
	@Override
	protected CrudDao<OrderItem> getDao() {
		return orderItemDao;
	}

	public List<String> findDeliveryerList(String orderNum) {
		return orderItemDao.findDeliveryerList(orderNum);
	}
	@Transactional(readOnly = false)
	public void insertItem(OrderItemDto item) {
		// 根据productItemId获取销售返佣和销售分红价格
		ProductItem productItem = new ProductItem();
		productItem.setId(item.getProductItemId());
		productItem = productItemService.get(productItem);
		item.setSaleEarning(productItem.getSaleEarning());
		item.setBonusEarning(productItem.getBonusEarning());
		if(item.getOrderItemType().equals(ApiConstants.SUPER) || item.getOrderItemType().equals(ApiConstants.GROUPBUY_SELLER)){
			item.setSaleEarning(new BigDecimal(0));
			if(shopService.isVipShop(item.getOrderId())){
				item.setBonusEarning(new BigDecimal(0));
			}
		}
		orderItemDao.insertItem(item);
	}

	public OrderItemDto checkReFund(String orderItemId) {
		return orderItemDao.checkReFund(orderItemId);
	}

	@Transactional(readOnly = false)
	public void deleteById(String orderItemId) {
		orderItemDao.deleteById(orderItemId);
	}
	public List<Map<String,Object>> getPaymentInfo(String paymentId){
		return orderItemDao.getPaymentInfo(paymentId);
	}

	// 1.订单类型是店主在商城中下单
	// 2.订单类型是店主在小店中下单
	// 3.订单类型是买家在小店中下单
	public int getOrderItemType(String productItemId,String orderId){
        return orderItemDao.getOrderItemType(productItemId,orderId);
    }

	/**
	 * 下单时插入团购信息
	 * @param groupBuySponsorId   买家参团时使用
	 * @param groupBuyId		  卖家/买家开团时使用
	 * @param item				  订单详情
	 * @param checkOrderType	  订单类型
     * @param buyer				  下单者
     */
	@Transactional(readOnly = false)
	public void insertGroupBuyInfo(String groupBuySponsorId, String groupBuyId, OrderItemDto item, String checkOrderType,Customer buyer){
		// 卖家开团或买家开团 插入 ec_group_buying_sponsor 和 ec_group_buying_customer
		// 根据 groupBuySponsorId 检查发起人表中是否含有此数据 如果没有 则插入新数据,如果有 则表示参团
		RLock lock      = DistLockHelper.getLock("insertGroupBuyInfo");
		try {
			GroupBuySponsorDto groupBuySponsorInfo = new GroupBuySponsorDto();
			groupBuySponsorInfo.setId(groupBuySponsorId);
			groupBuySponsorInfo = groupBuySponsorService.getGroupBuySponsor(groupBuySponsorInfo);

			int type = ApiConstants.SELLER_LAUNCH;
			if(checkOrderType.equals(ApiConstants.GROUPBUY_BUYER)){
				type = ApiConstants.BUYER_LAUNCH;
			}
			lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);

			if((checkOrderType.equals(ApiConstants.GROUPBUY_SELLER) // 卖家开团
					|| checkOrderType.equals(ApiConstants.GROUPBUY_BUYER)) && groupBuySponsorInfo == null){ // 买家开团
				// 插入发起人信息 ec_group_buying_sponsor
				// 获取GroupBuyDto信息
				GroupBuyDto groupBuyDto = new GroupBuyDto();
				groupBuyDto.setId(groupBuyId);
				groupBuyDto = groupBuyService.getGroupBuy(groupBuyDto);
				groupBuyService.addGroupBuyInfo(groupBuyDto,buyer.getId(),item,type);
			}
			else if(checkOrderType.equals(ApiConstants.GROUPBUY_BUYER) && groupBuySponsorInfo != null && !groupBuySponsorInfo.getLocked()) { // 参团
				// 如果是参团 且有团购信息 且此团购未锁定 则插入一条记录
				GroupBuyCustomerDto groupBuyCustomerDto = new GroupBuyCustomerDto();
				groupBuyCustomerDto.setId(IdGen.uuid());
				groupBuyCustomerDto.setGroupBuySponsorId(groupBuySponsorInfo.getId());
				groupBuyCustomerDto.setOrderId(item.getOrderId());
				groupBuyCustomerDto.setOrderItemId(item.getId());
				groupBuyCustomerDto.setJoinedDate(new Date());
				groupBuyCustomerDto.setCustomerId(buyer.getId());
				groupBuyCustomerDto.setSponsor(false);
				groupBuyCustomerDto.setStatus(0);
				groupBuyCustomerService.insertGroupBuyCustomer(groupBuyCustomerDto);
				// 查询此拼团参加人数(已支付+未支付)
				if(groupBuySponsorInfo.getApplyedCount() + 1 == groupBuySponsorInfo.getGroupBuyDto().getNeededPeople()){
					groupBuySponsorService.lockGroupBuy(groupBuySponsorInfo.getId(),ApiConstants.GROUP_BUY_LOCKED);
				}
			}
		} finally {
			if (lock.isLocked())
				lock.unlock();
		}
	}
}
