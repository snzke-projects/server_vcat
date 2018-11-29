package com.vcat.module.ec.service;

import com.vcat.module.core.entity.FundOperLog;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ShopFundDao;
import com.vcat.module.ec.dao.UpgradeBonusDao;
import com.vcat.module.ec.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class UpgradeBonusService extends CrudService<UpgradeBonusDao, UpgradeBonus> {
    @Autowired
    private FundOperLogService fundOperLogService;
    @Autowired
    private ShopFundDao shopFundDao;
    @Autowired
    private MessageService messageService;

    /**
     * 发放伯乐奖励
     * @param upgradeBonus
     */
    @Transactional(readOnly = false)
    public void issueBonus(UpgradeBonus upgradeBonus) {
        UpgradeBonus source = get(upgradeBonus);
        if(upgradeBonus.getStatus() == 1){
            BigDecimal bonus = upgradeBonus.getAmount();
            if(bonus.compareTo(BigDecimal.ZERO) == 1){
                ShopFund originalParentFund = shopFundDao.get(source.getOriginalParent().getId());
                BigDecimal remind = originalParentFund.getSaleAvailableFund().add(bonus);
                String msg = "下属店铺[" + source.getShop().getName() + "]成功升级钻石店铺，获得伯乐奖励。增加销售可提现[" + bonus + "]元";
                fundOperLogService.insert(source.getOriginalParent().getId(), FundFieldType.SALE_AVAILABLE_FUND, bonus, remind, source.getId(), FundOperLog.UPGRADE_BONUS, msg);

                // 推送伯乐奖励财富消息
                MessageEarning earning = new MessageEarning();
                earning.setShop(source.getOriginalParent());
                earning.setType(MessageEarning.TYPE_UPGRADE);
                earning.setEarning(bonus);
                earning.setConsumer(source.getShop().getName());
                messageService.pushEarningMsg(earning);

                // 发放伯乐奖励到销售可提现中
                shopFundDao.issueUpgradeBonus(source.getOriginalParent().getId(), bonus);
            }
        }
        dao.issueBonus(upgradeBonus);
    }

    /**
     * 批量发放伯乐奖励
     * @param idStrArray
     * @param upgradeBonus
     */
    @Transactional(readOnly = false)
    public void batchIssueBonus(String[] idStrArray, UpgradeBonus upgradeBonus) {
        if(null == idStrArray || idStrArray.length == 0){
            return;
        }
        for (String id: idStrArray) {
            upgradeBonus.setId(id);
            issueBonus(upgradeBonus);
        }
    }
}
