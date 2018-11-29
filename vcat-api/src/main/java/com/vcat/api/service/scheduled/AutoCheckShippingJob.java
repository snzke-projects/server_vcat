package com.vcat.api.service.scheduled;

import com.vcat.api.service.OrderService;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.utils.DictUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;
import java.util.Map;

/**
 * 自动确认收货
 */
public class AutoCheckShippingJob extends QuartzJobBean {
	@Autowired
	private OrderService orderService;
	private static Logger LOG = Logger.getLogger(AutoCheckShippingJob.class);

	@Override
	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
		String threadName = Thread.currentThread().getName();
		String threadId = Thread.currentThread().getId()+"";
		LOG.info("AutoCheckShippingJob["+threadName+"]["+threadId+"]---------------------------------------------------------START");
		String dayLimit = DictUtils.getDictValue("ec_auto_check_shipping_day_limit","15");
		// 查询发货日期后15天所有满足条件的订单
		List<Map<String,Object>> list = orderService.getAutoCheckShippingList(StringUtils.isNumeric(dayLimit) ? Integer.parseInt(dayLimit) : 15);

		int count = 0;
		if(null != list && !list.isEmpty()){
			for (Map<String,Object> map : list){
				try {
					MDC.put("request_id",( "AutoCheckShippingJob@" + IdGen.getRandomNumber(10)));
					orderService.autoCheckShipping(map.get("buyerId") + "",map.get("orderId") + "");
					count ++;
				} catch (Exception e) {
					LOG.error("AutoCheckShippingJob["+threadName+"]["+threadId+"]自动确认收货订单[" + map.get("orderId") + "]失败：" + e.getMessage());
				} finally {
					MDC.remove("request_id");
				}
			}
		}
		LOG.info("AutoCheckShippingJob["+threadName+"]["+threadId+"]--------------------------------自动确认收货完成，共确认 " + count + "个订单");
	}

}
