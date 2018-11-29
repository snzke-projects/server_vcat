package com.vcat.api.service;

import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.service.CrudService;
import com.vcat.module.core.entity.FundOperLog;
import com.vcat.module.core.entity.User;
import com.vcat.module.ec.dao.ShopFundDao;
import com.vcat.module.ec.dto.FundDetailBillDto;
import com.vcat.module.ec.dto.ShopFundDto;
import com.vcat.module.ec.dto.ShopFundExpandDto;
import com.vcat.module.ec.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@Transactional(readOnly = true)
public class ShopFundService extends CrudService<ShopFund> {
	@Autowired
	private ShopFundDao shopFundDao;
	@Autowired
	private MonthRankService monthRankService;
	@Autowired
	private AllRankService allRankService;
	@Autowired
	private WithdrawalService withdrawalService;
	@Autowired
	private FundOperLogService fundOperLogService;
	@Override
	protected CrudDao<ShopFund> getDao() {
		return shopFundDao;
	}
	public ShopFund getShopFund(String shopId) {
		return shopFundDao.getShopFund(shopId);
	}
	public ShopFundExpandDto getShopFund2(String shopId) {
		return shopFundDao.getShopFund2(shopId);
	}
	//获取总详单list
	public List<FundDetailBillDto> getShopDetailBill(Pager page, String shopId,
			String month) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page);
		map.put("shopId", shopId);
		map.put("month", month);
		return shopFundDao.getShopDetailBill(map);
	}
	//获取总详单count
	public int getShopDetailBillCount(String month, String shopId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("shopId", shopId);
		map.put("month", month);
		return shopFundDao.getShopDetailBillCount(map);
	}
	//获取收入排行总数，根据排行类型
	public int count(String rankType) {
		 Integer count = null ;
		// 获取所有收入排行数量
		if (ApiConstants.RANK_TYPE_ALL.equals(rankType)) {

			count = allRankService.count();

			// 获取当月排行数量
		} else if (ApiConstants.RANK_TYPE_MONTH.equals(rankType)) {

			count  = monthRankService.count();

		}
		return count==null?0:count;
	}
	//获取销售账单总数
	public int getShopSaleDetailBillCount(String month, String shopId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("shopId", shopId);
		map.put("month", month);
		return shopFundDao.getShopSaleDetailBillCount(map);
	}
	//获取销售账单list
	public List<FundDetailBillDto> getShopSaleDetailBill(Pager page,
			String shopId, String month) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page);
		map.put("shopId", shopId);
		map.put("month", month);
		return shopFundDao.getShopSaleDetailBill(map);
	}
	//获取总排行前100
	public List<Rank> getRankList() {
		
		return shopFundDao.getRankList();
	}

	// 获取我的排行位置
	public String getMyMonthRank(String shopId) {
		Integer rank = monthRankService.getMyMonthRank(shopId);
		return rank == null ? "0" : rank.toString();
	}
	//提现申请操作
	@Transactional(readOnly = false)
	public Object withdrawCash(String shopId, String bankId, BigDecimal fundCash) {
		Map<String, Object> map = new HashMap<>();
		//查询我的收入
		ShopFund shopFund = shopFundDao.select(shopId);
		if(shopFund==null){
			return false;
		}
		BigDecimal zero = BigDecimal.ZERO;
		//按照销售、代理、分享的顺序依次递减提现
		/* 判断要提现金额所在的区间
            0 表示大于总可用收入
            1 表示小于或等于销售可用收入
            2 表示大于销售可用收入，且小于或等于总可用收入
            3 表示大于销售+上架可用收入，且小于或等于总可用收入
		    4 表示大于销售+上架+分享可用收入，且小于或等于总可用收入
		    5 表示大于销售+上架+分享+分红可用收入，且小于或等于总可用收入
		 */
		int status = getFundStatus(shopFund,fundCash);
		//操作金钱
		return cash(shopId, bankId, fundCash, map, shopFund, zero, status);
	}

	@Transactional(readOnly = false)
	private Object cash(String shopId, String bankId, BigDecimal fundCash,
			Map<String, Object> map, ShopFund shopFund, BigDecimal zero,
			int status) {
		switch (status) {
		case 0: {
			map.put(ApiConstants.MSG, ApiMsgConstants.CASH_MORETHAN_FUND);
			map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
			map.put("status", ApiConstants.NO);
			return map;
		}
		case 1: {
			// 修改总收入表
			BigDecimal newSaleAvailAbleFund = shopFund.getSaleAvailableFund().subtract(fundCash);
			shopFund.setSaleAvailableFund(newSaleAvailAbleFund);
			shopFund.setSaleProcessingFund(fundCash);
			shopFundDao.update(shopFund);

			// 插入提现申请表
			Withdrawal withdrawal = new Withdrawal();
			withdrawal.preInsert();
			withdrawal.setShopId(shopFund.getId());
			// 创建操作者id为卖家
			User user = new User();
			user.setId(shopFund.getId());
			withdrawal.setWithdrawalStatus(ApiConstants.WITHDRAW_STATUS_NOT_CONFIRMED);
			withdrawal.setCreateBy(user);
			withdrawal.setAmount(fundCash);
			withdrawal.setShopAccountId(bankId);
			withdrawalService.insert(withdrawal);

			// 插入销售资金操作记录表
            insertSaleLog(shopId, shopFund, zero, withdrawal);

            break;
		}
		case 2: {
			// 修改总收入表
			shopFund.setSaleProcessingFund(shopFund.getSaleAvailableFund());
			shopFund.setSaleAvailableFund(new BigDecimal(0));
			// 设置代理提现中金额为fundcash -减去销售可提现
			shopFund.setLoadProcessingFund(fundCash.subtract(shopFund.getSaleProcessingFund()));
			shopFund.setLoadAvailableFund(shopFund.getLoadAvailableFund().subtract(shopFund.getLoadProcessingFund()));
			shopFundDao.update(shopFund);
			// 插入提现申请表
			Withdrawal withdrawal = new Withdrawal();
			withdrawal.preInsert();
			withdrawal.setShopId(shopFund.getId());
			// 创建操作者id为卖家
			User user = new User();
			user.setId(shopFund.getId());
			withdrawal.setWithdrawalStatus(ApiConstants.WITHDRAW_STATUS_NOT_CONFIRMED);
			withdrawal.setCreateBy(user);
			withdrawal.setAmount(fundCash);
			withdrawal.setShopAccountId(bankId);
			withdrawalService.insert(withdrawal);

			// 插入资金操作记录表
            insertLoadLog(shopId, shopFund, zero, withdrawal);
            insertSaleLog(shopId, shopFund, zero, withdrawal);

            break;
		}
		case 3: {
			// 修改总收入表
			// 修改销售收入
			shopFund.setSaleProcessingFund(shopFund.getSaleAvailableFund());
			shopFund.setSaleAvailableFund(new BigDecimal(0));
			// 修改代理奖励
			shopFund.setLoadProcessingFund(shopFund.getLoadAvailableFund());
			shopFund.setLoadAvailableFund(new BigDecimal(0));
			// 设置分析提现中金额为fundcash -减去销售可提现-减去代理可提现
			shopFund.setShareProcessingFund(fundCash
                    .subtract(shopFund.getSaleProcessingFund())
                    .subtract(shopFund.getLoadProcessingFund()));
			shopFund.setShareAvailableFund(shopFund.getShareAvailableFund()
                    .subtract(shopFund.getShareProcessingFund()));
			shopFundDao.update(shopFund);
			// 插入提现申请表
			Withdrawal withdrawal = new Withdrawal();
			withdrawal.preInsert();
			withdrawal.setShopId(shopFund.getId());
			// 创建操作者id为卖家
			User user = new User();
			user.setId(shopFund.getId());
			withdrawal
					.setWithdrawalStatus(ApiConstants.WITHDRAW_STATUS_NOT_CONFIRMED);
			withdrawal.setCreateBy(user);
			withdrawal.setAmount(fundCash);
			withdrawal.setShopAccountId(bankId);
			withdrawalService.insert(withdrawal);

			// 插入资金操作记录表
            insertLoadLog(shopId, shopFund, zero, withdrawal);
            insertSaleLog(shopId, shopFund, zero, withdrawal);
            insertShareLog(shopId, shopFund, zero, withdrawal);

            break;
		}
		case 4: {
			// 修改总收入表
			// 修改销售收入
			shopFund.setSaleProcessingFund(shopFund.getSaleAvailableFund());
			shopFund.setSaleAvailableFund(new BigDecimal(0));
			// 修改代理奖励
			shopFund.setLoadProcessingFund(shopFund.getLoadAvailableFund());
			shopFund.setLoadAvailableFund(new BigDecimal(0));
			// 修改分享收入
			shopFund.setShareProcessingFund(shopFund.getShareAvailableFund());
			shopFund.setShareAvailableFund(new BigDecimal(0));
			// 设置分红提现中金额为fundcash -减去销售可提现-减去代理可提现-减去分享可提现
			shopFund.setBonusProcessingFund(fundCash
					.subtract(shopFund.getSaleProcessingFund())
					.subtract(shopFund.getLoadProcessingFund())
					.subtract(shopFund.getShareProcessingFund()));
			shopFund.setBonusAvailableFund(shopFund.getBonusAvailableFund()
					.subtract(shopFund.getBonusProcessingFund()));
			shopFundDao.update(shopFund);
			// 插入提现申请表
			Withdrawal withdrawal = new Withdrawal();
			withdrawal.preInsert();
			withdrawal.setShopId(shopFund.getId());
			// 创建操作者id为卖家
			User user = new User();
			user.setId(shopFund.getId());
			withdrawal
					.setWithdrawalStatus(ApiConstants.WITHDRAW_STATUS_NOT_CONFIRMED);
			withdrawal.setCreateBy(user);
			withdrawal.setAmount(fundCash);
			withdrawal.setShopAccountId(bankId);
			withdrawalService.insert(withdrawal);

            // 插入资金操作记录表
            insertBonusLog(shopId, shopFund, zero, withdrawal);
            insertLoadLog(shopId, shopFund, zero, withdrawal);
            insertSaleLog(shopId, shopFund, zero, withdrawal);
            insertShareLog(shopId, shopFund, zero, withdrawal);

            break;
		}
		case 5: {
			break;
		}
		default:
			break;
		}
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("status", ApiConstants.YES);
		return map;
	}

    private void insertBonusLog(String shopId, ShopFund shopFund, BigDecimal zero, Withdrawal withdrawal) {
        if (shopFund.getBonusProcessingFund().compareTo(zero) != 0) {
            fundOperLogService.insert(shopId, FundFieldType.BONUS_AVAILABLE_FUND,shopFund.getBonusProcessingFund().negate(),shopFund.getBonusAvailableFund(),withdrawal.getId(), FundOperLog.WITHDRAWALS,
                "提现申请成功，减去分红可提现资金[" + shopFund.getBonusProcessingFund() + "]");
            fundOperLogService.insert(shopId,FundFieldType.BONUS_PROCESSING_FUND,shopFund.getBonusProcessingFund(),shopFund.getBonusProcessingFund(),withdrawal.getId(),FundOperLog.WITHDRAWALS,
                "提现申请成功，增加分红提现中资金[" + shopFund.getBonusProcessingFund() + "]");
        }
    }

    private void insertLoadLog(String shopId, ShopFund shopFund, BigDecimal zero, Withdrawal withdrawal) {
        if (shopFund.getLoadProcessingFund().compareTo(zero) != 0) {
            fundOperLogService.insert(shopId, FundFieldType.LOAD_AVAILABLE_FUND,shopFund.getLoadProcessingFund().negate(),shopFund.getLoadAvailableFund(),withdrawal.getId(), FundOperLog.WITHDRAWALS,
                "提现申请成功，减少上架可提现资金[" + shopFund.getLoadProcessingFund() + "]");
            fundOperLogService.insert(shopId,FundFieldType.LOAD_PROCESSING_FUND,shopFund.getLoadProcessingFund(),shopFund.getLoadProcessingFund(),withdrawal.getId(),FundOperLog.WITHDRAWALS,
                "提现申请成功，增加上架提现中资金[" + shopFund.getLoadProcessingFund() + "]");
        }
    }

    private void insertSaleLog(String shopId, ShopFund shopFund, BigDecimal zero, Withdrawal withdrawal) {
        if (shopFund.getSaleProcessingFund().compareTo(zero) != 0) {
            fundOperLogService.insert(shopId, FundFieldType.SALE_AVAILABLE_FUND,shopFund.getSaleProcessingFund().negate(),shopFund.getSaleAvailableFund(),withdrawal.getId(), FundOperLog.WITHDRAWALS,
                "提现申请成功，减少销售可提现资金[" + shopFund.getSaleProcessingFund() + "]");
            fundOperLogService.insert(shopId,FundFieldType.SALE_PROCESSING_FUND,shopFund.getSaleProcessingFund(),shopFund.getSaleProcessingFund(),withdrawal.getId(),FundOperLog.WITHDRAWALS,
                "提现申请成功，增加销售提现中资金[" + shopFund.getSaleProcessingFund() + "]");
        }
    }

    private void insertShareLog(String shopId, ShopFund shopFund, BigDecimal zero, Withdrawal withdrawal) {
        if (shopFund.getShareProcessingFund().compareTo(zero) != 0) {
            fundOperLogService.insert(shopId, FundFieldType.SHARE_AVAILABLE_FUND,shopFund.getShareProcessingFund().negate(),shopFund.getShareAvailableFund(),withdrawal.getId(), FundOperLog.WITHDRAWALS,
                "提现申请成功，减去分享可提现资金[" + shopFund.getShareProcessingFund() + "]");
            fundOperLogService.insert(shopId,FundFieldType.SHARE_PROCESSING_FUND,shopFund.getShareProcessingFund(),shopFund.getShareProcessingFund(),withdrawal.getId(),FundOperLog.WITHDRAWALS,
                "提现申请成功，增加分享提现中资金[" + shopFund.getShareProcessingFund() + "]");
        }
    }

    //判断提现金额状态
	private   int getFundStatus(ShopFund shopFund, BigDecimal fundCash) {
		shopFund.countTotalAvailableFund();
		if (shopFund.getTotalAvailableFund().compareTo(fundCash) < 0) {
			return 0;
		} else if (shopFund.getSaleAvailableFund().compareTo(fundCash) >= 0) {
			return 1;
		} else if (shopFund.getSaleAvailableFund()
				.add(shopFund.getLoadAvailableFund()).compareTo(fundCash) >= 0) {
			return 2;
		}else if(shopFund.getSaleAvailableFund()
				.add(shopFund.getLoadAvailableFund())
                .add(shopFund.getShareAvailableFund()).compareTo(fundCash)>=0){
			return 3;
		}else if(shopFund.getSaleAvailableFund()
                .add(shopFund.getLoadAvailableFund())
                .add(shopFund.getShareAvailableFund())
                .add(shopFund.getBonusAvailableFund()).compareTo(fundCash)>=0){
			return 4;
        }
		return 5;

	}
	//通过支付单id查询卖家的销售待确认收入
	public List<ShopFundDto> getSaleHoldFund(String id) {
		return shopFundDao.getSaleHoldFund(id);
	}
	//更新销售待确认收入
	public void refundSuccess(ShopFundDto fund) {
		shopFundDao.refundSuccess(fund);
	}
	//销售待确认收入获取
	public List<FundOperSaleLog> getSaleHoldFundLog(String id) {
		return shopFundDao.getSaleHoldFundLog(id);
	}
	//确认收货资金变化
	public void checkShipping(String shopId, BigDecimal saleEarning,
			BigDecimal loadEarning) {
		shopFundDao.checkShipping(shopId, saleEarning,
				loadEarning);
	}
	public BigDecimal getShopLoadEarning(String shopId) {
		
		return shopFundDao.getShopLoadEarning(shopId);
	}
	//获取分红详单数
	public Integer getShopBonusDetailBillCount(String month, String shopId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("shopId", shopId);
		map.put("month", month);
		return shopFundDao.getShopBonusDetailBillCount(map);
	}
	//查询分红详单
	public List<FundDetailBillDto> getShopBonusDetailBill(Pager page,
			String shopId, String month) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page);
		map.put("shopId", shopId);
		map.put("month", month);
		return shopFundDao.getShopBonusDetailBill(map);
	}
	//查询订单对应的分红
	public List<FundOperBonusLog> getBonusHoldFundLog(String id) {
		
		return shopFundDao.getBonusHoldFundLog(id);
	}
	//查询分红
	public List<ShopFund> getBonusHoldFund(String id) {
		
		return shopFundDao.getBonusHoldFund(id);
	}
	public void addBonusBoldFund(ShopFund bonusFund) {
		shopFundDao.addBonusBoldFund(bonusFund);
	}
	public void addSaleHoldFund(ShopFund bonusFund) {
		shopFundDao.addSaleHoldFund(bonusFund);
	}
	//确认收货 修改上级卖家收入
	public void addBonusAvaiableFund(String parentShopId,
			BigDecimal bonusEarning) {
		shopFundDao.addBonusAvaiableFund(parentShopId,bonusEarning);
	}
	public void addBonusAvaiableFundOnly(String parentShopId,
									 BigDecimal bonusEarning) {
		shopFundDao.addBonusAvaiableFundOnly(parentShopId, bonusEarning);
	}
	//支付成功后,如果是预售拿货,直接增加上家卖家销售分红可提现收入
	public void addBonusAvaiableFundOfReserve(String parentShopId,
									 BigDecimal bonusEarning){
		shopFundDao.addBonusAvaiableFundOfReserve(parentShopId,bonusEarning);
	}
	public void addSaleAvaiableFund(String shopId,
									BigDecimal saleEarning){
		shopFundDao.addSaleAvaiableFund(shopId,
				saleEarning);
	}
	public List<FundDetailBillDto> getShopDetailBill2(Pager page,
			String shopId, String month) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page);
		map.put("shopId", shopId);
		map.put("month", month);
		return shopFundDao.getShopDetailBill2(map);
	}
	public List<FundDetailBillDto> getShopSaleDetailBill2(Pager page,

			String shopId, String month) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page);
		map.put("shopId", shopId);
		map.put("month", month);
		return shopFundDao.getShopSaleDetailBill2(map);
	}
	public List<FundDetailBillDto> getShopBonusDetailBill2(Pager page,
			String shopId, String month) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page);
		map.put("shopId", shopId);
		map.put("month", month);
		return shopFundDao.getShopBonusDetailBill2(map);
	}

    public Integer getShopBonusDetailBillCount2(String month, String shopId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("shopId", shopId);
        map.put("month", month);
        return shopFundDao.getShopBonusDetailBillCount2(map);
    }
    public List<FundDetailBillDto> getShopBonusDetailBill3(Pager page,
                                                           String shopId, String month) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("page", page);
        map.put("shopId", shopId);
        map.put("month", month);
        return shopFundDao.getShopBonusDetailBill3(map);
    }
    public Integer getShopFirstBonusDetailBillCount(String month, String shopId){
        Map<String, Object> map = new HashMap<>();
        map.put("shopId", shopId);
        map.put("month", month);
        return shopFundDao.getShopFirstBonusDetailBillCount(map);
    }
    public Integer getShopSecondBonusDetailBillCount(String month, String shopId){
        Map<String, Object> map = new HashMap<>();
        map.put("shopId", shopId);
        map.put("month", month);
        return shopFundDao.getShopSecondBonusDetailBillCount(map);
    }

	public List<FundDetailBillDto> getShopFirstBonusDetailBill2(Pager page,
																 String shopId, String month) {
		Map<String, Object> map = new HashMap<>();
		map.put("page", page);
		map.put("shopId", shopId);
		map.put("month", month);
		return shopFundDao.getShopFirstBonusDetailBill2(map);
	}
	public List<FundDetailBillDto> getShopSecondBonusDetailBill2(Pager page,
																String shopId, String month) {
		Map<String, Object> map = new HashMap<>();
		map.put("page", page);
		map.put("shopId", shopId);
		map.put("month", month);
		return shopFundDao.getShopSecondBonusDetailBill2(map);
	}
	public List<ShopFund> getFirstBonusHoldFund(String id) {
		return shopFundDao.getFirstBonusHoldFund(id);
	}
    public List<ShopFund> getSecondBonusHoldFund(String id) {
        return shopFundDao.getSecondBonusHoldFund(id);
    }
	public List<FundOperBonusLog> getFirstBonusHoldFundLog(String id) {

		return shopFundDao.getFirstBonusHoldFundLog(id);
	}
    public List<FundOperBonusLog> getSecondBonusHoldFundLog(String id) {
        return shopFundDao.getSecondBonusHoldFundLog(id);
    }
	public void updateShopEarning(String shopId,BigDecimal saleEarning,BigDecimal bonusEarning){
		shopFundDao.updateShopEarning(shopId,saleEarning,bonusEarning);
	}
}
