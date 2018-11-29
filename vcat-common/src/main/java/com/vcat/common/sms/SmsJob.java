//package com.vcat.common.sms;
//
//import com.vcat.common.utils.DateUtils;
//import com.vcat.common.utils.QuartzUtils;
//import com.vcat.common.utils.StringUtils;
//import org.apache.log4j.Logger;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.springframework.scheduling.quartz.QuartzJobBean;
//
//import java.io.IOException;
//import java.util.Date;
//import java.util.Map;
//
///**
// * 定时发送短信任务
// */
//public class SmsJob extends QuartzJobBean {
//	// 接收电话
//	public final static String PHONE = "PHONE";
//	// 短信模板号
//	public final static String TPL_NUMBER = "TPL_NUMBER";
//	private static Logger LOG = Logger.getLogger(SmsJob.class);
//
//	@Override
//	protected void executeInternal(JobExecutionContext ctx)
//			throws JobExecutionException {
//		String phone = ctx.getMergedJobDataMap().get(PHONE)+"";
//		String tpl = ctx.getMergedJobDataMap().get(TPL_NUMBER)+"";
//
//		ctx.getMergedJobDataMap().remove(PHONE);
//		ctx.getMergedJobDataMap().remove(TPL_NUMBER);
//
//		if(StringUtils.isNotEmpty(phone) && StringUtils.isNotEmpty(tpl)){
//			String runTime = DateUtils.formatDateTime(ctx.getFireTime());
//			String threadName = Thread.currentThread().getName();
//			String threadId = Thread.currentThread().getId()+"";
//
//			try {
//				SmsClient.tplSendSms(Long.parseLong(tpl),phone,ctx.getMergedJobDataMap());
//			} catch (IOException e) {
//				LOG.info("[" + threadName + "][" + threadId + "][" + runTime + "]--------定时向[" + phone + "]发送短信[" + tpl + "]失败：" + e.getMessage());
//			}
//		}
//	}
//
//	/**
//	 * 定时发送短信
//	 * @param phone 接收目标
//	 * @param tpl 模板号
//	 * @param sendTime 发送时间
//	 * @param param 模板参数
//	 */
//	public static void sendSMS(String phone,long tpl,Date sendTime,Map<String,String> param){
//		if(sendTime.after(new Date())){		// 如果发送时间大于当前时间，则建立定时任务，否则直接发送
//			param.put(PHONE,phone);
//			param.put(TPL_NUMBER, tpl +"");
//			String jobName = DateUtils.formatDateTime(sendTime) + "向["+phone+"]发送短信["+ tpl +"]";
//            StringBuffer desc = new StringBuffer("定时发送短信任务：");
//            desc.append("\r\n" + PHONE + "：电话号码");
//            desc.append("\r\n" + TPL_NUMBER + "：短信模板号");
//			QuartzUtils.pushJob(jobName, SmsJob.class, QuartzUtils.getCronExpression(sendTime),desc.toString(), param);
//		}else{
//			try {
//				SmsClient.tplSendSms(tpl,phone,param);
//			} catch (IOException e) {
//				LOG.info("向[" + phone + "]发送短信[" + tpl + "]失败：" + e.getMessage());
//			}
//		}
//	}
//}
