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
 * 主动确认收货7天增加收入job
 */
public class CheckShippingEarningJob extends QuartzJobBean {

	@Autowired
	private OrderService orderService;

	private static Logger LOG = Logger.getLogger(CheckShippingEarningJob.class);

	@Override
	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
		LOG.info("checkShippingEarningJob---------------------------------------------------------START");
		String dayLimit = DictUtils.getDictValue("ec_auto_check_shipping_earning_day_limit", "7");

		List<Map<String, Object>> list = orderService
				.getCheckShippingEarningList(StringUtils.isNumeric(dayLimit) ? Integer
						.parseInt(dayLimit) : 7);

		int count = 0;
		if (null != list && !list.isEmpty()) {
			for (Map<String, Object> map : list) {
				try {
					MDC.put("request_id",( "CheckShippingEarningJob@" + IdGen.getRandomNumber(10)));
					orderService.checkShippingEarning(map.get("buyerId") + "", map.get("orderId") + "",5);
					count++;
				} catch (Exception e) {
					LOG.error("checkShippingEarningJob确认收货7天增加收入[" + map.get("orderId") + "]失败：" + e.getMessage(),e);
				} finally {
					MDC.remove("request_id");
				}
			}
		}
		LOG.info("checkShippingEarningJob--------------------------------确认收货" + dayLimit + "天后增加收入完成，共处理 " + count + " 个订单");

	}
}
