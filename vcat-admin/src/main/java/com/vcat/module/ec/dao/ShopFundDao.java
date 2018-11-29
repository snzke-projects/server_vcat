package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.ShopFund;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@MyBatisDao
public interface ShopFundDao extends CrudDao<ShopFund> {
    /**
     * 提现成功增加已提现收入并还原体现中收入
     * @param shopFund
     * @return
     */
	Integer withdrawalSuccess(ShopFund shopFund);

    /**
     * 提现失败还原可提现收入
     * @param shopFund
     * @return
     */
	Integer withdrawalFailure(ShopFund shopFund);

    /**
     * 退款减去销售待确认收入
     * @param shopFund
     * @return
     */
	Integer refundSuccess(ShopFund shopFund);

    /**
     * 退款减去分红待确认收入
     * @param bonusFund
     */
	void addBonusBoldFund(ShopFund bonusFund);

    /**
     * 增加伯乐奖励销售可提现收入
     * @param shopId
     * @param bonus
     */
    void issueUpgradeBonus(@Param(value = "shopId")String shopId, @Param(value = "bonus")BigDecimal bonus);

    /**
     * 增加团队分红可提现收入
     * @param shopId
     * @param bonus
     */
    void settlementTeamBonus(@Param(value = "shopId")String shopId, @Param(value = "bonus")BigDecimal bonus);

    /**
     * 减去待确认销售收入，增加可提现销售收入
     * @param shopId
     * @param bonus
     */
    void handlerSaleEarning(@Param(value = "shopId")String shopId, @Param(value = "bonus")BigDecimal bonus);

    /**
     * 减去待确认分红收入，增加可提现分红收入
     * @param shopId
     * @param bonus
     */
    void handlerBonusEarning(@Param(value = "shopId")String shopId, @Param(value = "bonus")BigDecimal bonus);
}
