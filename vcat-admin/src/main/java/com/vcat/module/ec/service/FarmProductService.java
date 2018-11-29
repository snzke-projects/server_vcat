package com.vcat.module.ec.service;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.FarmProductDao;
import com.vcat.module.ec.entity.FarmProduct;
import com.vcat.module.ec.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class FarmProductService extends CrudService<FarmProductDao, FarmProduct> {
    @Override
    @Transactional(readOnly = false)
    public void save(FarmProduct entity) {
        dao.deleteFarmProduct(entity.getFarm().getId());
        dao.addFarmProduct(entity.getFarm().getId(),entity.getProductIds().split("\\|"));
    }

    /**
     * 检查重复庄园商品
     * @param farmId
     * @param productIds
     * @return
     */
    public List<Product> checkSame(String farmId, String productIds) {
        if(StringUtils.isNotBlank(productIds)){
            return dao.checkSame(farmId, productIds.split("\\|"));
        }
        return new ArrayList<>();
    }

    /**
     * 修改庄园商品的服务微信号
     * @param farmProduct
     */
    @Transactional(readOnly = false)
    public void updateWechatNo(FarmProduct farmProduct) {
        dao.updateWechatNo(farmProduct);
    }
}
