package com.vcat.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ProductTopicDao;
import com.vcat.module.ec.dto.ProductDto;
import com.vcat.module.ec.entity.ProductTopic;

@Service
@Transactional(readOnly = true)
public class ProductTopicService extends CrudService<ProductTopic> {
	@Autowired
	private ProductTopicDao productTopicDao;

	@Override
	protected CrudDao<ProductTopic> getDao() {
		return productTopicDao;
	}
	
	public ProductTopic findTopicById(String id){
		return productTopicDao.findTopicById(id);
	}
	
	public int countProductByTopic(List<String> topicId){
		topicId.add("-9999");
		return productTopicDao.countProductByTopic(topicId);
	}
	
	public List<ProductDto> getProductList(Pager page, List<String> topicId){
		topicId.add("-9999");
		return productTopicDao.findProductByTopic(page, topicId);
	}

	public void getTopicByPid(ProductTopic productTopic, int size, int depth) {
		if(depth == 0){
			return;
		}
		List<ProductTopic> topics = productTopicDao.findTopicByPid(productTopic.getId());
		Pager page = new Pager();
		page.setPageOffset(0);
		page.setPageSize(size);
		if(size > 0) {
			List<String> list = new ArrayList<String>();
			list.add(productTopic.getId());
			productTopic.setProducts(productTopicDao.findProductByTopic(page, list));
		}
		for (ProductTopic topic : topics) {
			getTopicByPid(topic,size,depth-1);
		}
		productTopic.setChildren(topics);
	}

	public void getChildrenByPid(ProductTopic productTopic) {
		List<ProductTopic> topics = productTopicDao.findTopicByPid(productTopic.getId());
		topics.forEach(this::getChildrenByPid);
		productTopic.setChildren(topics);
	}

	public void getChildrenIds(ProductTopic productTopic, List<String> ids){
		List<ProductTopic> topics = productTopic.getChildren();
		for (ProductTopic topic : topics) {
			ids.add(topic.getId());
			getChildrenIds(topic, ids);
		}

	}

	public List<String> getChildren(String topicId){
		ProductTopic topic = new ProductTopic();
		topic.setId(topicId);
		getChildrenByPid(topic);
		List<String> ids = new ArrayList<String>();
		getChildrenIds(topic, ids);
		return ids;
	}


}
