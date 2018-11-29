package com.vcat.module.ec.service;

import com.vcat.common.sms.SmsClient;
import com.vcat.module.core.entity.FundOperLog;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.ec.dao.*;
import com.vcat.module.ec.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 提现申请Service
 */
@Service
@Transactional(readOnly = true)
public class WithdrawalService extends CrudService<WithdrawalDao, Withdrawal> {
    @Autowired
    private ShopFundDao shopFundDao;
    @Autowired
    private FundOperLogService fundOperLogService;
    @Autowired
    private MessageService messageService;

    @Transactional(readOnly = false)
    public Withdrawal getById(String id){
        return dao.getById(id);
    }

    @Transactional(readOnly = false)
    public void batchHandler(String[] ids){
        String note = UserUtils.getUser().getName() + " 批量处理提现申请完成！";
        for (int i = 0; i < ids.length; i++) {
            Withdrawal withdrawal = new Withdrawal();
            withdrawal.setId(ids[i]);
            withdrawal.setWithdrawalStatus(1);
            withdrawal.setNote(note);
            handler(withdrawal);
        }
    }

    @Transactional(readOnly = false)
    public void handler(Withdrawal withdrawal) {
        // 翻转提现状态
        dao.handler(withdrawal);

        // 在修改备注前获取备注信息
        String note = withdrawal.getNote();

        withdrawal = dao.getById(withdrawal.getId());

        ShopFund shopFund = new ShopFund();
        shopFund.setId(withdrawal.getShopId());
        // 获取店铺资金
        shopFund = shopFundDao.get(shopFund);
        // 获取提现中资金
        BigDecimal loadP = shopFund.getLoadProcessingFund();
        BigDecimal saleP = shopFund.getSaleProcessingFund();
        BigDecimal shareP = shopFund.getShareProcessingFund();
        BigDecimal bonusP = shopFund.getBonusProcessingFund();
        BigDecimal inviteP = BigDecimal.ZERO;

        String msgStart = "小店["+withdrawal.getShop().getName()+"]提现["+withdrawal.getAmount()+"]元";

        msgStart += (withdrawal.getWITHDRAWAL_STATUS_SUCCESS() == withdrawal.getWithdrawalStatus() ? "成功" : "失败") + "，";

        //清空店铺提现中资金
        clearProcessingFund(withdrawal, shopFund, loadP, saleP, shareP, bonusP, inviteP, msgStart);

        if(withdrawal.getWITHDRAWAL_STATUS_SUCCESS() == withdrawal.getWithdrawalStatus()){
            // 向小店店主推送财富消息 - 提现成功
            pushSuccessEarningMsg(withdrawal);

            // 增加店铺已提现资金
            addUsedFund(withdrawal, shopFund, loadP, saleP, shareP, bonusP, inviteP, msgStart);

            // 初始化店铺提现中金额以及增加店铺已提现金额
            shopFundDao.withdrawalSuccess(shopFund);
        }else if(withdrawal.getWITHDRAWAL_STATUS_FAILURE() == withdrawal.getWithdrawalStatus()){
            // 发送提现失败短信
            sendWithdrawalFailureSms(withdrawal.getShop().getCustomer().getPhoneNumber(),withdrawal.getShop().getName(),withdrawal.getAmount(),note);

            // 还原店铺可提现资金
            revertAvailableFund(withdrawal, shopFund, loadP, saleP, shareP, bonusP, inviteP, msgStart);

            // 初始化店铺提现中金额以及增加店铺已提现金额
            shopFundDao.withdrawalFailure(shopFund);
        }
    }

