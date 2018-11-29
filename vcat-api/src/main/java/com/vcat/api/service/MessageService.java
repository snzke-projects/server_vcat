package com.vcat.api.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.tencent.xinge.MessageIOS;
import com.vcat.module.ec.dao.*;
import com.vcat.module.ec.entity.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.api.exception.ApiException;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.Page;
import com.vcat.common.persistence.Pager;
import com.vcat.common.push.PushService;
import com.vcat.common.utils.DateUtils;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.utils.ThreadPoolUtil;
import com.vcat.module.content.dao.ArticleDao;
import com.vcat.module.content.entity.Article;
import com.vcat.module.core.utils.DictUtils;

@Service
@Transactional(readOnly = true)
public class MessageService{
	@Autowired
	private MessageEarningDao messageEarningDao;
	@Autowired
	private MessageOrderDao messageOrderDao;
	@Autowired
	private CustomerDao customerDao;
	@Autowired
	private ShareEarningDao shareEarningDao;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private MessageRemindDao   messageRemindDao;
    @Autowired
    private CustomerService customerService;
    public final static Logger log = LoggerFactory.getLogger(MessageService.class);

	public Map<String,Object> getNotReadMsgCount(String shopId){
		return messageEarningDao.queryNotReadMessageCount(shopId);
	}

	/**
     * 推送财富消息,采用异步新线程方式向信鸽服务器发送推送请求
     * @return
     */
    public void pushEarningMsgTask(MessageEarning earning){
        Customer customer = customerDao.select(earning.getShop().getId());
        earning.preInsert();
        messageEarningDao.insert(earning);
		// 推送财富消息
		Map<String,Object> param = new HashMap<>();
		param.put(PushService.MSG_TYPE, PushService.MSG_TYPE_FUND);
        param.putAll(messageEarningDao.queryNotReadMessageCount(earning.getShop().getId()));

        if(null == customer.getDeviceType() || StringUtils.isEmpty(customer.getPushToken())){
            log.warn("推送财富消息失败：" + customer.getUserName() + "用户设备类型[" + customer.getDeviceType() + "]或PushToken[" + customer.getPushToken() + "]为空");
            return;
        }

        //采用异步方式发送消息
        ThreadPoolUtil.execute(() -> {
            if(customer.isAndroid()){
                PushService.pushSingleDevice(customer.getPushToken(), PushService.createHideMessage(param));
                PushService.pushSingleDevice(customer.getPushToken(), PushService.createMessage(earning.getMessageTitle(), param));
            }else if(customer.isIOS()){
                PushService.pushSingleDevice(customer.getPushToken(), PushService.createMessageIOS(earning.getMessageTitle(),param));
            }
            log.info("PUSH-INFO-推送财富消息：推送目标[" + customer.getUserName() + "]推送时间[" + DateUtils.getDate("yyyy-MM-dd HH:mm:ss") + "]" + "标题[" + earning.getMessageTitle()+ "]参数[" + param + "]");
        });
    }

