package com.vcat.common;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tencent.xinge.MessageIOS;
import org.json.JSONObject;
import org.junit.Test;

import com.tencent.xinge.Message;
import com.tencent.xinge.XingeApp;
import com.vcat.common.push.PushService;

public class PushTest {
	@Test
	public void test() throws IOException, URISyntaxException {
		List<String> deviceList = new ArrayList<String>();
		deviceList.add("2a7f226d9d86368c4e76a00e6b2f24d8a276e2da");
		deviceList.add("52edb88a4ed784f1487d9bdee4b03884a76ac84a");
		//deviceList.add("a88f982526b655fa405c9c1d28518bd09b9102bb");

		Map<String, Object> custom = new HashMap<String, Object>();
		custom.put("key1", "value1");
		custom.put("key2", 2);
		Message message = new Message();
		message.setType(Message.TYPE_NOTIFICATION);
		message.setTitle("V猫");
		message.setContent("我的测试，测试 测试");
		//message.setSendTime("2015-08-17 20:45:00");
		message.setCustom(custom);
		System.out.println(PushService.pushDeviceListMultiple(message,
				deviceList));
	}

	@Test
	public void test1() throws IOException, URISyntaxException {
		System.out.println(PushService.pushAllDevice("2015-05-14 16:45:00",
				"pushAllDevice 2015-05-13 16:45:00 定时  测试", null, XingeApp.IOSENV_DEV));
	}

	@Test
	public void test3() throws IOException, URISyntaxException {
		System.out.println(PushService.pushSingleDevice(
				"aa3d471140e1c8d045cb8c6893ed694232f89c32",
				PushService.createMessage("2015-05-14 16:41:00", "pushSingleDevice 2015-05-13 16:41:00",
						null)));
	}
	
	@Test
	public void test4() throws IOException, URISyntaxException {
		System.out.println(PushService.cancelTimingPush("35792326", XingeApp.DEVICE_ANDROID));
	}
	
	@Test
	public void test5() throws IOException, URISyntaxException {
		List<String> pids = new ArrayList<String>();
		pids.add("35792326");
		System.out.println(PushService.queryPushStatus(pids, XingeApp.DEVICE_ANDROID));
	}

	/**
	 * long accessId, String secretKey, String content, String token, int env
	 */
	@Test
	public void testSilentIos() {
		//XingeApp xinge = new XingeApp("2200111542", "c7fba88af8f862a865947af7e1b47a95");
//		JSONObject o = XingeApp.pushTokenIos(2200111542l,"c7fba88af8f862a865947af7e1b47a95",
//				"test123","1694fc20a801c81f6f4b6b0cced545c483b9d344f97ae22a053fee96879c372e",XingeApp.IOSENV_PROD);
//		System.out.println(o);

		long accessId = 2200111542l;
		String secretKey = "c7fba88af8f862a865947af7e1b47a95";
		String token = "bfa01cea193603eaf5e8c54cfefa39336636e861738b3305a00f6c640b3aae08";//"efc28cb2147855e25b3bd6370e7e32661152cc1dfa66a3134d34e87074db7109";
		int env = XingeApp.IOSENV_DEV;
		Map<String, Object> custom = new HashMap<String, Object>();
		custom.put(PushService.MSG_TYPE, "7");
		custom.put("NOT_READ", 1);
		XingeApp xinge = new XingeApp(accessId, secretKey);
		JSONObject ret = xinge.pushSingleDevice(token, 		PushService.createSilentMessageIOS(custom), env);
		System.out.println(ret);
	}

	@Test
	public void testIos() {
		//XingeApp xinge = new XingeApp("2200111542", "c7fba88af8f862a865947af7e1b47a95");
//		JSONObject o = XingeApp.pushTokenIos(2200111542l,"c7fba88af8f862a865947af7e1b47a95",
//				"test123","1694fc20a801c81f6f4b6b0cced545c483b9d344f97ae22a053fee96879c372e",XingeApp.IOSENV_PROD);
//		System.out.println(o);

		long accessId = 2200111542l;
		String secretKey = "c7fba88af8f862a865947af7e1b47a95";
		String token = "ceb8a291a7b67654e6892e3d8295f79c73db4bb453b5ef14865a2b47faec017e";
		int env = XingeApp.IOSENV_PROD;
		Map<String, Object> custom = new HashMap<String, Object>();
		custom.put("key1", "value1");
		custom.put("key2", 2);
		XingeApp xinge = new XingeApp(accessId, secretKey);
		JSONObject ret = xinge.pushSingleDevice(token, 		PushService.createMessageIOS("dfsdfsdfsf",custom), env);
		System.out.println(ret);
	}



	@Test
	public void testAnd() {
		Message message = new Message();
		message.setType(Message.TYPE_NOTIFICATION);
		message.setTitle("title");
		message.setContent("content");

		XingeApp xinge = new XingeApp(2100111541, "8967234d8dbd46d7cb4c1cc9434d60f8");
		JSONObject ret = xinge.pushSingleDevice("2a7f226d9d86368c4e76a00e6b2f24d8a276e2da", message);
		System.out.println(ret);
	}

	@Test
	public void testIosCF() {
		Map<String,Object> param = new HashMap<>();
		param.put(PushService.MSG_TYPE, PushService.MSG_TYPE_FUND);
		param.put("MSG_SYSTEM_NOT_READ",1);
		param.put("MSG_FUND_NOT_READ",1);
		param.put("MSG_ORDER_NOT_READ", 1);
		System.out.println(
				PushService.pushSingleDevice("1694fc20a801c81f6f4b6b0cced545c483b9d344f97ae22a053fee96879c372e", PushService.createMessageIOS("测试财富消息", param)));
	}

	@Test
	public void testAndCF(){
		Map<String,Object> param = new HashMap<>();
		param.put(PushService.MSG_TYPE, PushService.MSG_TYPE_FUND);
		param.put("MSG_SYSTEM_NOT_READ",1);
		param.put("MSG_FUND_NOT_READ",1);
		param.put("MSG_ORDER_NOT_READ", 1);
		System.out.println(PushService.pushSingleDevice("2a7f226d9d86368c4e76a00e6b2f24d8a276e2da", PushService.createHideMessage(param)));
		System.out.println(PushService.pushSingleDevice("2a7f226d9d86368c4e76a00e6b2f24d8a276e2da", PushService.createMessage("测试财富消息", param)));
	}

}
