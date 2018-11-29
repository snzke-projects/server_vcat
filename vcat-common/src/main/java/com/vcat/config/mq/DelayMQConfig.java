package com.vcat.config.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ylin on 2016/5/19.
 */
public class DelayMQConfig {
    public static final String DELAY_EXCHANGE = "delay.exchange";

    private static final String DELAY_MAIN_QUEUE = "delay.queue.";
    //关闭拼团
    public static final String CLOSE_GROUP_BUY_NAME = DELAY_MAIN_QUEUE+"closeGroupBuy";
    //新用户推送消息
    public static final String REGISTER_MESSAGE_NAME = DELAY_MAIN_QUEUE+"registerMessage";
    //自动评论
    public static final String REVIEW_NAME = DELAY_MAIN_QUEUE+"review";
    // 自动取消为支付拼团订单
    public static final String CANCEL_TIMEOUT_ORDER_NAME = DELAY_MAIN_QUEUE + "cancelOrder";

    private List<String> delayNames = new ArrayList<>(10);

    public DelayMQConfig(){
        delayNames.add(REGISTER_MESSAGE_NAME);
        delayNames.add(REVIEW_NAME);
        delayNames.add(CLOSE_GROUP_BUY_NAME);
        delayNames.add(CANCEL_TIMEOUT_ORDER_NAME);
    }

    @Bean
    public Exchange delayExchange(){
        DirectExchange directExchange = new DirectExchange(DELAY_EXCHANGE);
        directExchange.setDelayed(true);
        return directExchange;
    }

    @Bean
    public List<Binding> bindingDelay() {
        List<Binding> bindings = new ArrayList<>(10);
        for(String queueName : delayNames){
            String qn = queueName;
            bindings.add(new Binding(qn, Binding.DestinationType.QUEUE, DELAY_EXCHANGE, qn, null));
        }
        return bindings;
    }

    @Bean
    public List<Queue> delayMainQueue() {
        List<Queue> queues = new ArrayList<>(10);
        for(String queueName : delayNames){
            queues.add(new Queue(queueName, true, false, false));
        }
        return queues;
    }
}
