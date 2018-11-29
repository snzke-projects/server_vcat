package com.vcat.module.ec.dao;

import com.vcat.common.dao.LRTreeDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.core.dao.TreeDao;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.Topic;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface TopicDao extends LRTreeDao<Topic> {
    Topic findParent(Topic topic);
    Topic getRoot();
    /**
     * 验证是否有子专题
     * @param id
     * @return
     */
    boolean hasChild(String id);

    /**
     * (取消)激活
     * @param topic
     */
    void activate(Topic topic);

    /**
     * 获取子专题中最高排序
     * @param parentId
     * @return
     */
    int getHighestOrder(String parentId);

    /**
     * 获取已选择的专题商品
     * @param product
     * @return
     */
    List<Product> selectedProductList(Product product);

    /**
     * 更新已选商品排序
     * @param id
     * @param displayOrder
     */
    void updateProductOrder(@Param(value = "id")String id, @Param(value = "displayOrder")String displayOrder);

    /**
     * 取消选择专题商品
     * @param topicId
     * @param productId
     */
    void cancelSelect(@Param(value = "topicId")String topicId, @Param(value = "productId")String productId);

    /**
     * 批量取消选择商品
     * @param topicId
     * @param productIdArray
     */
    void batchCancelSelect(@Param(value = "topicId")String topicId, @Param(value = "productIdArray")String[] productIdArray);

    /**
     * 选择商品
     * @param topicId
     * @param productId
     */
    void selectProduct(@Param(value = "topicId")String topicId, @Param(value = "productId")String productId);

    /**
     * 批量选择商品
     * @param topicId
     * @param productIdArray
     */
    void batchSelectProduct(@Param(value = "topicId")String topicId,@Param(value = "productIdArray")String[] productIdArray);

    /**
     * 根据条件选择所有商品
     * @param product
     */
    void selectAllProduct(Product product);

    /**
     * 判断该专题下是否选择商品
     * @param topicId
     * @return
     */
    boolean hasProduct(String topicId);

    /**
     * 取消选择全部商品
     * @param topicId
     */
    void cancelAllSelect(String topicId);

    /**
     * 取消选择已下架商品
     * @param productId
     */
    void deleteByProductId(String productId);
}
