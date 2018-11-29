package com.vcat.module.ec.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.ProductDto;
import com.vcat.module.ec.entity.ProductTopic;
@MyBatisDao
public interface ProductTopicDao extends CrudDao<ProductTopic>{
	List<ProductTopic> findTopicByPid(String pid);
	int countProductByTopic( @Param("topicId")List<String> topicId);
	List<ProductDto> findProductByTopic(@Param("page")Pager page, @Param("topicId")List<String> topicId);
	ProductTopic findTopicById(String id);
	void insertOrderTopic(@Param("id")String id, @Param("topicId")String topicId, @Param("orderItemId")String orderItemId);
	boolean isProductInTopic(String productItemId);
}