    /**
     * 根据消息类型不同获取消息列表
     * @param pager
     * @param shopId
     * @param type
     * 1:V猫新动态
     * 2:订单消息
     * 3:财富消息
     * 4:分享奖励消息
     * @param version
     * @return
     */
	@Transactional(readOnly = false)
	public Pager findPage(Pager pager,String shopId,String type,int version){
		Page page = new Page();
		page.setPageNo(pager.getPageNo());
		page.setPageSize(pager.getPageSize());
		List list = null;
		Integer rowCount = 0;
		Shop shop = new Shop();
		shop.setId(shopId);

		if((PushService.MSG_TYPE_SYSTEM+"").equals(type)){		// 查询V猫新动态
			// 封装查询实体
			Message message = new Message();
			message.setShop(shop);
			message.setPage(page);

            if(1 == version){
                list = messageDao.findMessageList(message);
            }else if(2 == version){
                list = messageDao.findMessageListV2(message);
            }
			rowCount = Integer.parseInt(message.getPage().getCount() + "");

            for (Object res : list) {
                if (res instanceof Map) {
                    Map<String, Object> row   = (Map) res;
                    String              thumb = QCloudUtils.createOriginalDownloadUrl(row.get("thumb") + "");
                    row.put("thumb", thumb);
                    if (2 == version) {
                        row.put("imageUrl", thumb);
                    }
                    row.put("templateUrl", ApiConstants.VCAT_DOMAIN + "/buyer/views/article.html?id=" + row.get("articleId"));
                    //row.put("templateUrl", "http://192.168.2.5:8080/vcat-api/vmao/views/article.html?id="+row.get("articleId"));
                }
            }
			// 将该小店的未读消息置为已读
			messageDao.read(shopId);

            /////////////////////////////////////////////////////////////////////////////////

		}else if((PushService.MSG_TYPE_ORDER+"").equals(type)){	// 查询订单消息
			// 封装查询实体
			MessageOrder messageOrder = new MessageOrder();
			messageOrder.setShop(shop);
			messageOrder.setPage(page);
			list = messageOrderDao.findList(messageOrder);
			rowCount = Integer.parseInt(messageOrder.getPage().getCount() + "");
			// 将该小店的未读消息置为已读
			messageOrderDao.read(shopId);
            /////////////////////////////////////////////////////////////////////////////////

		}else if((PushService.MSG_TYPE_FUND+"").equals(type)){	// 查询财富消息
			// 封装查询实体
			MessageEarning messageEarning = new MessageEarning();
			messageEarning.setShop(shop);
			messageEarning.setPage(page);

			list = messageEarningDao.findList(messageEarning);
			rowCount = Integer.parseInt(messageEarning.getPage().getCount() + "");

			// 将该小店的未读消息置为已读
			messageEarningDao.read(shopId);
		}else if((PushService.MSG_TYPE_SHARE+"").equals(type)){	// 查询分享奖励
			// 封装查询实体
			ShareEarning shareEarning = new ShareEarning();
			shareEarning.setPage(page);
			shareEarning.getSqlMap().put("remindType", Remind.REMIND_TYPE_SHARE);
			shareEarning.getSqlMap().put("shopId",shopId);

			list = shareEarningDao.getShareMessageList(shareEarning);
			for (int i = 0;i<list.size();i++){
				Object res = list.get(i);
				if(res instanceof Map){
					Map<String,Object> row = (Map)res;
					if(row.get("productArchived").equals("false")){
						row.put("productArchived", false);
					}
					if(row.get("productArchived").equals("true")){
						row.put("productArchived", true);
					}
					if(row.get("shopArchived").equals("false")){
						row.put("shopArchived", false);
					}
					if(row.get("shopArchived").equals("true")){
						row.put("shopArchived", true);
					}
					row.put("imgUrl",QCloudUtils.createOriginalDownloadUrl(row.get("imgUrl")+""));
					row.put("mainUrl", QCloudUtils.createThumbDownloadUrl(row.get("mainUrl")+"", ApiConstants.DEFAULT_PRODUCT_THUMBSTYLE));
				}
			}
			rowCount = Integer.parseInt(shareEarning.getPage().getCount() + "");
		}else if((PushService.MSG_TYPE_REMIND+"").equals(type)){ //个人消息
            MessageRemind messageRemind = new MessageRemind();
            messageRemind.setShop(shop);
            messageRemind.setPage(page);
            list = messageRemindDao.findList(messageRemind);
            rowCount = Integer.parseInt(messageRemind.getPage().getCount() + "");
            // 将该小店的未读消息置为已读
            messageRemindDao.read(shopId);
        }

        pager.setList(list);
        pager.setRowCount(rowCount);
        pager.doPage();
        if(pager.getPageNo()>pager.getPageCount()){
        	 pager.setList(new ArrayList<>());
        }
		return pager;
	}

	/**
	 * 订阅分享活动
	 * @param shopId
	 * @param shareId
	 */
	@Transactional(readOnly = false)
	public void remindShare(String shopId, String shareId) {
		// 获取分享活动
		ShareEarning shareEarning = shareEarningDao.get(shareId);

        if(shareEarning.getStartTime().before(new Date())){
            throw new ApiException("订阅分享活动失败：活动已开始！");
        }

		// 获取分享活动推送间隔时间
        long time;
        try {
            time = Long.parseLong(DictUtils.getDictValue(Remind.DICT_LIMIT_TIME_TYPE, "600000"));
        } catch (NumberFormatException e) {
            time = 600000L;
        }

        Date sendTime = new Date(shareEarning.getStartTime().getTime() - time);
		String sendTimeStr = DateUtils.formatDate(sendTime,"yyyy-MM-dd HH:mm:ss");

		// 推送标题
		String title = "喵，有商品分享活动将在10分钟之后开始";
		// 获取pushToken
		Customer customer = customerDao.select(shopId);
		// 推送定时分享活动消息
		Map<String,Object> param = new HashMap<>();
		param.put(PushService.MSG_TYPE, PushService.MSG_TYPE_SHARE);
		param.putAll(messageEarningDao.queryNotReadMessageCount(shopId));
		JSONObject json = null;

        if(null != customer.getDeviceType() && StringUtils.isNotEmpty(customer.getPushToken())){
            Object msg = null;
            if(customer.isAndroid()){
                msg = PushService.createMessage(sendTimeStr, title , param);
                json = PushService.pushSingleDevice(customer.getPushToken(), (com.tencent.xinge.Message)msg);
            }else if(customer.isIOS()){
                msg = PushService.createMessageIOS(sendTimeStr, title, param);
                json = PushService.pushSingleDevice(customer.getPushToken(), (MessageIOS)msg);
            }
            log.info("PUSH-INFO-推送分享活动提醒：结果[" + json + "]推送目标[" + customer.getUserName() + "]推送时间[" + sendTimeStr + "]" + "活动标题[" + title + "]推送MESSAGE[" + msg + "]");
        }else{
            log.error("推送分享活动提醒失败：[" + json + "]用户名：[" + customer.getUserName() + "]用户设备类型[" + customer.getDeviceType() + "]或PushToken[" + customer.getPushToken() + "]为空");
        }

		// 封装订阅数据
		Remind remind = new Remind();
		remind.preInsert();
		remind.setCustomerId(shopId);
		remind.setType(Remind.REMIND_TYPE_SHARE);
		remind.setRelationId(shareEarning.getId());
		remind.setPushId("no_push_id");
		remind.setPushDate(sendTime);
		remind.setIsActivate(Remind.ACTIVATED);
		remind.setTitle(shareEarning.getTitle());
		remind.setContent(shareEarning.getTitle());

		Integer remindCount = shareEarningDao.remindCount(remind);
		if(remindCount == null || remindCount == 0){	// 判断该分享活动是否已订阅
			shareEarningDao.remindShare(remind);
		}
	}

