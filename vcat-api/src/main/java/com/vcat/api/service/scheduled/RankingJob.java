package com.vcat.api.service.scheduled;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.vcat.api.service.AllRankService;
import com.vcat.api.service.MonthRankService;
import com.vcat.api.service.ShopFundService;
import com.vcat.api.service.ShopService;
import com.vcat.module.ec.entity.Rank;

/**
 * 排行任务
 */
public class RankingJob extends QuartzJobBean {
	@Autowired
	private ShopService shopService;
	@Autowired
	private ShopFundService shopFundService;
	@Autowired
	private MonthRankService monthRankService;
	@Autowired
	private AllRankService allRankService;
	private static Logger LOG = Logger.getLogger(RankingJob.class);

	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
		List<Rank> monthRankList = shopService.getMonthRankList();
		List<Rank> rankList = shopFundService.getRankList();
		LOG.info("查出排行榜");
		monthRankService.truncate();
		allRankService.truncate();
		List<String> phoneNumList = allRankService.getPhoneNumList();
		if (phoneNumList == null || phoneNumList.isEmpty()) {
			for (Rank rank : monthRankList) {
				rank.preInsert();
				monthRankService.insert(rank);
			}
			for (Rank rank : rankList) {
				rank.preInsert();
				allRankService.insert(rank);
			}
		} else {
			LOG.info("处理排行榜");
			// 特殊阶段，从新处理数据 21-27号 参见ec_rank_test表
			List<Rank> newRankList = shopService.operationRank(rankList,
					phoneNumList);
			if(newRankList.size()>100){
				newRankList =  newRankList.subList(0, 100);
			}
//			List<Rank> newMonthRankList = shopService.operationRank(
//					monthRankList, phoneNumList);
//			if(newMonthRankList.size()>100){
//				newMonthRankList =  newMonthRankList.subList(0, 100);
//			}
//			for (Rank rank : newMonthRankList) {
//				rank.preInsert();
//				monthRankService.insert(rank);
//			}
			for (Rank rank : monthRankList) {
				rank.preInsert();
				monthRankService.insert(rank);
			}
			for (Rank rank : newRankList) {
				rank.preInsert();
				allRankService.insert(rank);
			}
		}
	}
}
