package com.vcat.api.service;

import com.vcat.ApiApplication;
import com.vcat.common.thirdparty.WeixinClient;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.ec.entity.Payment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
//@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Rollback(true)
public class OrderServiceTest extends
		AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private OrderService orderService;

	@Test
	public void testApplyTopicRule() {
//		Map<String, Object> rule = orderService.getProductLimit("b2cbc661abf44e36897dcd1fe0b66bc0", "2");
//		if(!orderService.applyLimit(rule, "b2cbc661abf44e36897dcd1fe0b66bc0", "2", "2","04504ee2ebdc4b72acd26d530286db46",0)){
//			int interval = (Integer)rule.get("interval");
//			int times = (Integer)rule.get("times");
//			System.out.println("本商品在" + (interval + 1) + "天内，每人限购" + times + "件！");
//		}
	}

	@Test
	public void getWeixinInfoByPaymentTest() {
		Payment payment = new Payment();
		payment.setId("2ca0c22946a94d759fdda5e38df91e7f");
		String openId = "ov4RDswMFhNIsf9A_b3pIIeLaGiw";
		List<Map<String, Object>> results = orderService.getWeixinInfoByPayment(payment);
		for(Map<String,Object> result : results) {
			if (StringUtils.isNullBlank(openId)) {
				openId = (String) result.get("openId");
			}
			logger.debug("查询getWeixinInfoByPayment:" + result);
			if (result != null && result.size() != 0 &&
					!StringUtils.isBlank(openId)) {
				WeixinClient.sendOrderSuccessMsg(openId,
						(String) result.get("productName"),
						(String) result.get("id"),
						String.valueOf(result.get("price")));
			}
		}
	}



}
