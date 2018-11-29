package com.vcat.module.ec.service;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.GroupBuyingDao;
import com.vcat.module.ec.dao.ProductDao;
import com.vcat.module.ec.entity.*;
import com.vcat.module.ec.service.scheduled.ArchivedProductJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Transactional(readOnly = true)
public class GroupBuyingService extends CrudService<GroupBuyingDao, GroupBuying> {
    @Autowired
    private ProductLimitService productLimitService;
    @Autowired
    private ProductItemService productItemService;
    @Autowired
    private SpecService specService;
    @Autowired
    private ProductDao productDao;

    @Override
    @Transactional(readOnly = false)
    public void save(GroupBuying entity) {
        boolean isNew = entity.getIsNewRecord();

        // 保存团购规格
        saveItem(entity);

        // 保存团购限购信息
        saveLimit(entity, isNew);

        // 设置团购商品下架任务
        setArchivedJob(entity, isNew);

        super.save(entity);
    }

    /**
     * 设置团购商品下架任务
     * @param entity
     * @param isNew
     */
    private void setArchivedJob(GroupBuying entity, boolean isNew) {
        if(!isNew){
            ArchivedProductJob.deleteArchivedProductJob(entity.getProduct().getId());
        }
        ArchivedProductJob.pushArchivedProductJob(entity.getProduct().getId(), 1, entity.getEndDate());
    }

    /**
     * 保存商品限购信息
     * @param entity
     * @param isNew
     */
    private void saveLimit(GroupBuying entity, boolean isNew) {
        ProductLimit limit = new ProductLimit();
        limit.setProductItem(entity.getProductItem());
        limit.setStartTime(new Date());
        limit.setEndTime(entity.getEndDate());
        limit.setInterval(entity.getPeriod());
        limit.setTimes(entity.getTimes());
        limit.setProductType(7);    // 团购活动限购类型
        limit.setUserType(0);       // 买家和卖家同事限购

        if(entity.getIsLimit()){
            if(!isNew){
                limit.setId(entity.getLimitId());
            }
            productLimitService.save(limit);
        }else if(!isNew && 0 == entity.getStatus() && !entity.getIsLimit()){
            productLimitService.deleteByGroupBuying(entity.getProductItem().getId());
        }
    }

    /**
     * 保存团购规格
     * @param entity
     */
    private void saveItem(GroupBuying entity) {
        String oldProductId = entity.getSqlMap().get("oldProductId");
        String oldProductItemId = entity.getSqlMap().get("oldProductItemId");
        // 判断是否修改团购商品
        boolean changeProduct = StringUtils.isNotBlank(oldProductId) && !entity.getProduct().getId().equals(oldProductId);

        if(changeProduct){
            // 已修改则删除旧团购规格
            ProductItem item = new ProductItem();
            item.setId(oldProductItemId);
            productItemService.delete(item);
        }else{
            // 未修改则修改团购规格信息
            entity.getProductItem().setId(oldProductItemId);
        }

        // 初始化团购规格
        entity.getProductItem().setCouponValue(BigDecimal.ZERO);
        entity.getProductItem().setCouponAllInventory(0);
        entity.getProductItem().setCouponPartInventory(0);
        entity.getProductItem().setProductItemType(1);  // 设置为团购规格
        entity.getProductItem().getSqlMap().put("productId", entity.getProduct().getId());
        productItemService.save(entity.getProductItem());

        // 保存规格属性
        Spec spec = new Spec();
        String itemName = entity.getProductItem().getName();
        String specName = "规格";
        String specValue = itemName;
        if(itemName.indexOf("：") > 0){
            String[] itemNameArray = itemName.split("：");
            if(itemNameArray.length == 2){
                specName = itemNameArray[0];
                specValue = itemNameArray[1];
            }
        }

        spec.setProductItem(entity.getProductItem());
        spec.setName(specName);
        spec.setValue(specValue);
        specService.deleteByProductItemId(entity.getProductItem().getId());
        specService.insertSpecValue(spec);
    }

    @Transactional(readOnly = false)
	public Integer activate(GroupBuying groupBuying) throws Exception {
        Product p = productDao.get(groupBuying.getProduct());
        Assert.isTrue(p.getArchived() == 0, "该商品已下架，激活失败！");
        GroupBuying another = getAnother(groupBuying);
        Assert.isNull(another, "该商品已参加其他团购，激活失败！");
        groupBuying.preUpdate();
        return dao.activate(groupBuying);
	}

    public GroupBuying getAnother(GroupBuying groupBuying) {
        return dao.getAnother(groupBuying);
    }
}
