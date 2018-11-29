package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.ProductItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品规格DAO接口
 */
@MyBatisDao
public interface ProductItemDao extends CrudDao<ProductItem> {
    List<ProductItem> findListByProduct(Product product);
    // 根据商品逻辑删除规格
    void deleteByProduct(Product product);
    List<ProductItem> findListByIds(@Param("idArray")String[] idArray);

    /**
     * 退货成功增加规格库存
     * @param id
     * @param quantity
     */
    void addQuantityByRefund(@Param("id")String id, @Param("quantity")Integer quantity);
}
