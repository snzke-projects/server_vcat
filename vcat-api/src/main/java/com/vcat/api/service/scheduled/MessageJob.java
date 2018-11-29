//package com.vcat.api.service.scheduled;

//import com.google.common.collect.Maps;
//import com.tencent.xinge.Message;
//import com.tencent.xinge.MessageIOS;
//import com.vcat.common.push.PushService;
//import com.vcat.common.utils.DateUtils;
//import com.vcat.common.utils.IdGen;
//import com.vcat.common.utils.QuartzUtils;
//import com.vcat.common.utils.StringUtils;
//import com.vcat.module.content.dao.ArticleDao;
//import com.vcat.module.content.entity.Article;
//import com.vcat.module.ec.dao.CustomerDao;
//import com.vcat.module.ec.dao.MessageDao;
//import com.vcat.module.ec.dao.MessageRegisterDao;
//import com.vcat.module.ec.entity.Customer;
//import com.vcat.module.ec.entity.MessageRegister;
//import com.vcat.module.ec.entity.Shop;
//import org.apache.log4j.Logger;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.quartz.QuartzJobBean;
//
//import java.util.Calendar;
//import java.util.List;
//import java.util.Map;

/**
 * 发送消息任务
 */
//public class MessageJob extends QuartzJobBean {
//    // 用户ID
//    private final static String CUSTOMER_ID = "CUSTOMER_ID";
//    // 内容ID
//    private final static String ARTICLE_ID = "ARTICLE_ID";
//    @Autowired
//    private ArticleDao articleDao;
//    @Autowired
//    private MessageDao messageDao;
//    @Autowired
//    private CustomerDao customerDao;
//
//	private static Logger LOG = Logger.getLogger(MessageJob.class);

//	@Override
//	protected void executeInternal(JobExecutionContext ctx)
//			throws JobExecutionException {
//        String name = ctx.getJobDetail().getKey().getName();
//
//		LOG.info("发送消息任务["+name+"]START");
//
//        String customerId = ctx.getMergedJobDataMap().get(CUSTOMER_ID)+"";
//
//        String articleId = ctx.getMergedJobDataMap().get(ARTICLE_ID)+"";
//
//        if(StringUtils.isEmpty(customerId) || StringUtils.isEmpty(articleId)){
//            LOG.warn("发送消息任务["+name+"]失败：参数不完整customerId["+customerId+"],articleId["+articleId+"]");
//            return;
//        }
//
//        Customer customer = customerDao.select(customerId);
//
//        Article article = articleDao.get(articleId);
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
//            LOG.warn("发送消息任务["+name+"]失败：推送消息失败："+customer.getUserName()+"用户设备类型["+customer.getDeviceType()+"]或PushToken["+customer.getPushToken()+"]为空");
//            return;
//        }
//
//        Message message = PushService.createMessage(StringUtils.abbr(article.getTitle() ,50));
//        MessageIOS messageIOS = PushService.createMessageIOS(StringUtils.abbr(article.getTitle() ,50));
//
//        if(customer.isAndroid()){
//            PushService.pushSingleDevice(customer.getPushToken(),message);
//        }else if(customer.isIOS()){
//            PushService.pushSingleDevice(customer.getPushToken(),messageIOS);
//        }
//
//        LOG.info("发送消息任务["+name+"]END");
//	}

//    /**
//     * 设置注册消息任务
//     * @param customerId 需要提醒的小店ID
//     */
//    public static void addRegisterMessageJob(String customerId,List<MessageRegister> list){
//        if(null == list || list.isEmpty()){
//            return;
//        }
//
//        Map<String,String> jobData = Maps.newHashMap();
//        jobData.put(CUSTOMER_ID,customerId);
//
//        String desc = "推送注册后定时消息：\n小店ID：" + CUSTOMER_ID;
//
//        for (int i = 0; i < list.size(); i++) {
//            MessageRegister messageRegister = list.get(i);
//
//            String messageName = "MessageJob_" + customerId + "_" + i + "_" + messageRegister.getIntervalShow();
//
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.MINUTE, messageRegister.getInterval());
//
//            if(null == messageRegister.getArticle() || StringUtils.isEmpty(messageRegister.getArticle().getId())){
//                LOG.error("设置任务[" + messageName + "]失败：文章或文章ID为空");
//                continue;
//            }
//
//            jobData.put(ARTICLE_ID, messageRegister.getArticle().getId());
//
//            QuartzUtils.pushJob(messageName, MessageJob.class, QuartzUtils.getCronExpression(calendar.getTime()), desc, jobData);
//        }
//    }
//}
