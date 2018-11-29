package com.vcat.mp;

import com.vcat.ApiApplication;
import com.vcat.api.service.mq.RegisterMessageSendReceive;
import com.vcat.api.service.mq.ReviewReceive;
import com.vcat.config.mq.DelayMQConfig;
import com.vcat.mq.RegArticleMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

/**
 * Created by ylin on 2016/5/20.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class RabbitMQTest {

    @Autowired
    private RegisterMessageSendReceive registerMessageSendReceive;

    @Autowired
    private ReviewReceive reviewReceive;

    @Test
    public void regMsgTest() throws InterruptedException {
        RegArticleMessage regArticleMessage = new RegArticleMessage();
        regArticleMessage.setCustomerId("123");
        regArticleMessage.setArticleId("234");

        registerMessageSendReceive.delaySend(DelayMQConfig.REGISTER_MESSAGE_NAME, regArticleMessage, (int)TimeUnit.MINUTES.toMillis(5));
        registerMessageSendReceive.delaySend(DelayMQConfig.REGISTER_MESSAGE_NAME, regArticleMessage, (int)TimeUnit.MINUTES.toMillis(1));
        //registerMessageSendReceive.delaySend(DelayNames.REGISTER_MESSAGE_NAME, regArticleMessage, (int)TimeUnit.MINUTES.toMillis(1));
    }

    @Test
    public void reviewTest() {
        reviewReceive.delaySend(DelayMQConfig.REVIEW_NAME, "88888888888", TimeUnit.MINUTES.toMillis(1));
    }

    @Test
    public void reviewReceiveTest() {
        reviewReceive.handleReviewReceive("1");
    }
}
