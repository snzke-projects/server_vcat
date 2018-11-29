package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.FarmProduct;
import com.vcat.module.ec.entity.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface FarmProductDao extends CrudDao<FarmProduct> {
    /**
     * 删除庄园商品
     * @param farmId
     */
    void deleteFarmProduct(String farmId);

    /**
     * 添加庄园商品
     * @param farmId
     * @param productIdArray
     */
    void addFarmProduct(@Param("farmId")String farmId, @Param("productIdArray") String[]productIdArray);

    /**
     * 检查重复庄园商品
     * @param farmId
     * @param productIdArray
     * @return
     */
    List<Product> checkSame(@Param("farmId") String farmId, @Param("productIdArray") String[] productIdArray);

    /**
     * 修改服务微信号
     * @param farmProduct
     */
    void updateWechatNo(FarmProduct farmProduct);
}
