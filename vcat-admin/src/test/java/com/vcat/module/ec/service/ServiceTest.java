package com.vcat.module.ec.service;

import com.tencent.WXPay;
import com.tencent.business.RefundBusiness;
import com.tencent.business.RefundQueryBusiness;
import com.tencent.protocol.refund_protocol.RefundReqData;
import com.tencent.protocol.refund_protocol.RefundResData;
import com.tencent.protocol.refund_query_protocol.RefundQueryReqData;
import com.tencent.protocol.refund_query_protocol.RefundQueryResData;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
@TestExecutionListeners(listeners={
        DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
/**
 * 默认回滚数据
 */
@TransactionConfiguration(transactionManager="transactionManager",defaultRollback=true)
public class ServiceTest extends AbstractTransactionalJUnit4SpringContextTests {
	@BeforeClass
	public static void setUp() {
	}

	@Test
	public void testRefund() {
        String transactionNo = "1005120493201602163306673632";
        String refundNo = "738c48575d17407db6ca7536558d0abe";
        RefundBusiness.ResultListener rl = new RefundBusiness.ResultListener() {
            @Override
            public void onFailByReturnCodeError(RefundReqData refundReqData, RefundResData refundResData) {

            }

            @Override
            public void onFailByReturnCodeFail(RefundReqData refundReqData, RefundResData refundResData) {

            }

            @Override
            public void onFailBySignInvalid(RefundReqData refundReqData, RefundResData refundResData) {

            }

            @Override
            public void onRefundFail(RefundReqData refundReqData, RefundResData refundResData) {

            }

            @Override
            public void onRefundSuccess(RefundReqData refundReqData, RefundResData refundResData) {

            }
        };

        RefundQueryBusiness.ResultListener rql = new RefundQueryBusiness.ResultListener() {
            @Override
            public void onFailByReturnCodeError(RefundQueryResData refundQueryResData) {

            }

            @Override
            public void onFailByReturnCodeFail(RefundQueryResData refundQueryResData) {

            }

            @Override
            public void onFailBySignInvalid(RefundQueryResData refundQueryResData) {

            }

            @Override
            public void onRefundQueryFail(RefundQueryResData refundQueryResData) {

            }

            @Override
            public void onRefundQuerySuccess(RefundQueryResData refundQueryResData) {
                System.out.println("------------------" + ReflectionToStringBuilder.toString(refundQueryResData));
            }
        };
        try {
            WXPay wxPay = new WXPay();
            wxPay.initConfigurationForWap();
//            WXPay.doRefundBusiness(new RefundReqData(transactionNo,null,null,refundNo,8980,8980,"1244038802","CNY"),rl);
            wxPay.doRefundQueryBusiness(new RefundQueryReqData(transactionNo,null,null,refundNo,"2005120493201602160153366544",wxPay.getConfigure()),rql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
