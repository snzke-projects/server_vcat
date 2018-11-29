package com.vcat.api.service.mq;

import com.vcat.api.service.OrderService;
import com.vcat.config.mq.DelayMQConfig;
import com.vcat.module.ec.dto.OrderDto;
import com.vcat.mq.DelaySendReceive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Code.Ai on 16/6/2.
 * Description:
 * 10分钟关闭团购未支付订单
 */
@Component
public class CancelOrderReceive extends DelaySendReceive {
    private static final Logger LOGGER = LoggerFactory.getLogger(CancelOrderReceive.class);
    @Autowired
    private OrderService    orderService;

    public void addCancelGroupBuyJob(String orderId){
        OrderDto order = new OrderDto();
        order.setId(orderId);
        order = orderService.getSellerOrderDetail(order,"");
        if(order != null){
            delaySend(DelayMQConfig.CANCEL_TIMEOUT_ORDER_NAME, orderId, order.getAutoCancelDate());
            LOGGER.info("CancelGroupBuyJob["+orderId+"]自动取消未支付拼团订单");
        }else{
            LOGGER.info("CancelGroupBuyJob["+orderId+"] is null");
        }
    }

    @RabbitListener(queues = DelayMQConfig.CANCEL_TIMEOUT_ORDER_NAME)
    public void handleCancelGroupBuyReceive(String orderId) {
        LOGGER.info("CloseGroupBuyReceive["+orderId+"]---------取消未支付订单开始");
        orderService.cancelOrderWithTimeOut(orderId);
        LOGGER.info("CloseGroupBuyReceive["+orderId+"]---------取消未支付订单完成");
    }
}