package com.vcat.api.service;

import com.vcat.ApiApplication;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.FundOperLog;
import com.vcat.module.ec.dao.ShopDao;
import com.vcat.module.ec.dto.GroupBuySponsorDto;
import com.vcat.module.ec.entity.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * 支持事物回滚的测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
//@TransactionConfiguration(transactionManager="transactionManager",defaultRollback=true)
@Rollback(true)
public class ServiceTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
    private ShareEarningService shareEarningService;
    @Autowired
    private ShareEarningLogService shareEarningLogService;
    @Autowired
    private ShopDao shopDao;
    @Autowired
    private ShopProductService shopProductService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private FundOperLogService fundOperLogService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private GroupBuyService groupBuyService;
    @Autowired
    private GroupBuySponsorService groupBuySponsorService;
    @Autowired
    private CustomerService customerService;
	@BeforeClass
	public static void setUp() {}

    @Test
    @Transactional
    public void test(){
//        shopFundService.withdrawCash("1884aa88a41f46c3bcfa09c701288fff","f27af1f2ef064f1995dead54a0c24d08",new BigDecimal("16"));
        Product product = new Product();
        product.setId("300b0bee3f68427cb169f1f261c1bd4d");
        ShareEarning earning = new ShareEarning();
        earning.setProduct(product);
        earning = shareEarningService.getAvaShareEarning(earning);
        ShareEarningLog shareLog = new ShareEarningLog();
        shareLog.setShopId("1884aa88a41f46c3bcfa09c701288fff");
        shareLog.setShareId(earning.getId());
        shareLog.preInsert();
        ThirdLoginType type = new ThirdLoginType();
        type.setCode("QQ");
        shareLog.setThirdLoginType(type);
        shareEarningLogService.saveLog(earning, shareLog, earning.getProduct().getName());
    }

    @Test
    @Transactional
    public void test1() throws SQLException {
       int t =  dataSource.getConnection().getTransactionIsolation();
        System.out.println(t);
    }


    @Test
    @Transactional
    public void test2(){
        List<FundOperLog> list = fundOperLogService.getSaleLogs();
        for(FundOperLog fundOperLog : list) {
            System.out.println(fundOperLog.getNote());
        }
    }

    @Test
    @Transactional
    public void testGroupBuying(){
        groupBuyService.closeWithTimeOut("6ccf4e607601464fba7380bfd8118b82");
    }

    @Test
    @Transactional
    public void test3(){
        GroupBuySponsorDto groupBuySponsor = new GroupBuySponsorDto();
        groupBuySponsor.setId("5c086196eb344e098744a2e81a668454");
        groupBuySponsor = groupBuySponsorService.getGroupBuySponsor(groupBuySponsor);
    }

    @Test
    @Transactional
    public void test4(){
        String   customerId = "ffdb2df781db4f1a97204fca175a880c";
        Customer customer   = customerService.get(customerId);
        System.out.println(customer.getRoleList().size() == 2);
    }
}
