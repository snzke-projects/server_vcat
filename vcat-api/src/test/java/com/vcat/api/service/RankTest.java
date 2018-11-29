package com.vcat.api.service;

import com.vcat.ApiApplication;
import com.vcat.module.ec.entity.*;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * 支持事物回滚的测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
//@TransactionConfiguration(transactionManager="transactionManager",defaultRollback=false)
@Rollback(false)
public class RankTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private ShopService shopService;
    @Autowired
    private ShopFundService shopFundService;
    @Autowired
    private MonthRankService monthRankService;
    @Autowired
    private AllRankService allRankService;
    private static Logger LOG = Logger.getLogger(RankTest.class);

	@BeforeClass
	public static void setUp() {}

    @Test
    public void testRank() throws JobExecutionException {
        {
            final List<Rank> monthRankList = shopService.getMonthRankList();
            final List<Rank> rankList = shopFundService.getRankList();
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
//                List<Rank> newMonthRankList = shopService.operationRank(
//                        monthRankList, phoneNumList);
//                if(newMonthRankList.size()>100){
//                    newMonthRankList =  newMonthRankList.subList(0, 100);
//                }
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
}
