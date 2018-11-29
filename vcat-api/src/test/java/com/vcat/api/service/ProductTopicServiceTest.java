package com.vcat.api.service;

import com.vcat.ApiApplication;
import com.vcat.common.persistence.Pager;
import com.vcat.module.ec.dto.ProductDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vcat.module.ec.entity.ProductTopic;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
//@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Rollback(true)
public class ProductTopicServiceTest extends
		AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private ProductTopicService productTopicService;

	@Test
	public void testGetTopics() {
		ProductTopic topic = new ProductTopic();
		topic.setId("85a4fa0c72224218939b43dc0b6116dc");
		productTopicService.getTopicByPid(topic,3,3);
		System.out.println(topic);
	}

	@Test
	public void testGetChildren() {
		System.out.println(productTopicService.getChildren("4"));
	}

	@Test
	public void testGetProductList() {
		Pager page = new Pager();
		page.setPageOffset(0);
		page.setPageSize(10);
		List<ProductDto> t = productTopicService.getProductList(page, productTopicService.getChildren("4"));
		System.out.println(t);
	}

}
