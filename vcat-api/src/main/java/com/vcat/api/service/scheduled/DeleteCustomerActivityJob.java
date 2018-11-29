package com.vcat.api.service.scheduled;

import com.vcat.api.service.ActivityService;
import com.vcat.api.service.OrderService;
import com.vcat.api.service.PaymentService;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.payment.AlipayUtils;
import com.vcat.common.utils.QuartzUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.ec.dao.CustomerDao;
import com.vcat.module.ec.dao.PaymentLogDao;
import com.vcat.module.ec.entity.Payment;
import com.vcat.module.ec.entity.PaymentLog;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 删除未支付的支付单及订单任务
 */
public class DeleteCustomerActivityJob extends QuartzJobBean {
	private final static String PAYMENT_ID = "PAYMENT_ID";	// 参数支付单ID
    private final static String ACTIVITY_ID = "ACTIVITY_ID";	// 参数活动ID
	private final static String JOB_NAME_PRIFIX = "DELETE_PAYMENT_";		// 任务名称前缀

	@Autowired
	private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentLogDao paymentLogDao;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private CustomerDao customerDao;

	private static Logger LOG = Logger.getLogger(DeleteCustomerActivityJob.class);
	@Override
	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
		String paymentId = ctx.getMergedJobDataMap().get(PAYMENT_ID) + "";
        String activityId = ctx.getMergedJobDataMap().get(ACTIVITY_ID) + "";
        String buyerId = customerDao.getBuyerByPaymentId(paymentId);

		if(StringUtils.isNotEmpty(paymentId)){
            Payment payment = paymentService.get(paymentId);

            // 关闭支付宝未支付订单
            PaymentLog paymentLog = new PaymentLog();
            paymentLog.setPayment(payment);
            paymentLog.setTransactionSuccess("3");
            paymentLog.setGatewayCode(ApiConstants.PAY_TYPE_ALIPAY);
            paymentLog = paymentLogDao.getPaymentLog(paymentLog);
            if (paymentLog != null && payment != null) {
                if(AlipayUtils.closeTrade(paymentLog.getTransactionNo(), payment.getPaymentNo())){
                    LOG.info("支付宝关闭未支付活动费用订单成功：支付宝交易号["
                            + paymentLog.getTransactionNo() + "],支付单号["
                            + payment.getPaymentNo() + "]");
                }else{
                    LOG.warn("支付宝关闭未支付活动费用订单失败：支付宝交易号["
                            + paymentLog.getTransactionNo() + "],支付单号["
                            + payment.getPaymentNo() + "]");
                }
            }

            // 删除本地数据
            if(null != payment && StringUtils.isEmpty(payment.getTransactionNo())){
                orderService.deleteOrderByPaymentId(paymentId);
                activityService.deleteActivityCustomer(activityId,buyerId);
                LOG.info("删除未付款的支付单成功PAYMENT_ID[" + paymentId + "]");
            }else{
                LOG.info("该支付单[" + paymentId + "]已支付或已被删除，取消删除");
            }
		}else{
            LOG.info("支付单ID为空，删除失败！");
        }
	}

	/**
	 * 增加删除未支付的活动费用订单任务
     * @param activityId 活动ID
     * @param paymentId 支付单ID
	 * @param fireTime 商品ID
	 */
	public static void pushJob(String activityId,String paymentId,Date fireTime){
		String jobName = JOB_NAME_PRIFIX + paymentId;
		Map<String,String> param = new HashMap<>();
        param.put(ACTIVITY_ID, activityId);
		param.put(PAYMENT_ID, paymentId);
        StringBuffer desc = new StringBuffer("删除未支付的支付单及订单任务：");
        desc.append("\r\n" + paymentId + "：支付单ID");
		QuartzUtils.pushJob(jobName, DeleteCustomerActivityJob.class, QuartzUtils.getCronExpression(fireTime), desc.toString(), param);
	}
}
