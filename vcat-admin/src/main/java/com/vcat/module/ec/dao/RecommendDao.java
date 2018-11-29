package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.RecommendEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface RecommendDao extends CrudDao<RecommendEntity> {
    void deleteHotRecommend(String recommendId);
    void insertHotRecommend(Product product);
    /**
     * 插入热销
     * @param recommend
     */
    void insertProductRecommend(RecommendEntity recommend);

    /**
     * 删除热销
     * @param recommendId
     */
    void deleteProductRecommend(String recommendId);

    /**
     * 获取预购列表
     * @return
     */
    List<RecommendEntity> findReserveList();

    /**
     * 获取预购
     * @param reserveRecommend
     * @return
     */
    RecommendEntity getReserve(RecommendEntity reserveRecommend);

    /**
     * 取消激活历史预购
     * @param idArray
     */
    void unActivateHistoryReserve(@Param(value = "idArray") String[] idArray);

    /**
     * 获取正在进行的预购活动商品列表
     * @param productIdArray
     * @return
     */
    List<Product> getExistsReserve(@Param(value = "productIdArray") String[] productIdArray);

    /**
     * 获取热销商品列表
     * @param productIdArray
     * @return
     */
    List<Product> getExistsHot(@Param(value = "productIdArray") String[] productIdArray);
}
