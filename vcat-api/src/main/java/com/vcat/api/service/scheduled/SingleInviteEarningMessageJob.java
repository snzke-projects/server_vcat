//package com.vcat.api.service.scheduled;
//
//import com.google.common.collect.Maps;
//import com.tencent.xinge.Message;
//import com.tencent.xinge.MessageIOS;
//import com.vcat.common.push.PushService;
//import com.vcat.common.utils.IdGen;
//import com.vcat.common.utils.QuartzUtils;
//import com.vcat.common.utils.StringUtils;
//import com.vcat.module.content.dao.ArticleDao;
//import com.vcat.module.content.entity.Article;
//import com.vcat.module.core.utils.DictUtils;
//import com.vcat.module.ec.dao.CustomerDao;
//import com.vcat.module.ec.dao.MessageDao;
//import com.vcat.module.ec.entity.Customer;
//import com.vcat.module.ec.entity.Shop;
//import org.apache.log4j.Logger;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.quartz.QuartzJobBean;
//
//import java.util.Calendar;
//import java.util.Map;
//
///**
// * 发送未获取邀请奖励消息任务
// */
//public class SingleInviteEarningMessageJob extends QuartzJobBean {
//    // 用户ID
//    private final static String CUSTOMER_ID = "CUSTOMER_ID";
//    @Autowired
//    private ArticleDao articleDao;
//    @Autowired
//    private MessageDao messageDao;
//    @Autowired
//    private CustomerDao customerDao;
//	private static Logger LOG = Logger.getLogger(SingleInviteEarningMessageJob.class);
//
//	@Override
//	protected void executeInternal(JobExecutionContext ctx)
//			throws JobExecutionException {
//		LOG.info("发送未获取邀请奖励消息任务START");
//
//        String customerId = ctx.getMergedJobDataMap().get(CUSTOMER_ID)+"";
//
//        Customer customer = customerDao.select(customerId);
//
//        if(StringUtils.isNotEmpty(customer.getShop().getParentId())){
//            LOG.info(customer.getUserName() + "用户已获得战队红包，跳过战队红包提醒！");
//            return;
//        }
//
//        String messageId = DictUtils.getDictValue("ec_invite_earning_message_id",null);
//
//        if(StringUtils.isEmpty(messageId)){
//            LOG.warn("发送未获取邀请奖励消息任务失败：邀请奖励消息ID为空，请在字典管理中添加ec_invite_earning_message_id类型的值");
//            return;
//        }
//
//        Article article = articleDao.get(messageId);
//
//        Message message = PushService.createMessage(StringUtils.abbr(article.getTitle() ,50));
//        MessageIOS messageIOS = PushService.createMessageIOS(StringUtils.abbr(article.getTitle() ,50));
//
//
//        com.vcat.module.ec.entity.Message systemMessage = new com.vcat.module.ec.entity.Message();
//        systemMessage.preInsert();
//        systemMessage.setTitle(article.getTitle());
//        systemMessage.setThumb(article.getImage());
//        systemMessage.setIntro(article.getDescription());
//        systemMessage.setArticle(article);
//        systemMessage.setType(com.vcat.module.ec.entity.Message.PERSONAL);
//        messageDao.insert(systemMessage);
//
//        Shop shop = new Shop();
//        shop.setId(customer.getId());
//        systemMessage.setShop(shop);
//        systemMessage.getSqlMap().put("flagId", IdGen.uuid());
//        messageDao.insertFlag(systemMessage);
//
//        if(null == customer.getDeviceType() || StringUtils.isEmpty(customer.getPushToken())){
//            LOG.warn("推送邀请奖励消息失败：" + customer.getUserName() + "用户设备类型[" + customer.getDeviceType() + "]或PushToken[" + customer.getPushToken() + "]为空");
//            return;
//        }
//
//        if(customer.isAndroid()){
//            PushService.pushSingleDevice(customer.getPushToken(),message);
//        }else if(customer.isIOS()){
//            PushService.pushSingleDevice(customer.getPushToken(),messageIOS);
//        }
//	}
//
//    /**
//     * 设置指定小店间隔提醒分红奖励
//     * @param customerId 需要提醒的小店ID
//     */
//    public static void addSingleMessageJob(String customerId){
//        String inviteCountString = DictUtils.getDictValue("ec_invite_remind_count","2");    // 未领取战队红包提醒次数，默认2次
//        String intervalString = DictUtils.getDictValue("ec_invite_remind_interval", "1440");// 未领取战队红包提醒间隔时间，单位(分钟)，默认1天
//        Integer inviteCount;
//        Integer interval;
//        try {
//            inviteCount = Integer.parseInt(inviteCountString);
//            interval = Integer.parseInt(intervalString);
//        } catch (NumberFormatException e) {
//            inviteCount = 2;
//            interval = 1440;
//        }
//        Calendar calendar = Calendar.getInstance();
//
//        Map<String,String> jobData = Maps.newHashMap();
//        jobData.put(CUSTOMER_ID,customerId);
//
//        String desc = "提醒有未领取战队红包任务：\n" + CUSTOMER_ID + "：小店ID";
//
//        for (int i = 1; i <= inviteCount; i++) {
//            calendar.add(Calendar.MINUTE, interval);
//            QuartzUtils.pushJob("SingleInviteEarningMessageJob_"+customerId+"_"+i,SingleInviteEarningMessageJob.class, QuartzUtils.getCronExpression(calendar.getTime()),desc,jobData);
//        }
//    }
//}
