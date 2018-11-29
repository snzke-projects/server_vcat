package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ProductForecastDao;
import com.vcat.module.ec.entity.ProductForecast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品上架预告Service
 */
@Service
@Transactional(readOnly = true)
public class ProductForecastService extends CrudService<ProductForecastDao, ProductForecast> {
	@Autowired
	private MessageService messageService;
	@Autowired
	private ProductService productService;

    @Override
    public List<ProductForecast> findList(ProductForecast entity) {
        return super.findList(entity);
    }

    @Transactional(readOnly = false)
	public Integer activate(ProductForecast productForecast) throws Exception {
		if(dao.checkSameProductActivated(productForecast)){
			throw new Exception("该商品不能重复参与上架预告！");
		}
		Integer i = dao.activate(productForecast);
		if(1==i){
			// 自动上架商品
			productForecast = get(productForecast);
			productService.autoLoadByForecast(productForecast.getProduct(), productForecast.getForecastDate());

			// 推送未读隐匿消息
//			messageService.pushAllHideNotRead(PushService.MSG_TYPE_TRAILER);
		}
		return i;
	}

}