    /**
     * 清空店铺提现中资金
     * @param withdrawal
     * @param shopFund
     * @param loadP
     * @param saleP
     * @param shareP
     * @param bonusP
     * @param inviteP
     * @param msgStart
     */
    private void clearProcessingFund(Withdrawal withdrawal, ShopFund shopFund, BigDecimal loadP, BigDecimal saleP, BigDecimal shareP, BigDecimal bonusP, BigDecimal inviteP, String msgStart) {
        // 清零分红提现中
        if(BigDecimal.ZERO.compareTo(bonusP) != 0)fundOperLogService.insert(shopFund.getId(), FundFieldType.BONUS_PROCESSING_FUND, BigDecimal.ZERO.subtract(bonusP), BigDecimal.ZERO, withdrawal.getId(), FundOperLog.WITHDRAWALS, msgStart + "减去分红提现中资金[" + bonusP + "]");
        // 清零邀请提现中
        if(BigDecimal.ZERO.compareTo(inviteP) != 0)fundOperLogService.insert(shopFund.getId(), FundFieldType.INVITE_PROCESSING_FUND, BigDecimal.ZERO.subtract(inviteP), BigDecimal.ZERO, withdrawal.getId(), FundOperLog.WITHDRAWALS, msgStart + "减去邀请提现中资金[" + inviteP + "]");
        // 清零上架提现中
        if(BigDecimal.ZERO.compareTo(loadP) != 0)fundOperLogService.insert(shopFund.getId(), FundFieldType.LOAD_PROCESSING_FUND, BigDecimal.ZERO.subtract(loadP), BigDecimal.ZERO, withdrawal.getId(), FundOperLog.WITHDRAWALS, msgStart + "减去上架提现中资金[" + loadP + "]");
        // 清零销售提现中
        if(BigDecimal.ZERO.compareTo(saleP) != 0)fundOperLogService.insert(shopFund.getId(), FundFieldType.SALE_PROCESSING_FUND, BigDecimal.ZERO.subtract(saleP), BigDecimal.ZERO, withdrawal.getId(), FundOperLog.WITHDRAWALS, msgStart + "减去销售提现中资金[" + saleP + "]");
        // 清零分享提现中
        if(BigDecimal.ZERO.compareTo(shareP) != 0)fundOperLogService.insert(shopFund.getId(), FundFieldType.SHARE_PROCESSING_FUND, BigDecimal.ZERO.subtract(shareP), BigDecimal.ZERO, withdrawal.getId(), FundOperLog.WITHDRAWALS, msgStart + "减去分享提现中资金[" + shareP + "]");
    }

    /**
     * 推送提现成功财富消息
     * @param withdrawal
     */
    private void pushSuccessEarningMsg(Withdrawal withdrawal) {
        MessageEarning earning = new MessageEarning();
        Shop shop = new Shop();
        shop.setId(withdrawal.getShopId());
        earning.setShop(shop);
        earning.setType(MessageEarning.TYPE_WITHDRAWAL);
        earning.setConsumer("V猫");
        earning.setEarning(withdrawal.getAmount());
        messageService.pushEarningMsg(earning);
    }

    /**
     * 增加店铺已提现资金
     * @param withdrawal
     * @param shopFund
     * @param loadP
     * @param saleP
     * @param shareP
     * @param bonusP
     * @param inviteP
     * @param msgStart
     */
    private void addUsedFund(Withdrawal withdrawal, ShopFund shopFund, BigDecimal loadP, BigDecimal saleP, BigDecimal shareP, BigDecimal bonusP, BigDecimal inviteP, String msgStart) {
        // 增加分红已提现
        if(BigDecimal.ZERO.compareTo(bonusP) != 0)fundOperLogService.insert(shopFund.getId(), FundFieldType.BONUS_USED_FUND,bonusP,shopFund.getBonusUsedFund().add(bonusP),withdrawal.getId(),FundOperLog.WITHDRAWALS,msgStart + "增加分红已提现资金[" + bonusP + "]");
        // 增加邀请已提现
        if(BigDecimal.ZERO.compareTo(inviteP) != 0)fundOperLogService.insert(shopFund.getId(),FundFieldType.INVITE_USED_FUND,bonusP,BigDecimal.ZERO,withdrawal.getId(),FundOperLog.WITHDRAWALS,msgStart + "增加邀请已提现资金[" + inviteP + "]");
        // 增加上架已提现
        if(BigDecimal.ZERO.compareTo(loadP) != 0)fundOperLogService.insert(shopFund.getId(),FundFieldType.LOAD_USED_FUND,loadP,shopFund.getLoadUsedFund().add(loadP),withdrawal.getId(),FundOperLog.WITHDRAWALS,msgStart + "增加上架已提现资金[" + loadP + "]");
        // 增加销售已提现
        if(BigDecimal.ZERO.compareTo(saleP) != 0)fundOperLogService.insert(shopFund.getId(),FundFieldType.SALE_USED_FUND,saleP,shopFund.getSaleUsedFund().add(saleP),withdrawal.getId(),FundOperLog.WITHDRAWALS,msgStart + "增加销售已提现资金[" + saleP + "]");
        // 增加分享已提现
        if(BigDecimal.ZERO.compareTo(shareP) != 0)fundOperLogService.insert(shopFund.getId(),FundFieldType.SHARE_USED_FUND,shareP,shopFund.getShareUsedFund().add(shareP),withdrawal.getId(),FundOperLog.WITHDRAWALS,msgStart + "增加分享已提现资金[" + shareP + "]");
    }

