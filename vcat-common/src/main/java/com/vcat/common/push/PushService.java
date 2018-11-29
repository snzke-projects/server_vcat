package com.vcat.common.push;

import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.Style;
import com.tencent.xinge.XingeApp;
import com.vcat.common.config.Global;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PushService {
	public final static int ANDROID = 3;
	public final static int IOS = 4;
	public final static String MSG_SYSTEM_NOT_READ = "MSG_SYSTEM_NOT_READ"; // V猫新动态未读数量
	public final static String MSG_ORDER_NOT_READ = "MSG_ORDER_NOT_READ";   // 订单消息未读数量
	public final static String MSG_FUND_NOT_READ = "MSG_FUND_NOT_READ";     // 财富消息未读数量
    public final static String MSG_REMIND_NOT_READ = "MSG_REMIND_NOT_READ"; // 个人消息未读数量
	public final static String NOT_READ = "NOT_READ"; 	// 消息未读数量
	public final static String MSG_TYPE = "MSG_TYPE"; 	// 消息类型
	public final static String PUSH_TIME = "PUSH_TIME"; // 消息推送时间
    public final static String UPGRADE_SUCCESS = "UPGRADE_SUCCESS"; // 店铺升级成功标识
    public final static int MSG_TYPE_SYSTEM = 1; 	    // V猫新动态
    public final static int MSG_TYPE_ORDER = 2; 	    // 订单消息
    public final static int MSG_TYPE_FUND = 3; 		    // 财富消息
    public final static int MSG_TYPE_SHARE = 4; 	    // 分享消息
    public final static int MSG_TYPE_REMIND = 5; 	    // 个人提醒消息
    public final static int MSG_TYPE_ACTIVITY = 6; 	    // V猫体验官
    public final static int MSG_TYPE_ACTIVITY_OFFLINE = 7; 	// 发现-V猫线下活动
    public final static int MSG_TYPE_BUSINESS = 8; 	    // 发现-V买卖
	public final static int DEFAULT_ENVIRONMENT = Integer.parseInt(Global.getConfig("xinge.ios.environment"));
	public final static long ANDROID_ACCESS_ID = Long.valueOf(StringUtils
			.trim(Global.getConfig("xinge.android.accessId")));
	public final static String ANDROID_SECRET_KEY = Global
			.getConfig("xinge.android.secretKey");
	public final static long IOS_ACCESS_ID = Long.valueOf(StringUtils
			.trim(Global.getConfig("xinge.ios.accessId")));
	public final static String IOS_SECRET_KEY = Global
			.getConfig("xinge.ios.secretKey");
	public final static Logger log = LoggerFactory.getLogger(PushService.class);

    public static String getLabelByMsgType(int msgType){
        if(MSG_TYPE_SYSTEM == msgType){
            return "V猫新动态";
        }else if(MSG_TYPE_ORDER == msgType){
            return "订单消息";
        }else if(MSG_TYPE_FUND == msgType){
            return "财富消息";
        }else if(MSG_TYPE_SHARE == msgType){
            return "分享消息";
        }else if(MSG_TYPE_REMIND == msgType){
            return "个人提醒消息";
        }else if(MSG_TYPE_ACTIVITY == msgType){
            return "V猫体验官";
        }else if(MSG_TYPE_ACTIVITY_OFFLINE == msgType){
            return "V猫线下活动";
        }else if(MSG_TYPE_BUSINESS == msgType){
            return "V买卖";
        }
        return "未知类型";
    }

	/**
	 * 创建message对象
	 * 
	 * @param sendTime
	 *            消息定时推送的时间，格式为 year-mon-day hour:min:sec，若小于服务
	 *            器当前时间则立即推送。选填，默认为空字符串，代表立即推送
	 * @param title
	 * @param content
	 * @param custom
	 * @return
	 */
	public static Message createMessage(String sendTime, String title,
			String content, Map<String, Object> custom) {
		Message msg = new Message();
		msg.setContent(content);
		msg.setTitle(title);
		msg.setType(Message.TYPE_NOTIFICATION);
		msg.setStyle(new Style(0, 1, 1, 1, 0, 1, 0, 1));
		if (custom != null && !custom.isEmpty()) {
			custom.put(PUSH_TIME, new Date().getTime());
			msg.setCustom(custom);
		} else {
			custom = new HashMap<>();
			custom.put(PUSH_TIME, new Date().getTime());
			msg.setCustom(custom);
		}
		if (!StringUtils.isEmpty(sendTime)) {
			msg.setSendTime(sendTime);
		}
		return msg;
	}

	/**
	 * 创建message对象,默认title为V猫小店
	 * 
	 * @param sendTime
	 * @param content
	 * @param custom
	 * @return
	 */
	public static Message createMessage(String sendTime, String content,
			Map<String, Object> custom) {
		return createMessage(sendTime, "V猫小店", content, custom);
	}

	/**
	 * 创建message对象,默认title为V猫小店
	 *
	 * @param sendTime
	 * @param content
	 * @return
	 */
	public static Message createMessage(String sendTime, String content) {
		Map<String, Object> custom = new HashMap<>();
		custom.put(MSG_TYPE, MSG_TYPE_SYSTEM);
		return createMessage(sendTime, content, custom);
	}

	/**
	 * 创建message对象,默认title为V猫小店,立即发送
	 *
	 * @param content
	 * @return
	 */
	public static Message createMessage(String content) {
		return createMessage(null, content);
	}

	/**
	 * 创建message对象,默认title为V猫小店,立即发送
	 *
	 * @param content
	 * @return
	 */
	public static Message createMessage(String content,
			Map<String, Object> custom) {
		return createMessage(null, content, custom);
	}

	/**
	 * 创建透传消息
	 * 
	 * @param custom
	 *            自定义参数
	 * @return
	 */
	public static Message createHideMessage(Map<String, Object> custom) {
		Message msg = new Message();
		msg.setType(Message.TYPE_MESSAGE);
		if (custom != null && !custom.isEmpty()) {
			custom.put(PUSH_TIME, new Date().getTime());
			msg.setCustom(custom);
		} else {
			custom = new HashMap<>();
			custom.put(PUSH_TIME, new Date().getTime());
			msg.setCustom(custom);
		}
		return msg;
	}

    /**
     * 创建ios message对象
     *
     * @param content
     * @return
     */
    public static MessageIOS createMessageIOS(String content) {
        Map<String, Object> custom = new HashMap<>();
        custom.put(MSG_TYPE, MSG_TYPE_SYSTEM);
        return createMessageIOS("", content, custom);
    }

    /**
     * 创建ios message对象,注意创建的这个推送消息是空
     *
     * @param custom
     * @return
     */
    public static MessageIOS createMessageIOS(Map<String, Object> custom) {
        return createMessageIOS("", "", custom);
    }

    /**
     * 创建ios message对象
     *
     * @param content
     * @param custom
     * @return
     */
    public static MessageIOS createMessageIOS(String content,
                                              Map<String, Object> custom) {
        return createMessageIOS("", content, custom);
    }

    /**
     * 创建ios message对象,默认title为V猫小店
     *
     * @param sendTime
     * @param content
     * @return
     */
    public static MessageIOS createMessageIOS(String sendTime, String content) {
        Map<String, Object> custom = new HashMap<>();
        custom.put(MSG_TYPE, MSG_TYPE_SYSTEM);
        return createMessageIOS(sendTime, content, custom);
    }

	/**
	 * 创建ios message对象
	 * 
	 * @param sendTime
	 *            消息定时推送的时间，格式为 year-mon-day hour:min:sec，若小于服务
	 *            器当前时间则立即推送。选填，默认为空字符串，代表立即推送
	 * @param content
	 * @param custom
	 * @return
	 */
	public static MessageIOS createMessageIOS(String sendTime, String content,
			Map<String, Object> custom) {
		MessageIOS msg = new MessageIOS();
		msg.setAlert(content);
		if (custom != null && !custom.isEmpty()) {
			msg.setCustom(custom);
		}
		if (!StringUtils.isEmpty(sendTime)) {
			msg.setSendTime(sendTime);
		}
		return msg;
	}

	/**
	 * 创建静默IOS推送消息对象
	 * @param custom
	 * @return
	 */
	public static MessageIOS createSilentMessageIOS(Map<String, Object> custom){
		MessageIOS msg = new MessageIOS();
		msg.setSilentPush(1);
		msg.setCustom(custom);
		return msg;
	}

	/**
	 * 通过token发送消息给android设备(批量)
	 * 
	 * @param message
	 * @param deviceList
	 * @return
	 */
	public static JSONObject pushDeviceListMultiple(Message message,
			List<String> deviceList) {
		XingeApp xinge = new XingeApp(ANDROID_ACCESS_ID, ANDROID_SECRET_KEY);
		JSONObject ret = xinge.createMultipush(message);
		if (ret.getInt("ret_code") != 0)
			return (ret);
		else {
			long pushId = ret.getJSONObject("result").getLong("push_id");
			return pushMultiple(xinge, pushId, deviceList);
		}
	}

	/**
	 * 通过token发送消息给ios备(批量)
	 * 
	 * @param message
	 * @param deviceList
	 * @param environment
	 *            OSENV_PROD = 1; IOSENV_DEV = 2;
	 * @return
	 */
	public static JSONObject pushDeviceListMultiple(MessageIOS message,
			List<String> deviceList, int environment) {
		XingeApp xinge = new XingeApp(IOS_ACCESS_ID, IOS_SECRET_KEY);
		message = handlerMessageIOSNotRead(message);	// 处理消息中所有未读数量的角标
		JSONObject ret = xinge.createMultipush(message, environment);
		if (ret.getInt("ret_code") != 0) {
            log.info("消息推送失败：" + ret);
            return (ret);
        } else {
			long pushId = ret.getJSONObject("result").getLong("push_id");
            log.info("消息推送完成：" + ret);
			return pushMultiple(xinge, pushId, deviceList);
		}
	}

	/**
	 * 识别IOS消息中是否包含未读消息数量，如包含，则设置IOS角标
	 * @param message
	 * @return
	 */
	private static MessageIOS handlerMessageIOSNotRead(MessageIOS message){
		if(null != message && null != message.getCustom() && !message.getCustom().isEmpty()){
			Long badge = 0L;
			Map<String, Object> custom = message.getCustom();
			try {
				if(custom.containsKey(MSG_SYSTEM_NOT_READ)){
                    badge += (Long)custom.get(MSG_SYSTEM_NOT_READ);
                }
				if(custom.containsKey(MSG_ORDER_NOT_READ)){
                    badge += (Long)custom.get(MSG_ORDER_NOT_READ);
                }
				if(custom.containsKey(MSG_FUND_NOT_READ)){
                    badge += (Long)custom.get(MSG_FUND_NOT_READ);
                }
                if(custom.containsKey(MSG_REMIND_NOT_READ)){
                    badge += (Long)custom.get(MSG_REMIND_NOT_READ);
                }
				if(badge > 0){
                    message.setBadge(badge.intValue());
                }
			} catch (Exception e) {
				e.printStackTrace();
				log.error("设置IOS角标出错：" + e.getMessage());
			}
		}
		return message;
	}

	/**
	 * 通过token发送消息给ios备(批量)
	 *
	 * @param message
	 * @param deviceList
	 * @return
	 */
	public static JSONObject pushDeviceListMultiple(MessageIOS message,
			List<String> deviceList) {
		return pushDeviceListMultiple(message, deviceList, DEFAULT_ENVIRONMENT);
	}

	/**
	 * 单个Android设备下发通知消息
	 * 
	 * @return
	 */
	public static JSONObject pushSingleDevice(String token, Message message) {
        XingeApp xinge = new XingeApp(ANDROID_ACCESS_ID, ANDROID_SECRET_KEY);
        return xinge.pushSingleDevice(token, message);
	}

	/**
	 * 单个IOS设备下发通知消息
	 *
	 * @return
	 */
	public static JSONObject pushSingleDevice(String token, MessageIOS message) {
        XingeApp xinge = new XingeApp(IOS_ACCESS_ID, IOS_SECRET_KEY);
        return xinge.pushSingleDevice(token,message, DEFAULT_ENVIRONMENT);
	}

	/**
	 * 取消定时推送，目前只有对于推送所有设备有效
	 * 
	 * @param pushId
	 * @param deviceType
	 *            3 Android 4 ios
	 * @return
	 */
	public static JSONObject cancelTimingPush(String pushId, int deviceType) {
		if (deviceType == XingeApp.DEVICE_ANDROID) {
			XingeApp xinge = new XingeApp(ANDROID_ACCESS_ID, ANDROID_SECRET_KEY);
			return xinge.cancelTimingPush(pushId);
		} else if (deviceType == XingeApp.DEVICE_IOS) {
			XingeApp xinge = new XingeApp(IOS_ACCESS_ID, IOS_SECRET_KEY);
			return xinge.cancelTimingPush(pushId);
		}
		return null;
	}

	private static JSONObject pushMultiple(XingeApp xinge, long pushId,
			List<String> deviceList) {
		JSONObject result = new JSONObject();
		result.put("push_id", pushId);
		List<String> tmpList = new ArrayList<String>();
		for (int i = 0; i < deviceList.size(); i++) {
			tmpList.add(deviceList.get(i));
			if (i != 0 && (i + 1) % 1000 == 0) {
				result.append("all",
						xinge.pushDeviceListMultiple(pushId, tmpList));
				tmpList.clear();
			}
		}
		if (!tmpList.isEmpty()) {
			result.append("all", xinge.pushDeviceListMultiple(pushId, tmpList));
		}
		return (result);
	}

	/**
	 * 下发所有设备
	 * 
	 * @return
	 */
	public static JSONObject pushAllDevice(String sendTime, String content,
			Map<String, Object> custom, int environment) {
		XingeApp xinge = new XingeApp(ANDROID_ACCESS_ID, ANDROID_SECRET_KEY);
		JSONObject result = new JSONObject();
		result.append(
				"all",
				xinge.pushAllDevice(XingeApp.DEVICE_ALL,
						createMessage(sendTime, content, custom)));
		XingeApp xinge1 = new XingeApp(IOS_ACCESS_ID, IOS_SECRET_KEY);
		result.append("all", xinge1.pushAllDevice(XingeApp.DEVICE_ALL,
				createMessageIOS(sendTime, content, custom), environment));
		return (result);
	}

	/**
	 * 下发所有设备(静默)
	 *
	 * @return
	 */
	public static JSONObject pushAllDeviceSilent(Map<String, Object> custom, int environment) {
		XingeApp xinge = new XingeApp(ANDROID_ACCESS_ID, ANDROID_SECRET_KEY);
		JSONObject result = new JSONObject();
		result.append(
				"all",
				xinge.pushAllDevice(XingeApp.DEVICE_ALL,
						createHideMessage(custom)));
		XingeApp xinge1 = new XingeApp(IOS_ACCESS_ID, IOS_SECRET_KEY);
		result.append("all", xinge1.pushAllDevice(XingeApp.DEVICE_ALL,
				createSilentMessageIOS(custom), environment));
        log.error("静默推送所有设备参数=["+custom+"]完成！");
		return (result);
	}

	/**
	 * 查询push状态,目前只有对于推送所有设备有效
	 * 
	 * @param pushIds
	 * @param deviceType
	 * @return
	 */
	public static JSONObject queryPushStatus(List<String> pushIds,
			int deviceType) {
		if (deviceType == XingeApp.DEVICE_ANDROID) {
			XingeApp xinge = new XingeApp(ANDROID_ACCESS_ID, ANDROID_SECRET_KEY);
			return xinge.queryPushStatus(pushIds);
		} else if (deviceType == XingeApp.DEVICE_IOS) {
			XingeApp xinge = new XingeApp(IOS_ACCESS_ID, IOS_SECRET_KEY);
			return xinge.queryPushStatus(pushIds);
		}
		return null;
	}
}
