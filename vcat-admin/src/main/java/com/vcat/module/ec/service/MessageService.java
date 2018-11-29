package com.vcat.module.ec.service;

import com.vcat.common.push.PushService;
import com.vcat.common.utils.DateUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.utils.ThreadPoolUtil;
import com.vcat.module.core.service.BaseService;
import com.vcat.module.ec.dao.MessageEarningDao;
import com.vcat.module.ec.dao.MessageRemindDao;
import com.vcat.module.ec.entity.Customer;
import com.vcat.module.ec.entity.MessageEarning;
import com.vcat.module.ec.entity.MessageRemind;
import com.vcat.module.ec.entity.Shop;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息Service
 */
@Service
@Transactional(readOnly = true)
public class MessageService extends BaseService{
    @Autowired
    private MessageEarningDao messageEarningDao;
    @Autowired
    private MessageRemindDao messageRemindDao;
    @Autowired
    private CustomerService customerService;

    /**
     * 推送财富消息
     * @return
     */
    @Transactional(readOnly = false)
    public void pushEarningMsg(MessageEarning earning){
        Customer customer = customerService.get(earning.getShop().getId());
        earning.preInsert();
        messageEarningDao.insert(earning);
		// 推送财富消息
		Map<String,Object> param = new HashMap<>();
		param.put(PushService.MSG_TYPE, PushService.MSG_TYPE_FUND);
        param.putAll(messageEarningDao.queryNotReadMessageCount(earning.getShop().getId()));

        if(null == customer.getDeviceType() || StringUtils.isEmpty(customer.getPushToken())){
            logger.error("推送财富消息失败：" + customer.getUserName() + "用户设备类型[" + customer.getDeviceType() + "]或PushToken[" + customer.getPushToken() + "]为空");
            return;
        }

		if(customer.isAndroid()){
            PushService.pushSingleDevice(customer.getPushToken(), PushService.createHideMessage(param));
			PushService.pushSingleDevice(customer.getPushToken(), PushService.createMessage(earning.getMessageTitle(), param));
		}else if(customer.isIOS()){
            PushService.pushSingleDevice(customer.getPushToken(), PushService.createSilentMessageIOS(param));
			PushService.pushSingleDevice(customer.getPushToken(), PushService.createMessageIOS(earning.getMessageTitle(),param));
		}
        logger.info("PUSH-INFO-推送财富消息：推送目标[" + customer.getUserName() + "]推送时间[" + DateUtils.getDate("yyyy-MM-dd HH:mm:ss") + "]" + "标题[" + earning.getMessageTitle() + "]参数[" + param + "]");
    }

    /**
     * 获取未读消息数量
     * @param shopId
     * @return
     */
    public Map<String,Object> queryNotReadMessageCount(String shopId){
        return messageEarningDao.queryNotReadMessageCount(shopId);
    }

    /**
     * 推送透传未读消息
     * @param pushTokenList 需要推送的设备TOKEN
     * @param type 未读消息类型
     */
    public void pushHideNotRead(List<String> pushTokenList,int type){
        pushHideNotRead(pushTokenList, type, 1);
    }

    /**
     * 推送透传未读消息
     * @param pushTokenList 需要推送的设备TOKEN
     * @param type 未读消息类型
     * @param count 未读消息数量
     */
    public void pushHideNotRead(List<String> pushTokenList,int type ,Integer count){
        if(null == pushTokenList || pushTokenList.isEmpty()){
            logger.info("推送设备TOKEN集合为空，推送完成！");
            return;
        }
        // 推送安卓隐匿消息
        Map<String,Object> param = new HashMap<>();
        param.put(PushService.MSG_TYPE, type);
        param.put(PushService.NOT_READ, count);
        PushService.pushDeviceListMultiple(PushService.createHideMessage(param), pushTokenList);
        logger.info("推送类型为[" + PushService.getLabelByMsgType(type) + "]的安卓未读消息完成！共推送：" + (null != pushTokenList ? pushTokenList.size() : 0) + "条");
    }

