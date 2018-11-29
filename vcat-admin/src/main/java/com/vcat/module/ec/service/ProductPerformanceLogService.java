package com.vcat.module.ec.service;

import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.*;
import com.vcat.module.ec.entity.Customer;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.ProductPerformanceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductPerformanceLogService extends CrudService<ProductPerformanceLogDao, ProductPerformanceLog> {
    @Autowired
    private ProductDao productDao;
    @Autowired
    private ReviewDetailDao reviewDetailDao;
    @Autowired
    private RatingSummaryDao ratingSummaryDao;
    @Autowired
    private CustomerDao customerDao;

    @Transactional(readOnly = false)
    public String addPerformance(ProductPerformanceLog entity) {
        String productIds = entity.getSqlMap().get("productIds");
        int allAddReviewCount = 0;
        if(StringUtils.isNotEmpty(productIds)){
            String[] productArray = productIds.split("\\|");
            int reviewCount = entity.getReviewCount();
            boolean needAddReview = reviewCount > 0;
            for (int i = 0; i < productArray.length; i++) {
                String productId = productArray[i];
                Product product = new Product();
                product.setId(productId);
                entity.setProduct(product);
                entity.preInsert();
                productDao.addPerformance(entity);
                int addReviewCount = 0;
                if(needAddReview){
                    Customer randomCustomerQueryParam = new Customer();
                    randomCustomerQueryParam.setRegistered(0);
                    for (int j = 0; j < reviewCount; j++) {
                        String id = IdGen.uuid();
                        String randomCustomerId = customerDao.getRandomId(randomCustomerQueryParam);
                        addReviewCount += ratingSummaryDao.addSummary(id,randomCustomerId,productId);
                        reviewDetailDao.addReview(id,randomCustomerId,productId);
                    }
                }
                allAddReviewCount += addReviewCount;
                entity.setReviewCount(addReviewCount);
                dao.insert(entity);
                ratingSummaryDao.updateRating(productId);
            }
        }
        return "共添加["+allAddReviewCount+"]条评论";
    }
}
