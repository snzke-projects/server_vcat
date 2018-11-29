package com.vcat.api.service.mq;

import com.vcat.api.service.OrderItemService;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import com.vcat.config.mq.DelayMQConfig;
import com.vcat.module.core.utils.DictUtils;
import com.vcat.module.ec.dao.RatingSummaryDao;
import com.vcat.module.ec.dao.ReviewDetailDao;
import com.vcat.module.ec.entity.Order;
import com.vcat.module.ec.entity.OrderItem;
import com.vcat.mq.DelaySendReceive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自动添加订单评论
 */
@Component
public class ReviewReceive extends DelaySendReceive {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewReceive.class);

    @Autowired
    private ReviewDetailDao reviewDetailDao;
    @Autowired
    private RatingSummaryDao ratingSummaryDao;
    @Autowired
    private OrderItemService orderItemService;

    /**
     * 自动添加订单评论
     * @param orderNumber 订单号
     */
    public void autoAddReviewJob(String orderNumber){
        Long time = new Long(1000 * 60 * 60 * 24 * 7);

        String reviewTime = DictUtils.getDictValue("ec_auto_review_time",(1000 * 60 * 60 * 24 * 7) + "");

        if(StringUtils.isNotBlank(reviewTime)){
            try {
                time = Long.parseLong(reviewTime);
            } catch (NumberFormatException e) {
                LOGGER.warn("字典[ec_auto_review_time:" + reviewTime + "]必须为数字类型！");
            }
        }

        delaySend(DelayMQConfig.REVIEW_NAME, orderNumber, time);
    }

    @RabbitListener(queues = DelayMQConfig.REVIEW_NAME)
    public void handleReviewReceive(String orderNumber) {
        String threadName = Thread.currentThread().getName();
        String threadId = Thread.currentThread().getId() + "";
        LOGGER.info("ReviewReceive["+threadName+"]["+threadId+"]--------------------------------------------------自动评论开始");

        OrderItem orderItem = new OrderItem();
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        orderItem.setOrder(order);

        List<OrderItem> orderItemList = orderItemService.findList(orderItem);

        orderItemList.forEach(item -> {
            if(reviewDetailDao.getReviewCount(item.getId()) > 0){
                LOGGER.warn("ReviewJob["+threadName+"]["+threadId+"]该订单["+orderNumber+"]已评论，跳过自动评论");
            }else{
                String id = IdGen.uuid();
                ratingSummaryDao.addSummary(id,item.getOrder().getBuyer().getId(),item.getProductItem().getProduct().getId());
                reviewDetailDao.addRandomReviewByItemId(id, item.getId());
                ratingSummaryDao.updateRating(item.getProductItem().getProduct().getId());
            }
        });

        LOGGER.info("ReviewReceive["+threadName+"]["+threadId+"]--------------------------------------------------自动评论完成");
    }

}
