package com.vcat.api.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import com.easemob.server.httpclient.api.EasemobIMUsers;
import com.vcat.common.constant.ApiMsgConstants;

import com.vcat.common.sms.SmsClient;
import com.vcat.common.utils.SortUtils;
import com.vcat.module.core.utils.DictUtils;
import com.vcat.module.ec.dto.*;
import com.vcat.module.ec.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.CustomerDao;
import com.vcat.module.ec.dao.ShopDao;
import sun.security.provider.SHA;


@Service
@Transactional(readOnly = true)
public class ShopService extends CrudService<Shop> {

	@Autowired
	private ShopDao shopDao;
	@Autowired
	private MonthRankService monthRankService;
	@Autowired
	private AllRankService allRankService;
	@Autowired
	private CustomerDao customerDao;
	@Autowired
	private ShopFundService shopFundService;
	@Autowired
	private CouponFundService couponFundService;
	@Autowired
	private CouponService couponService;
	@Autowired
	private CouponShopService couponShopService;
	@Autowired
	private CouponOperLogService couponOperLogService;
	@Autowired
	private MessageService messageService;
    @Autowired
    private ShopProductService shopProductService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderLogService orderLogService;
	@Autowired
	private ShopInfoService shopInfoService;

	public List<String> getAllFriends(String cid){
		List<String> huanids = shopDao.getFriendsById(cid);
		Shop shop = get(cid);
		huanids.add(shop.getParentId());
		return huanids;
	}

	@Override
	protected CrudDao<Shop> getDao() {
		return shopDao;
	}

	// 计算总数
	public int count() {
		return shopDao.count();
	}

	// 获取前一百的月收入排行
	public List<Rank> getMonthRankList() {
		return shopDao.getMonthRankList();
	}

