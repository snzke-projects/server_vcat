package com.vcat.module.ec.service;

import com.vcat.common.push.PushService;
import com.vcat.common.utils.IdGen;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.ec.dao.InvitationCodeDao;
import com.vcat.module.ec.dao.ShopDao;
import com.vcat.module.ec.dao.UpgradeRequestDao;
import com.vcat.module.ec.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class UpgradeRequestService extends CrudService<UpgradeRequestDao, UpgradeRequest> {
    @Autowired
    private MessageService messageService;
    @Autowired
    private ShopDao shopDao;
    @Autowired
    private InvitationCodeDao invitationCodeDao;
    @Autowired
    private UpgradeBonusService upgradeBonusService;

    @Override
    public UpgradeRequest get(String id) {
        UpgradeRequest upgradeRequest = new UpgradeRequest();
        upgradeRequest.setId(id);
        return get(upgradeRequest);
    }

    @Override
    public UpgradeRequest get(UpgradeRequest entity) {
        UpgradeRequest result = super.get(entity);
        result.setLogList(dao.findLogs(result));
        return result;
    }

    @Transactional()
    public void add(String shopId, String shopName){
        String id = IdGen.uuid();

        dao.add(id, shopId);

        UpgradeRequestLog upgradeRequestLog = new UpgradeRequestLog();
        upgradeRequestLog.preInsert();
        upgradeRequestLog.getSqlMap().put("upgradeRequestId", id);
        upgradeRequestLog.setStatus(0);
        upgradeRequestLog.setNote("[" + UserUtils.getUser().getName() + "]手动为[" + shopName + "]店铺添加升级申请");
        dao.insertLog(upgradeRequestLog);

        UpgradeRequest upgradeRequest = new UpgradeRequest();
        upgradeRequest.setId(id);
        upgradeRequest.setStatus(1);
        approval(upgradeRequest);
    }

    @Transactional()
    public void approval(UpgradeRequest upgradeRequest){
        boolean approvalSussess = upgradeRequest.getStatus() == 1;
        // 审核
        dao.approval(upgradeRequest);
        // 记录审核日志
        insertLog(upgradeRequest);
        // 获取原升级申请
        UpgradeRequest source = get(upgradeRequest);
        // 获取申请店铺
        Shop shop = shopDao.get(source.getShop());

        String title = approvalSussess ? "喵主，您的小店已升级成功！" : "喵主，您的升级申请被拒绝！";
        String content = approvalSussess ? "您的小店已成功升级为钻石小店。" : "您的小店升级申请被拒绝！\r\n原因：" + upgradeRequest.getSqlMap().get("note");
        // 推送店铺升级申请结果消息
        messageService.pushRemindMessage(source.getShop().getId(), title, content);
        if(approvalSussess){
            // 升级小店
            dao.upgradeShop(source);
            // 设置钻石小店邀请码
            String codeId = invitationCodeDao.getUnusedCodeId();
            dao.setInvitationCode(source.getShop().getId(),codeId);
            invitationCodeDao.updateCodeIsUseful(codeId);

            Map<String,Object> cusPrarm = new HashMap<>();
            cusPrarm.put(PushService.UPGRADE_SUCCESS,true);

            if(null != source.getOriginalParent()){
                // 推送上架店铺可获得伯乐奖励消息
                messageService.pushRemindMessage(source.getOriginalParent().getId(), "您的下属店铺【" + shop.getName() + "】成功升级为钻石小店！", "恭喜您，您的下属店铺【" + shop.getName() + "】成功升级为钻石小店！\r\n您将在近段时间得到一笔伯乐奖励。", cusPrarm);

                // 插入伯乐奖励
                UpgradeBonus upgradeBonus = new UpgradeBonus();
                upgradeBonus.setShop(shop);
                upgradeBonus.setOriginalParent(source.getOriginalParent());
                upgradeBonus.setRequest(source);
                upgradeBonusService.save(upgradeBonus);
            }
        }
    }

    /**
     * 插入审批日志
     * @param upgradeRequest
     */
    @Transactional()
    private void insertLog(UpgradeRequest upgradeRequest) {
        String note = upgradeRequest.getSqlMap().get("note");
        note = upgradeRequest.getStatus() == 1 ? "审核通过" : note;
        UpgradeRequestLog upgradeRequestLog = new UpgradeRequestLog();
        upgradeRequestLog.preInsert();
        upgradeRequestLog.getSqlMap().put("upgradeRequestId", upgradeRequest.getId());
        upgradeRequestLog.setStatus(upgradeRequest.getStatus());
        upgradeRequestLog.setNote(note);
        dao.insertLog(upgradeRequestLog);
    }
}