    /**
     * 推送透传未读消息
     * @param pushTokenList 需要推送的IOS设备TOKEN
     * @param type 未读消息类型
     */
    public void pushHideIOSNotRead(List<String> pushTokenList,int type){
        pushHideIOSNotRead(pushTokenList, type, 1);
    }

    /**
     * 推送透传未读消息
     * @param pushTokenList 需要推送的IOS设备TOKEN
     * @param type 未读消息类型
     * @param count 未读消息数量
     */
    public void pushHideIOSNotRead(List<String> pushTokenList,int type ,Integer count){
        // 推送ISO隐匿消息
        Map<String,Object> param = new HashMap<>();
        param.put(PushService.MSG_TYPE, type);
        param.put(PushService.NOT_READ, count);
        PushService.pushDeviceListMultiple(PushService.createSilentMessageIOS(param), pushTokenList);
        logger.info("推送类型为[" + PushService.getLabelByMsgType(type) + "]的IOS未读消息完成！共推送：" + (null != pushTokenList ? pushTokenList.size() : 0) + "条");
    }

    /**
     * 推送隐匿内容到所有设备
     * @param type 内容类型
     */
    public void pushAllHideNotRead(int type){
        ThreadPoolUtil.execute(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("deviceType", PushService.ANDROID);
            List<String> androidTokenList = customerService.findCustomerPushToken(map);
            map.put("deviceType", PushService.IOS);
            List<String> iosTokenList = customerService.findCustomerPushToken(map);
            pushHideNotRead(androidTokenList, type);
            pushHideIOSNotRead(iosTokenList, type);
        });
    }

    /**
     * 推送个人提醒消息
     * @param shopId
     * @param title
     * @param content
     * @param cusParam
     * @return pushToken
     */
    public Customer pushRemindMessage(String shopId,String title,String content,Map<String,Object> cusParam){
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
            if(null != cusParam){
                param.putAll(cusParam);
            }

            if(customer.isAndroid()){
                JSONObject json = PushService.pushSingleDevice(customer.getPushToken(), PushService.createMessage(title, param));
                logger.info("推送安卓个人消息完成！推送目标[" + customer.getUserName() + "]推送时间[" + DateUtils.getDate("yyyy-MM-dd HH:mm:ss") + "]" + "标题[" + title + "]参数[" + param + "]结果[" + json + "]");
                JSONObject hideJson = PushService.pushSingleDevice(customer.getPushToken(),PushService.createHideMessage(param));
                logger.info("推送安卓透传消息完成！推送目标[" + customer.getUserName() + "]推送时间[" + DateUtils.getDate("yyyy-MM-dd HH:mm:ss") + "]" + "参数[" + param + "]结果[" + hideJson + "]");
            }else if(customer.isIOS()){
                JSONObject json = PushService.pushSingleDevice(customer.getPushToken(), PushService.createMessageIOS(title, param));
                logger.info("推送IOS个人消息完成！推送目标[" + customer.getUserName() + "]推送时间[" + DateUtils.getDate("yyyy-MM-dd HH:mm:ss") + "]" + "标题[" + title + "]参数[" + param + "]结果[" + json + "]");
                JSONObject hideJson = PushService.pushSingleDevice(customer.getPushToken(),PushService.createSilentMessageIOS(param));
                logger.info("推送IOS透传消息完成！推送目标[" + customer.getUserName() + "]推送时间[" + DateUtils.getDate("yyyy-MM-dd HH:mm:ss") + "]" + "参数[" + param + "]结果[" + hideJson + "]");
            }
        }else{
            logger.warn("推送个人提醒消息失败：" + customer.getUserName() + "PushToken[" + customer.getPushToken() + "]为空");
        }
        return customer;
    }

    /**
     * 推送个人提醒消息
     * @param shopId
     * @param title
     * @param content
     * @return pushToken
     */
    public Customer pushRemindMessage(String shopId,String title,String content){
        return pushRemindMessage(shopId,title,content,null);
    }
}
