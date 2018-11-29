package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.PromotionDao;
import com.vcat.module.ec.entity.Promotion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PromotionService extends CrudService<PromotionDao, Promotion> {
    @Override
    public Promotion get(Promotion entity) {
        Promotion result = super.get(entity);
        result.setProductList(dao.findProductListByPromotion(result));
        return result;
    }

    @Transactional(readOnly = false)
    public void activate(Promotion promotion) {
        dao.activate(promotion);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Promotion entity) {
        super.save(entity);
        dao.deleteProduct(entity);
        dao.addProduct(entity);
    }
}
