package com.vcat.module.ec.service;

import com.vcat.common.persistence.Page;
import com.vcat.module.core.service.LRTreeService;
import com.vcat.module.core.service.ServiceException;
import com.vcat.module.ec.dao.TopicDao;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.Topic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TopicService extends LRTreeService<TopicDao, Topic> {

    /**
     * 递归激活或取消激活该专题
     * @param topic
     */
    @Transactional(readOnly = false)
    public void activate(Topic topic) {
        Integer activate = topic.getIsActivate();
        topic = get(topic);
        topic.setIsActivate(activate);
        dao.activate(topic);
    }

    public int getHighestOrder(String parentId) {
        return dao.getHighestOrder(parentId);
    }

    public Page<Product> findSelectProductPage(Page page,Product product) {
        product.setPage(page);
        productDataScopeFilter(product,"distribution",null);
        page.setList(dao.selectedProductList(product));
        return page;
    }

    @Transactional(readOnly = false)
    public void saveOrder(String[] refIds, String[] displayOrders) {
        if(null == refIds || refIds.length == 0){
            return;
        }
        if(refIds.length != displayOrders.length){
            throw new ServiceException("保存商品排序失败：商品ID数组长度与商品排序数组长度不一致[refIds:" + refIds.length + ",displayOrders:" + displayOrders.length + "]");
        }
        for (int i = 0; i < refIds.length; i++) {
            dao.updateProductOrder(refIds[i], displayOrders[i]);
        }
    }

    @Transactional(readOnly = false)
    public void cancelSelect(String topicId, String productId) {
        dao.cancelSelect(topicId,productId);
    }

    @Transactional(readOnly = false)
    public void batchCancelSelect(String topicId, String[] productIdArray) {
        dao.batchCancelSelect(topicId,productIdArray);
    }

    @Transactional(readOnly = false)
    public void selectProduct(String topicId, String productId) {
        dao.selectProduct(topicId,productId);
    }

    @Transactional(readOnly = false)
    public void batchSelectProduct(String topicId, String[] productIdArray) {
        dao.batchSelectProduct(topicId,productIdArray);
    }

    @Transactional(readOnly = false)
    public void selectAllProduct(Product product) {
        dao.selectAllProduct(product);
    }

    public boolean checkHasProduct(String topicId) {
        return dao.hasProduct(topicId);
    }

    @Transactional(readOnly = false)
    public void cancelAllSelect(String topicId) {
        dao.cancelAllSelect(topicId);
    }
}

