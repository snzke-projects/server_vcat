package com.tencent.service;

import com.tencent.common.Configure;
import com.tencent.protocol.pay_protocol.WebPayReqData;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

public class WebPayService extends BaseService {

	public WebPayService(Configure configure) throws IllegalAccessException,
			InstantiationException, ClassNotFoundException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException {
		super(Configure.WEB_PAY_API, configure);
	}

	/**
	 * 请求支付服务
	 * 
	 * @param webPayReqData
	 *            这个数据对象里面包含了API要求提交的各种数据字段
	 * @return API返回的数据
	 * @throws Exception
	 */
	public String request(WebPayReqData webPayReqData) throws Exception {

		// --------------------------------------------------------------------
		// 发送HTTPS的Post请求到API地址
		// --------------------------------------------------------------------
		String responseString = sendPost(webPayReqData);

		return responseString;
	}

}
