package com.vcat.api.service;

import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.core.utils.DictUtils;
import com.vcat.module.ec.dao.UpgradeRequestDao;
import com.vcat.module.ec.entity.Shop;
import com.vcat.module.ec.entity.ShopFund;
import com.vcat.module.ec.entity.UpgradeRequest;
import com.vcat.module.ec.entity.UpgradeRequestLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional(readOnly = true)
public class UpgradeRequestService extends CrudService<UpgradeRequest> {
    @Autowired
    private UpgradeRequestDao upgradeRequestDao;
    @Autowired
    private ShopService          shopService;
    @Autowired
    private ShopFundService shopFundService;
    @Override
    protected CrudDao<UpgradeRequest> getDao() {
        return upgradeRequestDao;
    }

    @Transactional(readOnly = false)
    public Object getMyShopApplyStatus(String shopId){
        BigDecimal defaultSalesLimit = new BigDecimal(DictUtils.getDictValue("ec_upgrade_sales_limit",""));
        ShopFund   fund              = shopFundService.getShopFund(shopId);
        BigDecimal nowSales          = (fund.getTotalSale() == null ? new BigDecimal(0) : fund.getTotalSale()).add(fund.getSaleHoldFund());
        Map<String,Object> map = new HashMap<>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);

        Shop pp = shopService.get(shopId);
        if(pp.getAdvancedShop() == 2){
            map.put("defaultSalesLimit",new BigDecimal(0));
            map.put("nowSales",new BigDecimal(10));
        }else { // 0
            map.put("defaultSalesLimit",defaultSalesLimit);
            map.put("nowSales",nowSales);
        }
        UpgradeRequest upgradeRequest = new UpgradeRequest();
        Shop    shop = new Shop();
        shop.setId(shopId);
        upgradeRequest.setShop(shop);
        upgradeRequest = upgradeRequestDao.get(upgradeRequest);
        if(upgradeRequest == null){
            map.put("applyStatus",-1);
        }
        else{
            map.put("applyStatus",upgradeRequest.getStatus());
        }
        return map;
    }

    public List<UpgradeRequestLog> findLogs(String shopId){
        return upgradeRequestDao.findLog(shopId);
    }
}
