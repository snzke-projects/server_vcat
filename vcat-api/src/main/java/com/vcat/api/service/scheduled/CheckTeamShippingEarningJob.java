//package com.vcat.api.service.scheduled;
//
//import com.vcat.api.service.OrderService;
//import com.vcat.common.utils.StringUtils;
//import com.vcat.module.core.utils.DictUtils;
//import org.apache.log4j.Logger;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.quartz.QuartzJobBean;
//import java.util.List;
//import java.util.Map;
//
///**
// * 团队分红结算任务
// * 结算时间：每周日晚上七点
// * 这个需求实现不了
// * 在确认收货7天之后,会结算销售分红,之后会将订单状态变为已结算
// * 这时我需要根据订单状态为未结算的订单来结算团队分红
// * 所以查不出来
// * 需求改为在结算销售分红时,一起将团队分红结算
// */
//public class CheckTeamShippingEarningJob extends QuartzJobBean {
//    @Autowired
//    private OrderService orderService;
//    private static Logger LOG = Logger.getLogger(CheckTeamShippingEarningJob.class);
//    @Override
//    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        //String threadName = Thread.currentThread().getName();
//        //String threadId = Thread.currentThread().getId() + "";
//        //LOG.info("CheckTeamShippingEarningJob["
//        //        + threadName
//        //        + "]["
//        //        + threadId
//        //        + "]---------------------------------------------------------START");
//        //String dayLimit = DictUtils.getDictValue(
//        //        "ec_auto_check_shipping_day_limit", "7");
//        //
//        //List<Map<String, Object>> list = orderService
//        //        .getCheckShippingEarningList(StringUtils.isNumeric(dayLimit) ? Integer
//        //                .parseInt(dayLimit) : 7);
//        //
//        //int count = 0;
//        //if (null != list && !list.isEmpty()) {
//        //    for (Map<String, Object> map : list) {
//        //        try {
//        //            orderService.CheckTeamShippingEarning(map.get("buyerId") + "",
//        //                    map.get("orderId") + "",5);
//        //            count++;
//        //        } catch (Exception e) {
//        //            LOG.error("checkShippingEarningJob[" + threadName + "]["
//        //                    + threadId + "]确认收货7天增加收入[" + map.get("orderId")
//        //                    + "]失败：" + e.getMessage());
//        //        }
//        //    }
//        //}
//        //LOG.info("checkShippingEarningJob[" + threadName + "][" + threadId
//        //        + "]--------------------------------确认收货7天增加收入完成，共确认 " + count
//        //        + "个订单");
//    }
//}
