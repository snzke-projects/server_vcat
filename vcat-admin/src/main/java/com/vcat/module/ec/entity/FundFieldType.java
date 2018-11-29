package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.StatusEntity;

public class FundFieldType extends StatusEntity<FundFieldType> {
	private static final long serialVersionUID = 1L;
    /**
     * 分红可提现资金
     */
    public static final String BONUS_AVAILABLE_FUND = "bonus_available_fund";
    /**
     * 分红待确认资金
     */
    public static final String BONUS_HOLD_FUND = "bonus_hold_fund";
    /**
     * 分红提现中资金
     */
    public static final String BONUS_PROCESSING_FUND = "bonus_processing_fund";
    /**
     * 分红已提现资金
     */
    public static final String BONUS_USED_FUND = "bonus_used_fund";
    /**
     * 邀请可提现资金
     */
    public static final String INVITE_AVAILABLE_FUND = "invite_available_fund";
    /**
     * 邀请提现中资金
     */
    public static final String INVITE_PROCESSING_FUND = "invite_processing_fund";
    /**
     * 邀请已提现资金
     */
    public static final String INVITE_USED_FUND = "invite_used_fund";
    /**
     * 上架可提现资金
     */
    public static final String LOAD_AVAILABLE_FUND = "load_available_fund";
    /**
     * 上架提现中资金
     */
    public static final String LOAD_PROCESSING_FUND = "load_processing_fund";
    /**
     * 上架已提现资金
     */
    public static final String LOAD_USED_FUND = "load_used_fund";
    /**
     * 销售可提现资金
     */
    public static final String SALE_AVAILABLE_FUND = "sale_available_fund";
    /**
     * 销售待确认资金
     */
    public static final String SALE_HOLD_FUND = "sale_hold_fund";
    /**
     * 销售提现中资金
     */
    public static final String SALE_PROCESSING_FUND = "sale_processing_fund";
    /**
     * 销售已提现资金
     */
    public static final String SALE_USED_FUND = "sale_used_fund";
    /**
     * 分享可提现资金
     */
    public static final String SHARE_AVAILABLE_FUND = "share_available_fund";
    /**
     * 分享提现中资金
     */
    public static final String SHARE_PROCESSING_FUND = "share_processing_fund";
    /**
     * 分享已提现资金
     */
    public static final String SHARE_USED_FUND = "share_used_fund";
	public FundFieldType(String name){
		super.setName(name);
	}
}
