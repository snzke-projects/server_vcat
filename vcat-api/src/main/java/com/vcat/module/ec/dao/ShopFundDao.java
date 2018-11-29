package com.vcat.module.ec.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.vcat.module.ec.dto.ShopFundDto;
import com.vcat.module.ec.dto.ShopFundExpandDto;
import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.FundDetailBillDto;
import com.vcat.module.ec.entity.FundOperBonusLog;
import com.vcat.module.ec.entity.FundOperSaleLog;
import com.vcat.module.ec.entity.Rank;
import com.vcat.module.ec.entity.ShopFund;

@MyBatisDao
public interface ShopFundDao extends CrudDao<ShopFund> {

	ShopFund getShopFund(String shopId);
    ShopFundExpandDto getShopFund2(@Param("shopId") String shopId);

	List<FundDetailBillDto> getShopDetailBill(Map<String, Object> map);

	Integer getShopDetailBillCount(Map<String, Object> map);

	Integer count();

	Integer getShopSaleDetailBillCount(Map<String, Object> map);

	List<FundDetailBillDto> getShopSaleDetailBill(Map<String, Object> map);

	List<Rank> getRankList();

	Integer withdrawalSuccess(ShopFund shopFund);
	//关于提现申请的update
	ShopFund withdrawCashing(Map<String, Object> map);

	Integer withdrawalFailure(ShopFund shopFund);

	Integer refundSuccess(ShopFundDto shopFundDto);

	List<ShopFundDto> getSaleHoldFund(String id);
	
	List<FundOperSaleLog> getSaleHoldFundLog(String id);

	void checkShipping(@Param("id") String shopId, @Param("saleEarning") BigDecimal saleEarning,
			@Param("loadEarning") BigDecimal loadEarning);

	BigDecimal getShopLoadEarning(String shopId);

	Integer getShopBonusDetailBillCount(Map<String, Object> map);
	Integer getShopBonusDetailBillCount2(Map<String, Object> map);
	Integer getShopFirstBonusDetailBillCount(Map<String, Object> map);
	Integer getShopSecondBonusDetailBillCount(Map<String, Object> map);

	List<FundDetailBillDto> getShopBonusDetailBill(Map<String, Object> map);

	List<FundOperBonusLog> getBonusHoldFundLog(String id);

	List<ShopFund> getBonusHoldFund(String id);
	
	void addBonusBoldFund(ShopFund bonusFund);
    void addSaleHoldFund(ShopFund bonusFund);

	void addSaleAvaiableFund(@Param("shopId")String shopId, @Param("saleEarning")BigDecimal saleEarning);
	void addBonusAvaiableFund(@Param("parentShopId")String parentShopId, @Param("bonusEarning")BigDecimal bonusEarning);
	void addBonusAvaiableFundOnly(@Param("parentShopId")String parentShopId, @Param("bonusEarning")BigDecimal bonusEarning);
	void addBonusAvaiableFundOfReserve(@Param("parentShopId")String parentShopId, @Param("bonusEarning")BigDecimal bonusEarning);

	//V2
	List<FundDetailBillDto> getShopDetailBill2(Map<String, Object> map);
	//V2
	List<FundDetailBillDto> getShopSaleDetailBill2(Map<String, Object> map);
	//V2
	List<FundDetailBillDto> getShopBonusDetailBill2(Map<String, Object> map);
    List<FundDetailBillDto> getShopBonusDetailBill3(Map<String, Object> map);
	List<FundDetailBillDto> getShopFirstBonusDetailBill2(Map<String, Object> map);
	List<FundDetailBillDto> getShopSecondBonusDetailBill2(Map<String, Object> map);
	List<ShopFund> getSecondBonusHoldFund(String id);
	List<ShopFund> getFirstBonusHoldFund(String id);

	List<FundOperBonusLog> getSecondBonusHoldFundLog(String id);
	List<FundOperBonusLog> getFirstBonusHoldFundLog(String id);

	void updateShopEarning(@Param("shopId")String shopId, @Param("saleEarning") BigDecimal saleEarning,@Param("bonusEarning") BigDecimal bonusEarning);
}
