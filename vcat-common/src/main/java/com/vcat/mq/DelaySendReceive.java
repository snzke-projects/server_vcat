package com.vcat.mq;

import com.vcat.common.utils.DateUtils;
import com.vcat.config.mq.DelayMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by ylin on 2016/5/20.
 */
@Component
public class DelaySendReceive {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelaySendReceive.class);

    @Autowired
    protected RabbitTemplate sender;


    /**
     * 发送消息到延时队列
     * @param message  消息
     * @param expiration  需要延长的时间，单位毫秒
     */
    public void delaySend(String routingkey, Object message,long expiration){
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setDelay((int)expiration);
        LOGGER.debug("      发送延时消息：delay="+expiration+"毫秒，执行时间：" + DateUtils.formatDateTime(new Date(new Date().getTime() + expiration)));
        sender.send(DelayMQConfig.DELAY_EXCHANGE, routingkey,
                sender.getMessageConverter().toMessage(message,messageProperties));
   }

    /**
     * 发送消息到延时队列
     * @param message 消息
     * @param invokeTime 任务执行时间
     */
    public void delaySend(String routingkey, Object message,Date invokeTime){
        Date now = new Date();
        Long expiration = invokeTime.getTime() - now.getTime();
        if(expiration < 0){
            return;
        }
        delaySend(routingkey, message, expiration.intValue());
    }
}
