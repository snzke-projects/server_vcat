package com.vcat.api.service.mq;

import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.vcat.common.push.PushService;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import com.vcat.config.mq.DelayMQConfig;
import com.vcat.module.content.dao.ArticleDao;
import com.vcat.module.content.entity.Article;
import com.vcat.module.ec.dao.CustomerDao;
import com.vcat.module.ec.dao.MessageDao;
import com.vcat.module.ec.entity.Customer;
import com.vcat.module.ec.entity.MessageRegister;
import com.vcat.module.ec.entity.Shop;
import com.vcat.mq.DelaySendReceive;
import com.vcat.mq.RegArticleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ylin on 2016/5/20.
 */
@Component
public class RegisterMessageSendReceive extends DelaySendReceive {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterMessageSendReceive.class);

    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private CustomerDao customerDao;

    /**
     * 设置注册消息任务
     * @param customerId 需要提醒的小店ID
     */
    public void addRegisterMessageJob(String customerId,List<MessageRegister> list){
        if(null == list || list.isEmpty()){
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            MessageRegister messageRegister = list.get(i);
            RegArticleMessage regArticleMessage = new RegArticleMessage();
            regArticleMessage.setCustomerId(customerId);
            regArticleMessage.setArticleId(messageRegister.getArticle().getId());

            if(null == messageRegister.getArticle() || StringUtils.isEmpty(messageRegister.getArticle().getId())){
                String messageName = "MessageJob_" + customerId + "_" + i + "_" + messageRegister.getIntervalShow();
                LOGGER.error("设置任务[" + messageName + "]失败：文章或文章ID为空");
                continue;
            }
            delaySend(DelayMQConfig.REGISTER_MESSAGE_NAME,regArticleMessage,TimeUnit.MINUTES.toMillis(
                    messageRegister.getInterval() == null ? 1 : messageRegister.getInterval()));
        }
    }

    @RabbitListener(queues = DelayMQConfig.REGISTER_MESSAGE_NAME)
    public void handleMessage(RegArticleMessage regArticleMessage) {
        LOGGER.debug("      接收"+ regArticleMessage.toString());

        String customerId = regArticleMessage.getCustomerId();

        String articleId = regArticleMessage.getArticleId();

        if(StringUtils.isEmpty(customerId) || StringUtils.isEmpty(articleId)){
            LOGGER.warn("发送消息任务失败：参数不完整customerId["+customerId+"],articleId["+articleId+"]");
            return;
        }

        Customer customer = customerDao.select(customerId);

        Article article = articleDao.get(articleId);

        com.vcat.module.ec.entity.Message systemMessage = new com.vcat.module.ec.entity.Message();
        systemMessage.preInsert();
        systemMessage.setTitle(article.getTitle());
        systemMessage.setThumb(article.getImage());
        systemMessage.setIntro(article.getDescription());
        systemMessage.setArticle(article);
        systemMessage.setType(com.vcat.module.ec.entity.Message.PERSONAL);
        messageDao.insert(systemMessage);

        Shop shop = new Shop();
        shop.setId(customer.getId());
        systemMessage.setShop(shop);
        systemMessage.getSqlMap().put("flagId", IdGen.uuid());
        messageDao.insertFlag(systemMessage);

        if(null == customer.getDeviceType() || StringUtils.isEmpty(customer.getPushToken())){
            LOGGER.warn("发送消息任务失败：推送消息失败："+customer.getUserName()+"用户设备类型["+customer.getDeviceType()+"]或PushToken["+customer.getPushToken()+"]为空");
            return;
        }

        Message message = PushService.createMessage(StringUtils.abbr(article.getTitle() ,50));
        MessageIOS messageIOS = PushService.createMessageIOS(StringUtils.abbr(article.getTitle() ,50));

        if(customer.isAndroid()){
            PushService.pushSingleDevice(customer.getPushToken(),message);
        }else if(customer.isIOS()){
            PushService.pushSingleDevice(customer.getPushToken(),messageIOS);
        }

        LOGGER.info("发送消息任务END");
    }

}
