package com.vcat.api.service;

import com.vcat.ApiApplication;
import com.vcat.common.sms.SmsClient;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.ec.dto.GroupBuySponsorDto;
import com.vcat.module.ec.entity.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Code.Ai on 16/6/14.
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
//@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Rollback(true)
public class UpdateGroupBuyStatus extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private GroupBuySponsorService groupBuySponsorService;
    @Autowired
    private GroupBuyCustomerService groupBuyCustomerService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;

    @Test
    @Transactional
    public void test(){
        GroupBuySponsorDto groupBuy = new GroupBuySponsorDto();
        String groupBuySponsorId = "a26a6978c05e44dc9bd8e298281996ea";
        groupBuy.setId(groupBuySponsorId);
        groupBuy = groupBuySponsorService.getGroupBuySponsor(groupBuy);
        // 更新此拼团的状态

        groupBuy.setStatus(2);
        groupBuySponsorService.updateGroupBuySponsor(groupBuy);
        // 给拼团者发送拼团成功短信
        // 参团者信息
        List<Map<String, Object>> joinedCustomers = groupBuyCustomerService.getJoinedCustomers(groupBuySponsorId);
        for(Map<String, Object> customerInfo :joinedCustomers){
            String phone = (String) customerInfo.get("phone");
            String productName = StringUtils.StringFilter((String)customerInfo.get("productName"));
            String text = "【V猫小店】亲爱的喵主，恭喜您，您拼团的商品\"" + productName + "\"已拼团成功！";
            try {
                SmsClient.sendSms(text,phone);
            } catch (IOException e) {

            }
        }
        // 分红
        // 根据 groupBuySponsorId 找出所有参团者订单
        List<Map<String, Object>> joinedCustomerList = groupBuyCustomerService.getJoinedCustomers(groupBuySponsorId);
        Product                   product            = new Product();
        product.setId("51014f08f29046fbb5aaf48585f00534");
        product = productService.get(product);
        // 如果是拼团成功且是不可退款的订单,拼团成功后将待确认收入变为可提现收入
        if(joinedCustomerList != null && !product.getCanRefund()){
            // 拼团成功待发货时将待确认收入变为可提现收入
            joinedCustomerList.stream().filter(joinedCustoemr -> !StringUtils.isBlank((String) joinedCustoemr.get("orderId")))
                    .forEach(joinedCustoemr -> {
                        // 拼团成功待发货时将待确认收入变为可提现收入
                        orderService.checkShippingEarning((String) joinedCustoemr.get("buyerId"), (String) joinedCustoemr.get("orderId"), 3);
                    });
        }

    }
}