	// 获取收入排行，并分页
	public List<Rank> getFundRankList(Pager page, String rankType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page);
		List<Rank> rank = null;
		// 获取所有收入排行
		if (ApiConstants.RANK_TYPE_ALL.equals(rankType)) {

			rank = allRankService.getAllFundRankList(map);

			// 获取当月排行
		} else if (ApiConstants.RANK_TYPE_MONTH.equals(rankType)) {

			rank = monthRankService.getMonthFundRankList(map);

		}
		return rank;
	}

	// 将新建的用户和所有商品关联
	@Transactional(readOnly = false)
	public void insertProducts(String shopId) {
		shopDao.insertProducts(shopId);
	}

	// 将新建的用户和所有背景图片关联
	@Transactional(readOnly = false)
	public void insertBgImages(String shopId) {
		shopDao.insertBgImages(shopId);
	}

	// 获取店铺买家数量
	public int countBuyerList(String shopId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("shopId", shopId);
		return shopDao.countBuyerList(map);
	}

	// 获取店铺买家信息
	public List<BuyerInfoDto> getBuyerList(String shopId, String rankType,
			Pager page) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("shopId", shopId);
		map.put("rankType", rankType);
		map.put("page", page);
		return shopDao.getBuyerList(map);
	}

	public void updateLevel(String shopId, int saledWith) {
		shopDao.updateLevel(shopId, saledWith);
	}

	@Transactional(readOnly = false)
	public void updateParant(Shop shop) {
		shopDao.update(shop);
		//环信添加好友关系
		EasemobIMUsers.addFriendSingle(shop.getId(), shop.getParentId());
		//取消猫币
//		Shop parentShop = new Shop();
//		parentShop.setId(shop.getParentId());
//
////		if(!couponShopService.canInsertCoupon(shop.getId())){
////			logger.debug("已经获得过优惠券，跳过添加V猫币");
////			return;
////		}
//
//		// 现在使用邀请劵的方式
//		// 先获取上家 下家 的邀请劵收入
//		Coupon cou = new Coupon();
//		cou.setType(ApiConstants.COUPON_TYPE_INVITER);
//		// 上家邀请劵
//		Coupon couInviter = couponService.get(cou);
//		cou.setType(ApiConstants.COUPON_TYPE_INVITEE);
//		// 下家邀请劵
//		Coupon couInvitee = couponService.get(cou);
//		if (couInviter != null && couInvitee != null) {
//			// 判断下家是否已获得过邀请劵
//			CouponShop couponShop = new CouponShop();
//			couponShop.setShop(shop);
//			couponShop.setCoupon(couInvitee);
//			// 防止同步出错
//			CouponShop cs = null;
//			RLock lock = DistLockHelper.getLock("updateParant");
//			try {
//				lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
//				cs = couponShopService.get(couponShop);
//				if (cs != null) {
//					logger.debug("已经获得过下家优惠券，跳过添加V猫币");
//					return;
//				}
//				// 当下家没有获取过邀请劵
//				Customer customer = customerDao.select(shop.getId());
//				Customer parentCustomer = customerDao
//						.select(parentShop.getId());
//				// 操作上家劵金额
//				if (couInviter.getTotal() > couInviter.getCouponUsedCount()) {
//					BigDecimal couInviterFund = couInviter.getFund();
//					if (couInviterFund != null
//							&& couInviterFund.compareTo(BigDecimal.ZERO) > 0) {
//						CouponFund parentCouponFund = new CouponFund();
//						parentCouponFund.setShop(parentShop);
//						// 如果第一次增加
//						CouponFund cFund = couponFundService
//								.get(parentCouponFund);
//						if (cFund == null) {
//							parentCouponFund.preInsert();
//							parentCouponFund.setAvailableFund(couInviterFund);
//							parentCouponFund.setUsedFund(BigDecimal.ZERO);
//							couponFundService.insert(parentCouponFund);
//						}
//						// 增加上家劵金额
//						else {
//							couponFundService.addCoupon(parentShop.getId(),
//									couInviterFund);
//						}
//						// 记录日志
//						CouponOperLog log = new CouponOperLog();
//						log.preInsert();
//						if (cFund == null) {
//							log.setCouponFundId(parentCouponFund.getId());
//							log.setRemainFund(couInviterFund);
//						} else {
//							log.setCouponFundId(cFund.getId());
//							log.setRemainFund(cFund.getAvailableFund().add(
//									couInviterFund));
//						}
//						log.setFund(couInviterFund);
//						if (parentCustomer != null)
//							log.setNote("上家[" + parentCustomer.getUserName()
//									+ "]获得[" + couInviterFund + "]邀请劵");
//						log.setType(ApiConstants.NORMAL);
//						log.setCouponId(couInviter.getId());
//						couponOperLogService.insert(log);
//						// 发送推送消息
//						MessageEarning inviterEarningMsg = new MessageEarning();
//						inviterEarningMsg.setShop(parentShop);
//						inviterEarningMsg.setType(MessageEarning.TYPE_INVITE);
//						inviterEarningMsg.setConsumer(customer == null ? null
//								: customer.getUserName());
//						inviterEarningMsg.setEarning(couInviterFund);
//						messageService.pushEarningMsgTask(inviterEarningMsg);
//
//						// 记录couponShop
//						couponShop.preInsert();
//						couponShopService.insert(couponShop);
//					}
//
//				}
//				// 操作下家劵金额
//				if (couInvitee.getTotal() > couInvitee.getCouponUsedCount()) {
//					BigDecimal couInviteeFund = couInvitee.getFund();
//					if (couInviteeFund != null
//							&& couInviteeFund.compareTo(BigDecimal.ZERO) > 0) {
//						// 增加下家邀请劵金额
//						CouponFund couponFund = new CouponFund();
//						couponFund.setShop(shop);
//						// 如果第一次增加
//						CouponFund conFund = couponFundService.get(couponFund);
//						if (conFund == null) {
//							couponFund.preInsert();
//							couponFund.setAvailableFund(couInviteeFund);
//							couponFund.setUsedFund(BigDecimal.ZERO);
//							couponFundService.insert(couponFund);
//
//						} else {
//							couponFundService.addCoupon(shop.getId(),
//									couInviteeFund);
//						}
//						CouponOperLog log = new CouponOperLog();
//						log.preInsert();
//						if (conFund == null) {
//							log.setCouponFundId(couponFund.getId());
//							log.setRemainFund(couInviteeFund);
//						} else {
//							log.setCouponFundId(conFund.getId());
//							log.setRemainFund(conFund.getAvailableFund().add(
//									couInviteeFund));
//						}
//						log.setFund(couInviteeFund);
//						if (customer != null)
//							log.setNote("下家[" + customer.getUserName() + "]获得["
//									+ couInviteeFund + "]邀请劵");
//						log.setType(ApiConstants.SUPER);
//						log.setCouponId(couInvitee.getId());
//						couponOperLogService.insert(log);
//						// 发送推送消息
//						MessageEarning inviteeEarningMsg = new MessageEarning();
//						inviteeEarningMsg.setShop(shop);
//						inviteeEarningMsg.setType(MessageEarning.TYPE_TEAM);
//						inviteeEarningMsg
//								.setConsumer(parentCustomer == null ? null
//										: parentCustomer.getUserName());
//						inviteeEarningMsg.setEarning(couInviteeFund);
//						messageService.pushEarningMsgTask(inviteeEarningMsg);
//						// 记录couponShop
//						CouponShop parentCouponShop = new CouponShop();
//						parentCouponShop.preInsert();
//						parentCouponShop.setShop(parentShop);
//						parentCouponShop.setCoupon(couInviter);
//						couponShopService.insert(parentCouponShop);
//					}
//				}
//			} finally {
//				if (lock.isLocked())
//					lock.unlock();
//			}
//		}
	}

	// 获取我的下家数量
	public int countChildsList(String shopId) {

		return shopDao.countChildsList(shopId);
	}
	//更新店铺状态
	@Transactional(readOnly = false)
	public void updateAdvanced(String shopId,int type){
		shopDao.updateAdvanced(shopId,type);
		//throw new RuntimeException("test error");
	}
	// 获取我下家的列表
	public List<ChildCustomerDto> getChildList(String shopId, Pager page) {

		return shopDao.getChildList(shopId, page);
	}

	public int getShopCount(int count) {
		Shop parentShop = new Shop();
		Level level = new Level();
		parentShop.setLevel(level);
		parentShop.setShopNum(count+"");
		//判断shopNum是否有对应的店铺
		Shop pShop = shopDao.get(parentShop);
		if(pShop!=null){
			count = getShopCount(count+1);
		}else{
			return count;
		}
		return count;
	}

	public List<Rank> operationRank(List<Rank> rankList,List<String> phoneNumList) {
		
		for (int i=0;i<phoneNumList.size();i++) {
			boolean flag = false;
			Rank rank = shopDao.getRankByPhone(phoneNumList.get(i));
			if (rank == null) {
				continue;
			}
			for (Rank r : rankList) {
				if (rank.getShopId().equals(r.getShopId())) {
					r.setTotalFund(new BigDecimal(rank.getTotalFund())
							.add(new BigDecimal(r.getTotalFund())) + "");
					flag = true;
					break;
				}
			}
			if(!flag){
				rankList.add(rank);
			}
	
		}
		Set<Rank> set = new TreeSet<Rank>(rankList);
		int index = 1;
		Iterator<Rank> iterator = set.iterator();
		while (iterator.hasNext()) {
			iterator.next().setRank(index);
			index++;
		}
		return new ArrayList<Rank>(set);
	}

	public Shop getShopByInviteCode(String inviteCode){
		return shopDao.getShopByInviteCode(inviteCode);
	}

	public String getInviteCode(String id){
		return shopDao.getInviteCode(id);
	}
    //店铺的总销售额
	public BigDecimal getTotalSale(String shopId){
		return shopDao.getTotalSale(shopId) == null ? new BigDecimal(0) : shopDao.getTotalSale(shopId);
	}
    //根据支付单号查询所有的店铺ID
    public List<Map<String,Object>> getAllShopByPayment(String paymentId) {
        return shopDao.getAllShopByPayment(paymentId);
    }
    //根据支付单号查询所有的订单项
    public Map<String,String> getProductItemsByPayment(String paymentId){
        return shopDao.getProductItemsByPayment(paymentId);
    }
	public ProductItemDto getReserveProductItemByPayment(String paymentId){
        return shopDao.getReserveProductItemByPayment(paymentId);
    }
    public Map<String,String> getReserveProductItemByItemId(String orderItemId){
        return shopDao.getReserveProductItemByItemId(orderItemId);
    }
    public List<Map<String,Object>> getBuyersByPaymentId(String paymentId,String upgradeConditionId){
        return shopDao.getBuyersByPaymentId(paymentId, upgradeConditionId);
    }
    public Map<String,Object> isSpecialProduct(String paymentId){
        return shopDao.isSpecialProduct(paymentId);
    }
    public Boolean isReach(String buyerId, Date paymentDate){
        return shopDao.isReach(buyerId,paymentDate);
    }

    //升级新需求,支付成功后,检查订单项是否有特定商品,如果有,根据规则计算24小时之内的所有已支付订单中在特殊商品列表中的商品项中的总额是否达到标准
    //检查是否发送个人推送
	@Transactional(readOnly = false)
	public Boolean isVipShop(String orderId){
		return shopDao.isVipShop(orderId);
	}



	public int countChildsIsVIP(Map<String, Object> params){
		return shopDao.countChildsIsVIP(params).size();
	}

	public List<AgentShopDto> getChildListIsVIP(Map<String, Object> params, Pager page){
        return shopDao.getChildListIsVIP(params,page);
    }

	public int countChildsNotVIP(Map<String, Object> params){
		return shopDao.countChildsNotVIP(params).size();
	}

	public List<AgentShopDto> getChildListNotVIP(Map<String, Object> params , Pager page){
        return shopDao.getChildListNotVIP(params,page);
	}

	public int countGrandChilds(Map<String, Object> params){
		return shopDao.countGrandChilds(params).size();
	}

	public List<AgentShopDto> getGrandChildList(Map<String, Object> params,  Pager page){
        return shopDao.getGrandChildList(params,page);
	}

	public AgentShopDto getVIPInfo(String shopId){
	    return shopDao.getVIPInfo(shopId);
    }
	public AgentShopDto getNotVIPInfo(String shopId){
		return shopDao.getNotVIPInfo(shopId);
	}
    public AgentShopDto getGrandVIPInfo(String shopId){
        return shopDao.getGrandVIPInfo(shopId);
    }

    private List<AgentShopDto> getChildList(Map<String, Object> params,int type,Pager page){
        List<String> shopIdList = null;
        if(type == 0){
            shopIdList = shopDao.countChildsIsVIP(params);
        }else if(type == 1){
            shopIdList = shopDao.countChildsNotVIP(params);
        }else{
            shopIdList = shopDao.countGrandChilds(params);
        }
        List<AgentShopDto> agentShopDtos = new ArrayList<>();
        for(String shopId : shopIdList){
            if (type == 0) {
                agentShopDtos.add(shopDao.getVIPInfo(shopId));
            }else if(type == 1){
                agentShopDtos.add(shopDao.getNotVIPInfo(shopId));
            }else if(type == 2){
                agentShopDtos.add(shopDao.getGrandVIPInfo(shopId));
            }
        }
        // 排序
        agentShopDtos.sort(new Comparator<AgentShopDto>() {
            public int compare(AgentShopDto o1, AgentShopDto o2) {
                if (o2.getTotalFund().compareTo(o1.getTotalFund()) == 0) {
                    if (params.get("monthOrderType").equals("desc")) {
                        return o2.getMonthTotalFund().compareTo(o1.getMonthTotalFund());
                    } else {
                        return o1.getMonthTotalFund().compareTo(o2.getMonthTotalFund());
                    }
                } else {
                    if (params.get("allOrderType").equals("desc")) {
                        return o2.getTotalFund().compareTo(o1.getTotalFund());
                    } else {
                        return o1.getTotalFund().compareTo(o2.getTotalFund());
                    }
                }
            }

        });
        // 手动分页
        List<AgentShopDto> result = new ArrayList<>();
        for(int i = (page.getPageNo() - 1) * page.getPageSize(), j = 0; i < agentShopDtos.size() && j < page.getPageSize(); i++,j++){
            result.add(agentShopDtos.get(i));
        }
        return result;
    }
	public int getShopStatus(String shopId){
    	return shopDao.getShopStatus(shopId);
	}
	public AgentShopDto getShopInfo(String shopId){
		return shopDao.getShopInfo(shopId);
	}
}
