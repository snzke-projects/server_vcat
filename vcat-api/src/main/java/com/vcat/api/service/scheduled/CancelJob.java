package com.vcat.api.service.scheduled;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.vcat.api.service.OrderService;
import com.vcat.module.core.utils.DictUtils;

/**
 * 自动取消普通订单
 */
public class CancelJob extends QuartzJobBean{
	private static Logger LOG = Logger.getLogger(CancelJob.class);
	@Autowired
	private OrderService orderService;
	@Override
	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
		//查询出要取消的订单
		String date = DictUtils.getDictValue("ec_auto_close_order_day_limit","1");
		LOG.debug("autoCancelDate = "+date+"start----------");
		try{
			List<String> orderIdList = orderService.getOrderListBydealTime(Integer.parseInt(date));
			if(orderIdList!=null&&orderIdList.size()>0){
				for(String orderId:orderIdList)
					orderService.cancelOrder(orderId, null,5);
			}
		}catch(Exception e){
			LOG.error(e.getMessage());
		}
		LOG.debug("autoCancelDate = "+date+"end----------");
	}

}