	//获取最新消息
	public Map<String,Object> getNewestMessage(String shopId){
		Map<String,Object> map = new HashMap<>();
        Map<String,Object> newMsg = messageDao.getNewestMessage(shopId);
        Map<String,Object> notRead = messageEarningDao.queryNotReadMessageCount(shopId);
        if(null != newMsg){
            map.putAll(newMsg);
        }
        if(null != notRead){
            map.putAll(notRead);
        }
		return map;
	}
	
	//发送战队红包领取消息
	@Transactional(readOnly = false)
	public void pushInviteMessage(String shopId) {
		//领取战队红包V猫新动态
		String messageId = DictUtils.getDictValue(
				"ec_invite_earning_message_id", null);

		if (StringUtils.isEmpty(messageId)) {
			return;
		}
		Article article = articleDao.get(messageId);
		Shop shop = new Shop();
		if(article!=null){
			com.vcat.module.ec.entity.Message systemMessage = new com.vcat.module.ec.entity.Message();
			systemMessage.preInsert();
			systemMessage.setTitle(article.getTitle());
			systemMessage.setThumb(article.getImage());
			systemMessage.setIntro(article.getDescription());
			systemMessage.setArticle(article);
			systemMessage.setType(Message.PERSONAL);
			messageDao.insert(systemMessage);
			shop.setId(shopId);
			systemMessage.setShop(shop);
			systemMessage.getSqlMap().put("flagId", IdGen.uuid());
			messageDao.insertFlag(systemMessage);
		}
		//组建自己战队V猫新动态
		String bonusMessageId = DictUtils.getDictValue(
				"ec_bonus_earning_message_id", null);

		if (StringUtils.isEmpty(bonusMessageId)) {
			return;
		}
		Article bonusArticle = articleDao.get(bonusMessageId);
		if(bonusArticle!=null){
			com.vcat.module.ec.entity.Message message1 = new com.vcat.module.ec.entity.Message();
			message1.preInsert();
			message1.setTitle(bonusArticle.getTitle());
			message1.setThumb(bonusArticle.getImage());
			message1.setIntro(bonusArticle.getDescription());
			message1.setArticle(bonusArticle);
			message1.setType(Message.PERSONAL);
			messageDao.insert(message1);

			message1.setShop(shop);
			message1.getSqlMap().put("flagId", IdGen.uuid());
			messageDao.insertFlag(message1);
		}
	}

	/**
	 * 向达到金额标准的用户发送可以申请VIP 个人消息
	 * @param
     */
	public void pushRemindMessage(String shopId,String title,String content){
        MessageRemind remind = new MessageRemind();
        remind.preInsert();
        Shop shop = new Shop();
        shop.setId(shopId);
        remind.setTitle(title);
        remind.setContent(content);
        remind.setShop(shop);
        messageRemindDao.insert(remind);
        Customer customer = customerService.get(shopId);
        if(StringUtils.isNotBlank(customer.getPushToken())){
            // 初始化消息参数
            Map<String,Object> param = new HashMap<>();
            param.put(PushService.MSG_TYPE, PushService.MSG_TYPE_REMIND);       // 消息类型
            param.putAll(messageEarningDao.queryNotReadMessageCount(shopId));   // 未读消息数量
            PushService.pushSingleDevice(customer.getPushToken(), PushService.createMessage(title, param));
            log.info("个人消息推送成功");
        }else{
            log.warn("个人消息推送失败");
        }
	}
}
