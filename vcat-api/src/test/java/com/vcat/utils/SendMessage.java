package com.vcat.utils;

import com.vcat.ApiApplication;
import com.vcat.api.service.MessageService;
import com.vcat.module.ec.entity.MessageEarning;
import com.vcat.module.ec.entity.Shop;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

/**
 * Created by Code.Ai on 16/4/29.
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class SendMessage {
    @Autowired
    private MessageService messageService;
    @Test
    public void test(){
        MessageEarning                   earning = new MessageEarning();
        Shop p       = new Shop();
        p.setId("");
        earning.setShop(p);
        earning.setType(MessageEarning.TYPE_SALE); //销售返佣类型
        earning.setConsumer("");
        earning.setEarning(new BigDecimal(0));
        earning.setOrderNumber("");
        earning.setProductName("");
        messageService.pushEarningMsgTask(earning);
    }
}