    /**
     * 发送提现失败短信
     * @param phone
     * @param name
     * @param amount
     * @param msg
     */
    private void sendWithdrawalFailureSms(String phone,String name,BigDecimal amount,String msg) {
        // 发送退货/退款失败短信给买家
        String tplValue = "#name#="+name+"&#amount#="+amount+"&#msg#="+msg;
        SmsClient.tplSendSms(SmsClient.TPL_WITHDRAWAL_FAILURE, tplValue, phone);
    }

    /**
     * 还原店铺可提现资金
     * @param withdrawal
     * @param shopFund
     * @param loadP
     * @param saleP
     * @param shareP
     * @param bonusP
     * @param inviteP
     * @param msgStart
     */
    private void revertAvailableFund(Withdrawal withdrawal, ShopFund shopFund, BigDecimal loadP, BigDecimal saleP, BigDecimal shareP, BigDecimal bonusP, BigDecimal inviteP, String msgStart) {
        // 还原分红可提现
        if(BigDecimal.ZERO.compareTo(bonusP) != 0)fundOperLogService.insert(shopFund.getId(), FundFieldType.BONUS_AVAILABLE_FUND, bonusP, shopFund.getBonusAvailableFund().add(bonusP), withdrawal.getId(), FundOperLog.WITHDRAWALS, msgStart + "增加分红可提现资金[" + bonusP + "]");
        // 还原邀请可提现
        if(BigDecimal.ZERO.compareTo(inviteP) != 0)fundOperLogService.insert(shopFund.getId(), FundFieldType.INVITE_AVAILABLE_FUND, bonusP, BigDecimal.ZERO, withdrawal.getId(), FundOperLog.WITHDRAWALS, msgStart + "增加邀请可提现资金[" + inviteP + "]");
        // 还原上架可提现
        if(BigDecimal.ZERO.compareTo(loadP) != 0)fundOperLogService.insert(shopFund.getId(), FundFieldType.LOAD_AVAILABLE_FUND, loadP, shopFund.getLoadAvailableFund().add(loadP), withdrawal.getId(), FundOperLog.WITHDRAWALS, msgStart + "增加上架可提现资金[" + loadP + "]");
        // 还原销售可提现
        if(BigDecimal.ZERO.compareTo(saleP) != 0)fundOperLogService.insert(shopFund.getId(), FundFieldType.SALE_AVAILABLE_FUND, saleP, shopFund.getSaleAvailableFund().add(saleP), withdrawal.getId(), FundOperLog.WITHDRAWALS, msgStart + "增加销售可提现资金[" + saleP + "]");
        // 还原分享可提现
        if(BigDecimal.ZERO.compareTo(shareP) != 0)fundOperLogService.insert(shopFund.getId(), FundFieldType.SHARE_AVAILABLE_FUND, shareP, shopFund.getShareAvailableFund().add(shareP), withdrawal.getId(), FundOperLog.WITHDRAWALS, msgStart + "增加分享可提现资金[" + shareP + "]");
    }

    public List<WithdrawalExcel> exprotList(Withdrawal withdrawal){
        return dao.exprotList(withdrawal);
    }
}
