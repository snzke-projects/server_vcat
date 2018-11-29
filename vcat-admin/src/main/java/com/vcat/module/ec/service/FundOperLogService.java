package com.vcat.module.ec.service;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.FundOperLog;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.core.service.ServiceException;
import com.vcat.module.ec.dao.FundOperLogDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class FundOperLogService extends CrudService<FundOperLogDao, FundOperLog> {
    private final String bonus = "bonus";   // 分红奖励
    private final String invite = "invite"; // 邀请奖励
    private final String load = "load";     // 上架奖励
    private final String sale = "sale";     // 销售奖励
    private final String share = "share";   // 分享奖励
    @Transactional(readOnly = false)
    public void insert(FundOperLog fundOperLog){
        if(null == fundOperLog || null == fundOperLog.getFundFieldType() || StringUtils.isBlank(fundOperLog.getFundFieldType().getName())){
            logger.error("保存资金操作日志失败，参数不完整：" + fundOperLog);
            throw new ServiceException("保存资金操作日志失败，参数不完整：" + fundOperLog);
        }
        if(fundOperLog.getFundFieldType().getName().indexOf(bonus) == 0){
            dao.insertBonus(fundOperLog);
        }else if(fundOperLog.getFundFieldType().getName().indexOf(invite) == 0){
            dao.insertInvite(fundOperLog);
        }else if(fundOperLog.getFundFieldType().getName().indexOf(load) == 0){
            dao.insertLoad(fundOperLog);
        }else if(fundOperLog.getFundFieldType().getName().indexOf(sale) == 0){
            dao.insertSale(fundOperLog);
        }else if(fundOperLog.getFundFieldType().getName().indexOf(share) == 0){
            dao.insertShare(fundOperLog);
        }else{
            logger.error("保存资金操作日志失败，资金类型[FundFieldType]参数不正确：[" + fundOperLog.getFundFieldType().getName() + "]");
            throw new ServiceException("保存资金操作日志失败，资金类型[FundFieldType]参数不正确：[" + fundOperLog.getFundFieldType().getName() + "]");
        }
    }
    @Transactional(readOnly = false)
    public void insert(String shopId, String fieldTypeName, BigDecimal fund, BigDecimal remainFund, String relateId, int fundType, String note){
        insert(FundOperLog.create(shopId,fieldTypeName,fund,remainFund,relateId,fundType,note));
    }
}
