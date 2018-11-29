package com.vcat.api.service;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.FundOperLog;
import com.vcat.module.ec.dao.FundOperLogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class FundOperLogService extends CrudService<FundOperLog> {
    private final String bonus = "bonus";   // 分红奖励
    private final String invite = "invite"; // 邀请奖励
    private final String load = "load";     // 上架奖励
    private final String sale = "sale";     // 销售奖励
    private final String share = "share";   // 分享奖励
    @Autowired
    private FundOperLogDao fundOperLogDao;

    @Override
    protected CrudDao<FundOperLog> getDao() {
        return fundOperLogDao;
    }

    @Transactional(readOnly = false)
    public int insert(FundOperLog fundOperLog){
        if(null == fundOperLog || null == fundOperLog.getFundFieldType() || StringUtils.isBlank(fundOperLog.getFundFieldType().getName())){
            logger.error("保存资金操作日志失败，参数不完整：" + fundOperLog);
            return 0;
        }
        int i = 0;
        if(fundOperLog.getFundFieldType().getName().indexOf(bonus) == 0){
            i = fundOperLogDao.insertBonus(fundOperLog);
        }else if(fundOperLog.getFundFieldType().getName().indexOf(invite) == 0){
            i = fundOperLogDao.insertInvite(fundOperLog);
        }else if(fundOperLog.getFundFieldType().getName().indexOf(load) == 0){
            i = fundOperLogDao.insertLoad(fundOperLog);
        }else if(fundOperLog.getFundFieldType().getName().indexOf(sale) == 0){
            i = fundOperLogDao.insertSale(fundOperLog);
        }else if(fundOperLog.getFundFieldType().getName().indexOf(share) == 0){
            i = fundOperLogDao.insertShare(fundOperLog);
        }
        return i;
    }

    /**
     * 插入资金操作日志
     * @param shopId 店铺ID
     * @param fieldTypeName 操作资金类型名称(英文)
     * @param fund 本次操作资金(正数增加、负数减去)
     * @param remainFund 本次操作后余额
     * @param relateId 关联ID
     * @param fundType 资金操作类型
     * @param note 备注
     */
    @Transactional(readOnly = false)
    public void insert(String shopId, String fieldTypeName, BigDecimal fund, BigDecimal remainFund, String relateId, int fundType, String note){
        insert(FundOperLog.create(shopId,fieldTypeName,fund,remainFund,relateId,fundType,note));
    }

    public FundOperLog get(FundOperLog fundOperLog){
        if(fundOperLog.getFundFieldType().getName().indexOf(bonus) == 0){
            fundOperLog = fundOperLogDao.getBonusLog(fundOperLog);
        }else if(fundOperLog.getFundFieldType().getName().indexOf(invite) == 0){
            fundOperLog = fundOperLogDao.getInviteLog(fundOperLog);
        }else if(fundOperLog.getFundFieldType().getName().indexOf(load) == 0){
            fundOperLog = fundOperLogDao.getLoadLog(fundOperLog);
        }else if(fundOperLog.getFundFieldType().getName().indexOf(sale) == 0){
            fundOperLog = fundOperLogDao.getSaleLog(fundOperLog);
        }else if(fundOperLog.getFundFieldType().getName().indexOf(share) == 0){
            fundOperLog = fundOperLogDao.getShareLog(fundOperLog);
        }
        return fundOperLog;
    }
    public List<FundOperLog> getSaleLogs(){
        return fundOperLogDao.getSaleLogs();
    }
}
