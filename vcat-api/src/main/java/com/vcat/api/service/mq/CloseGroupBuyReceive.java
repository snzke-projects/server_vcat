package com.vcat.api.service.mq;

import com.vcat.api.service.GroupBuyService;
import com.vcat.common.utils.IdGen;
import com.vcat.config.mq.DelayMQConfig;
import com.vcat.mq.DelaySendReceive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 关闭失败拼团
 */
@Component
public class CloseGroupBuyReceive extends DelaySendReceive {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloseGroupBuyReceive.class);

    @Autowired
    private GroupBuyService groupBuyService;

    /**
     * 添加关闭拼团任务
     * @param sponsorId
     * @param invokeTime
     */
    public void addCloseGroupBuyJob(String sponsorId, Date invokeTime){
        delaySend(DelayMQConfig.CLOSE_GROUP_BUY_NAME, sponsorId, invokeTime);
        LOGGER.info("CloseGroupBuyReceive["+sponsorId+"]关闭拼团任务设置成功");
    }

    @RabbitListener(queues = DelayMQConfig.CLOSE_GROUP_BUY_NAME)
    public void handleCloseGroupBuyReceive(String sponsorId) {
        MDC.put("request_id",("handleCloseGroupBuyReceive@" + IdGen.getRandomNumber(10)));
        LOGGER.info("CloseGroupBuyReceive["+sponsorId+"]---------关闭拼团任务开始");

        groupBuyService.closeWithTimeOut(sponsorId);

        LOGGER.info("CloseGroupBuyReceive["+sponsorId+"]---------关闭拼团任务完成");
        MDC.remove("request_id");
    }

}
