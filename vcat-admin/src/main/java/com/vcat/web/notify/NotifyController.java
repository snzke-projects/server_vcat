package com.vcat.web.notify;

import com.alipay.entity.AlipayRefundResponseData;
import com.alipay.util.AlipayNotify;
import com.vcat.common.web.BaseController;
import com.vcat.module.ec.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 该容器捕获所有后台回调请求
 */
@Controller
@RequestMapping("/notify")
public class NotifyController extends BaseController {
    @Autowired
    private RefundService refundService;

    /**
     * 支付宝退款回调请求
     * @param alipayRefundResponseData 回调数据对象
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "alipay_refund_notify", produces="text/html;charset=UTF-8")
    public String refundAlipay(AlipayRefundResponseData alipayRefundResponseData, HttpServletRequest request){
        logger.info("支付宝退款回调请求<-------------------------------------------------------------------------------");
        if(AlipayNotify.verify(request) && refundService.executionRefundByAlipay(alipayRefundResponseData)) {//验证成功
            return "success";
        }
        return "fail";
    }
}
