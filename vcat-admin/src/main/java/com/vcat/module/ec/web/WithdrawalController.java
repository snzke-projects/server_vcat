package com.vcat.module.ec.web;

import com.vcat.common.persistence.Page;
import com.vcat.common.utils.DateUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.core.utils.excel.ExportExcel;
import com.vcat.module.ec.entity.Gateway;
import com.vcat.module.ec.entity.Withdrawal;
import com.vcat.module.ec.entity.WithdrawalExcel;
import com.vcat.module.ec.service.GatewayService;
import com.vcat.module.ec.service.WithdrawalService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 订单Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/withdrawal")
public class WithdrawalController extends BaseController {
	@Autowired
	private WithdrawalService withdrawalService;
    @Autowired
    private GatewayService gatewayService;

	@RequestMapping(value = "list")
	public String list(Withdrawal withdrawal,@RequestParam(value = "type",required = false)String type, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isEmpty(type)){
			type = "financeWithdrawalList";
		}
		withdrawal.getSqlMap().put("type",type);
		model.addAttribute("page", withdrawalService.findPage(new Page<>(request, response), withdrawal));
		model.addAttribute("withdrawal",withdrawal);
		model.addAttribute("type",type);
        model.addAttribute("gatewayTypeList", gatewayService.findList(new Gateway()));
		return "module/ec/withdrawal/list";
	}

	@RequestMapping(value = "form")
	public String form(Withdrawal withdrawal, Model model) {
		if(StringUtils.isNotEmpty(withdrawal.getId())){
			withdrawal = withdrawalService.getById(withdrawal.getId());
		}
		model.addAttribute("withdrawal", withdrawal);
		return "module/ec/withdrawal/form";
	}

    @ResponseBody
    @RequestMapping(value = "find")
    public Withdrawal find(Withdrawal withdrawal) {
        if(StringUtils.isNotEmpty(withdrawal.getId())){
            withdrawal = withdrawalService.getById(withdrawal.getId());
        }
        return withdrawal;
    }

	@ResponseBody
	@RequiresPermissions("ec:finance:withdrawal:handler")
	@RequestMapping(value = "handler")
	public void handler(Withdrawal withdrawal) {
		String result = "驳回提现申请";
		if(withdrawal.getWITHDRAWAL_STATUS_SUCCESS().equals(withdrawal.getWithdrawalStatus())){
			result = "处理提现申请完成";
		}
        String note = "！";
        if(StringUtils.isNotEmpty(withdrawal.getNote())){
            note = " ：\r\n\t" + withdrawal.getNote();
        }
        withdrawal.getSqlMap().put("note",UserUtils.getUser().getName() + " " + result + note);
		withdrawalService.handler(withdrawal);
	}

    @ResponseBody
    @RequiresPermissions("ec:finance:withdrawal:handler")
    @RequestMapping(value = "batchHandler")
    public void batchHandler(@RequestParam(value = "withdrawalIds")String[]withdrawalIds) {
        withdrawalService.batchHandler(withdrawalIds);
    }

	@RequiresPermissions("ec:finance:withdrawal:history")
	@RequestMapping(value = "history")
	public String history(Withdrawal withdrawal, HttpServletRequest request, HttpServletResponse response, Model model) {
		return list(withdrawal,"historyWithdrawalList",request,response,model);
	}

    @RequestMapping(value = "export", method= RequestMethod.POST)
    public void exportShipList(Withdrawal withdrawal,@RequestParam(value = "type",required = false)String type, HttpServletResponse response) {
        String title = "提现申请";
        if("historyWithdrawalList".equals(type)){
            title += "历史";
        }else{
            title += "列表";
        }
        try {
            withdrawal.getSqlMap().put("type",type);
            String fileName = title + DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            List<WithdrawalExcel> list = withdrawalService.exprotList(withdrawal);
            new ExportExcel(title, WithdrawalExcel.class).setDataList(list).write(response, fileName).dispose();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
