package com.vcat.module.ec.service;

import com.vcat.common.persistence.Page;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.RecommendDao;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.RecommendEntity;
import com.vcat.module.ec.entity.ReserveRecommend;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品Service
 */
@Service
@Transactional(readOnly = true)
public class RecommendService extends CrudService<RecommendDao, RecommendEntity> {
    @Override
    @Transactional(readOnly = false)
    public void save(RecommendEntity entity) {
        boolean isNew = StringUtils.isEmpty(entity.getId());
        boolean isReserve = ReserveRecommend.RECOMMEND_RESERVE.equals(entity.getTypeCode());
        if(isReserve){
            String ids = entity.getSqlMap().get("productIds");
            deleteProductHotFlag(entity.getSqlMap().get("hotRecommendId"));
            String [] idArray = ids.split("\\|");
            dao.unActivateHistoryReserve(idArray);
            for (String id : idArray) {
                entity.preInsert();
                dao.insert(entity);
                Product product = new Product();
                product.setId(id);
                entity.setProduct(product);
                entity.getSqlMap().put("productRecommendId", IdGen.uuid());
                dao.insertProductRecommend(entity);
            }
        }else{
            super.save(entity);
            if(isNew){
                entity.getSqlMap().put("productRecommendId", IdGen.uuid());
                dao.insertProductRecommend(entity);
            }
        }
    }

    private void deleteProductHotFlag(String hotRecommendId){
        if(StringUtils.isNotBlank(hotRecommendId)){
            String[] hotRecommendIdArray = hotRecommendId.split("\\|");
            for (int i = 0; i < hotRecommendIdArray.length; i++) {
                deleteProductRecommend(hotRecommendIdArray[i]);
                deleteHotRecommend(hotRecommendIdArray[i]);
            }
        }
    }

    public void insertProductRecommend(String productId,String recommendId){
        RecommendEntity sr = new RecommendEntity();
        Product p = new Product();
        p.setId(productId);
        sr.getSqlMap().put("productRecommendId", IdGen.uuid());
        sr.setProduct(p);
        sr.setId(recommendId);
        dao.insertProductRecommend(sr);
    }

    public void deleteProductRecommend(String recommendId){
        dao.deleteProductRecommend(recommendId);
    }

    public Page<RecommendEntity> findReservePage(Page<RecommendEntity> page, RecommendEntity entity) {
        entity.setPage(page);
        page.setList(dao.findReserveList());
        return page;
    }

    public RecommendEntity getReserve(RecommendEntity reserveRecommend){
        return dao.getReserve(reserveRecommend);
    }

    public void deleteHotRecommend(String recommendId){
        dao.deleteHotRecommend(recommendId);
    }
    public void insertHotRecommend(Product product){
        dao.insertHotRecommend(product);
    }

    /**
     * 获取正在进行预购活动的商品列表
     * @param productIds
     * @return
     */
    public List<Product> getExistsReserve(String productIds) {
        if(StringUtils.isBlank(productIds)){
            return new ArrayList<>();
        }
        return dao.getExistsReserve(productIds.split("\\|"));
    }

    /**
     * 获取热销商品列表
     * @param productIds
     * @return
     */
    public List<Product> getExistsHot(String productIds) {
        if(StringUtils.isBlank(productIds)){
            return new ArrayList<>();
        }
        return dao.getExistsHot(productIds.split("\\|"));
    }
}
