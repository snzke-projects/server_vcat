package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.Promotion;

import java.util.List;

@MyBatisDao
public interface PromotionDao extends CrudDao<Promotion> {
    /**
     * （取消）激活优惠卷
     * @param promotion
     */
    void activate(Promotion promotion);

    /**
     * 删除商品关联
     * @param promotion
     */
    void deleteProduct(Promotion promotion);

    /**
     * 添加商品关联
     * @param promotion
     */
    void addProduct(Promotion promotion);

    /**
     * 根据优惠卷获取商品列表
     * @param result
     * @return
     */
    List<Product> findProductListByPromotion(Promotion result);
}
