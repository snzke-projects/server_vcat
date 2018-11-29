package com.vcat.common;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.vcat.common.sms.SmsClient;
import com.vcat.common.sms.UcpSmsRestClient;


public class SmsClientTest {
	private static Logger logger = Logger.getLogger(SmsClientTest.class);
	@Test
	public void smsTest() throws IOException, URISyntaxException{
		System.out.println(SmsClient.getUserInfo());
	}
	
	@Test
	public void ucpaasSmsTest() {
		if(UcpSmsRestClient.templateSMS(true, UcpSmsRestClient.VERIFY_TMP_ID, "15802810815", "23232323,1")){
			logger.debug("手机号码:15802810815,验证码:23232323发送成功");
		}else{
			logger.warn("手机号码:15802810815,验证码:23232323发送失败");
		}
	}
}
