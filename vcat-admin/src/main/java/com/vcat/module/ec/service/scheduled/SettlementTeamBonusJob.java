package com.vcat.module.ec.service.scheduled;

import com.vcat.module.ec.service.OrderService;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 结算团队分红任务
 */
public class SettlementTeamBonusJob extends QuartzJobBean {
	@Autowired
	private OrderService orderService;
	private static Logger LOG = Logger.getLogger(SettlementTeamBonusJob.class);

	@Override
	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
        LOG.info("SettlementTeamBonusJob---结算团队分红任务START");

        orderService.settlementTeamBonus();

		LOG.info("SettlementTeamBonusJob---结算团队分红任务END");
	}
}
