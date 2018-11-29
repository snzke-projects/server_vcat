package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.ProductPerformanceLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface ProductDao extends CrudDao<Product> {
    void archived(@Param(value = "id")String id,@Param(value = "archived")Integer archived);

    /**
     * 根据ID数组获取商品集合
     * @param ids
     * @return
     */
    List<Product> findListByIds(String[] ids);

    /**
     * 自动上架
     * @param product
     * @return
     */
    Integer autoLoadByForecast(Product product);

    /**
     * 下架小店商品
     * @param productId
     * @param shopId
     */
    void archivedShopProduct(@Param("productId")String productId, @Param("shopId")String shopId);

    /**
     * 查看该品牌与商品分类是否已存在
     * @param brandId
     * @param categoryId
     * @return
     */
    boolean hasBrandCategory(@Param(value="brandId")String brandId,@Param(value="categoryId")String categoryId);

    /**
     * 插入品牌与商品分类关系表
     * @param id
     * @param brandId
     * @param categoryId
     */
    void insertBrandCategory(@Param(value="id")String id,@Param(value="brandId")String brandId,@Param(value="categoryId")String categoryId);

    /**
     * 检查是否需要删除品牌与商品分类关系表
     * @param brandId
     * @param categoryId
     * @return
     */
    Boolean needDeleteBrandCategory(@Param(value="brandId")String brandId,@Param(value="categoryId")String categoryId);
    /**
     * 删除品牌分类关联
     * @param brandId
     * @param categoryId
     */
    void deleteBrandCategory(@Param(value="brandId")String brandId,@Param(value="categoryId")String categoryId);

    /**
     * 批量保存商品排序
     * @param id 商品ID数组
     * @param displayOrder 商品排序数组
     */
    void updateOrder(@Param(value = "id")String id, @Param(value = "displayOrder")String displayOrder);

    /**
     * 恢复已删除的商品
     * @param product
     */
    void recover(Product product);

    /**
     * 查询该商品是否可上架
     * @param id 商品ID
     * @return
     */
    Boolean getCanBeArchived(String id);

    /**
     * 置顶商品
     * @param product
     */
    void stick(Product product);

    /**
     * 添加商品销售及上架业绩
     * @param entity
     */
    void addPerformance(ProductPerformanceLog entity);

}
