package com.vcat.module.ec.web;

import com.vcat.common.easyui.DataGrid;
import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.ec.entity.Gateway;
import com.vcat.module.ec.entity.Refund;
import com.vcat.module.ec.entity.Supplier;
import com.vcat.module.ec.service.GatewayService;
import com.vcat.module.ec.service.RefundRequestService;
import com.vcat.module.ec.service.RefundService;
import com.vcat.module.ec.service.SupplierService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 订单Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/refund")
public class RefundController extends BaseController {
	@Autowired
	private RefundService refundService;
	@Autowired
	private SupplierService supplierService;
    @Autowired
    private GatewayService gatewayService;
    @Autowired
    private RefundRequestService refundRequestService;

	@RequiresPermissions("ec:finance:refund:list")
	@RequestMapping(value = "list")
	public String list(Refund refund,@RequestParam(value = "type",required = false)String type, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isEmpty(type)){
			type = "financeRefundList";
		}
		refund.getSqlMap().put("type",type);
		model.addAttribute("supplierList", supplierService.findList(new Supplier()));
        model.addAttribute("gatewayList", gatewayService.findList(new Gateway()));
		model.addAttribute("refund",refund);
		model.addAttribute("type",type);
		return "module/ec/refund/list";
	}

    @ResponseBody
    @RequiresPermissions("ec:finance:refund:list")
    @RequestMapping(value = "listData")
    public DataGrid listData(Refund refund, @RequestParam(value = "type",required = false)String type, HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isEmpty(type)){
            type = "financeRefundList";
        }
        refund.getSqlMap().put("type",type);
        return DataGrid.parse(refundService.findPage(new Page<>(request, response), refund));
    }

	@ResponseBody
	@RequiresPermissions("ec:finance:refund:verify")
	@RequestMapping(value = "verify")
	public void verify(Refund refund,@RequestParam(value = "verifyNote")String verifyNote) {
		String result = "审核退款不通过";
		if(Refund.REFUND_STATUS_REFUND.equals(refund.getRefundStatus())){
			result = "审核退款通过";
		}
		refund.getSqlMap().put("verifyNote",verifyNote);
		refund.setNote(UserUtils.getUser().getName() + " " + result + " ：" + verifyNote);
		refundService.verify(refund);
	}

	@ResponseBody
	@RequiresPermissions("ec:finance:refund:verify")
	@RequestMapping(value = "confirmRefundCompleted")
	public void confirmRefundCompleted(Refund refund,@RequestParam(value = "verifyNote")String verifyNote) {
		String result = "处理退款失败";
		if(refund.getREFUND_STATUS_COMPLETED().equals(refund.getRefundStatus())){
			result = "确认退款成功";
		}
		refund.getSqlMap().put("verifyNote", verifyNote);
        String note = UserUtils.getUser().getName() + " " + result;
        note += StringUtils.isEmpty(verifyNote) ? "。" : " ：" + verifyNote;
		refund.setNote(note);
		refundService.confirmRefundCompleted(refund);
	}

	@RequiresPermissions("ec:finance:refund:history")
	@RequestMapping(value = "history")
	public String history(Refund refund, HttpServletRequest request, HttpServletResponse response, Model model) {
		return list(refund,"historyRefundList",request,response,model);
	}

    @RequestMapping(value = "showReturnHistory")
    public String showReturnHistory(Refund refund, Model model){
        model.addAttribute("refund", refund);
        return "module/ec/refund/history";
    }

    @RequestMapping(value = "refundRequestByAlipay")
    public String refundRequestByAlipay(Refund refund, Model model){
        model.addAttribute("formString", refundRequestService.refundRequestByAlipay(refund));
        return "module/ec/refund/refundAlipay";
    }

    @ResponseBody
    @RequestMapping(value = "refundRequestByWechat")
    public boolean refundRequestByWechat(Refund refund){
        return refundService.executionRefundByWeChat(refund);
    }

    @ResponseBody
    @RequestMapping(value = "refundRequestByWechatMobile")
    public boolean refundRequestByWechatMobile(Refund refund){
        return refundService.executionRefundByWeChatMobile(refund);
    }

    @ResponseBody
    @RequestMapping(value = "returnHistoryData")
    public DataGrid returnHistoryData(Refund refund, HttpServletRequest request, HttpServletResponse response){
        refund.getSqlMap().put("type","historyRefundList");
        return DataGrid.parse(refundService.findHistory(new Page<>(request, response), refund));
    }

}