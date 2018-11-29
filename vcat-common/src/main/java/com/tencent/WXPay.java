package com.tencent;

import com.tencent.business.DownloadBillBusiness;
import com.tencent.business.RefundBusiness;
import com.tencent.business.RefundQueryBusiness;
import com.tencent.business.ScanPayBusiness;
import com.tencent.common.Configure;
import com.tencent.protocol.downloadbill_protocol.DownloadBillReqData;
import com.tencent.protocol.pay_protocol.ScanPayReqData;
import com.tencent.protocol.pay_protocol.WebPayReqData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.tencent.protocol.refund_protocol.RefundReqData;
import com.tencent.protocol.refund_query_protocol.RefundQueryReqData;
import com.tencent.protocol.reverse_protocol.ReverseReqData;
import com.tencent.service.*;
import com.vcat.common.config.Global;

/**
 * SDK总入口
 */
public class WXPay {

    public Configure getConfigure() {
        return configure;
    }

    private Configure configure = new Configure();


    /**
     * 初始化SDK依赖的几个关键配置
     * @param key 签名算法需要用到的秘钥
     * @param appID 公众账号ID
     * @param mchID 商户ID
     * @param sdbMchID 子商户ID，受理模式必填
     * @param certLocalPath HTTP证书在服务器中的路径，用来加载证书用
     * @param certPassword HTTP证书的密码，默认等于MCHID
     */
    public void initConfiguration(String key,String appID,String mchID,String sdbMchID,String certLocalPath,String certPassword){
        configure.setKey(key);
        configure.setAppID(appID);
        configure.setMchID(mchID);
        configure.setSubMchID(sdbMchID);
        configure.setCertLocalPath(certLocalPath);
        configure.setCertPassword(certPassword);
    }

    /**
     * 初始化公众号支付配置
     */
    public void initConfigurationForWap(){
        configure.setKey(Global.getConfig("wechat.pay.app.key"));
        configure.setAppID(Global.getConfig("wechat.pay.app.id"));
        configure.setMchID(Global.getConfig("wechat.pay.app.mch.id"));
        configure.setCertLocalPath("com/tencent/common/apiclient_cert.p12");
        configure.setCertPassword(Global.getConfig("wechat.pay.app.mch.id"));
    }

     /**
     * 初始化微信App支付
     */
    public void initConfigurationForMobile(){
        configure.setKey(Global.getConfig("wechat.pay.app.key"));
        configure.setAppID(Global.getConfig("wechat.pay.app.id.mobile"));
        configure.setMchID(Global.getConfig("wechat.pay.app.mch.id.mobile"));
        configure.setCertLocalPath("com/tencent/common/apiclient_cert_mobile.p12");
        configure.setCertPassword(Global.getConfig("wechat.pay.app.mch.id.mobile"));
    }
    
    /**
     * 请求Web支付服务
     * @param webPayReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的数据
     * @throws Exception
     */
    public String requestWebPayService(WebPayReqData webPayReqData) throws Exception{
        return new WebPayService(configure).request(webPayReqData);
    }

    /**
     * 请求支付服务
     * @param scanPayReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的数据
     * @throws Exception
     */
    public String requestScanPayService(ScanPayReqData scanPayReqData) throws Exception{
        return new ScanPayService(configure).request(scanPayReqData);
    }

    /**
     * 请求支付查询服务
     * @param scanPayQueryReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
	public String requestScanPayQueryService(ScanPayQueryReqData scanPayQueryReqData) throws Exception{
		return new ScanPayQueryService(configure).request(scanPayQueryReqData);
	}

    /**
     * 请求退款服务
     * @param refundReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
    public String requestRefundService(RefundReqData refundReqData) throws Exception{
        return new RefundService(configure).request(refundReqData);
    }

    /**
     * 请求退款查询服务
     * @param refundQueryReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
	public String requestRefundQueryService(RefundQueryReqData refundQueryReqData) throws Exception{
		return new RefundQueryService(configure).request(refundQueryReqData);
	}

    /**
     * 请求撤销服务
     * @param reverseReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
	public String requestReverseService(ReverseReqData reverseReqData) throws Exception{
		return new ReverseService(configure).request(reverseReqData);
	}

    /**
     * 请求对账单下载服务
     * @param downloadBillReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
    public String requestDownloadBillService(DownloadBillReqData downloadBillReqData) throws Exception{
        return new DownloadBillService(configure).request(downloadBillReqData);
    }

    /**
     * 直接执行被扫支付业务逻辑（包含最佳实践流程）
     * @param scanPayReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @throws Exception
     */
    public void doScanPayBusiness(ScanPayReqData scanPayReqData, ScanPayBusiness.ResultListener resultListener) throws Exception {
        new ScanPayBusiness(configure).run(scanPayReqData, resultListener);
    }

    /**
     * 调用退款业务逻辑
     * @param refundReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 业务逻辑可能走到的结果分支，需要商户处理
     * @throws Exception
     */
    public void doRefundBusiness(RefundReqData refundReqData, RefundBusiness.ResultListener resultListener) throws Exception {
        new RefundBusiness(configure).run(refundReqData,resultListener);
    }

    /**
     * 运行退款查询的业务逻辑
     * @param refundQueryReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @throws Exception
     */
    public void doRefundQueryBusiness(RefundQueryReqData refundQueryReqData,RefundQueryBusiness.ResultListener resultListener) throws Exception {
        new RefundQueryBusiness().run(refundQueryReqData,resultListener);
    }

    /**
     * 请求对账单下载服务
     * @param downloadBillReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @return API返回的XML数据
     * @throws Exception
     */
    public void doDownloadBillBusiness(DownloadBillReqData downloadBillReqData,DownloadBillBusiness.ResultListener resultListener) throws Exception {
        new DownloadBillBusiness(configure).run(downloadBillReqData,resultListener,configure);
    }


}
