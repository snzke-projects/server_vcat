package com.vcat.api.service;

import com.vcat.ApiApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by Code.Ai on 16/6/22.
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
@Rollback(true)
public class MessageServiceTest extends
        AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private MessageService messageService;
    @Test
    public void testPushRemindMessage() throws Exception {
        String shopId = "982943d9a198479a972f20babffe9278";
        String title = "喵主，有新成员加入您的团队了！";
        String content = "新成员小店名:" + "v1234" + "\r\n注册手机号：" + "18628362906";
        messageService.pushRemindMessage(shopId,title,content);
    }
}