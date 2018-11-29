package com.vcat.module.ec.web;

import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.ec.entity.*;
import com.vcat.module.ec.service.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 商品Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/invite")
public class InviteController extends BaseController {
	@Autowired
	private InviteEarningService inviteEarningService;

	@RequestMapping(value = "inviteList")
	public String inviteList(InviteEarning inviteEarning, HttpServletRequest request, HttpServletResponse response,Model model) {
		model.addAttribute("invite", inviteEarning);
		model.addAttribute("page", inviteEarningService.findPage(new Page<>(request, response), inviteEarning));
		return "module/ec/invite/inviteList";
	}

	@RequiresPermissions("ec:invite:edit")
	@RequestMapping(value = "inviteForm")
	public String inviteForm(InviteEarning inviteEarning,Model model) {
		if(StringUtils.isNotEmpty(inviteEarning.getId())){
			inviteEarning = inviteEarningService.get(inviteEarning);
		}
		model.addAttribute("invite", inviteEarning);
		return "module/ec/invite/inviteForm";
	}

    @RequestMapping(value = "inviteLog")
    public String inviteLog(InviteEarning inviteEarning, HttpServletRequest request, HttpServletResponse response,Model model) {
        model.addAttribute("inviteEarning", inviteEarningService.get(inviteEarning));
        model.addAttribute("page", inviteEarningService.findLogPage(new Page<>(request, response), inviteEarning));
        model.addAttribute("chartList", inviteEarningService.getPieChartList(inviteEarning));
        return "module/ec/invite/inviteLog";
    }

    @ResponseBody
    @RequestMapping(value = "invitePieChart")
    public List<Map<String, Object>> invitePieChart(InviteEarning inviteEarning){
        return inviteEarningService.getPieChartList(inviteEarning);
    }

    @RequiresPermissions("ec:invite:edit")
	@RequestMapping(value = "saveInvite")
	public String saveInvite(InviteEarning inviteEarning, RedirectAttributes redirectAttributes) {
		inviteEarningService.save(inviteEarning);
		addMessage(redirectAttributes, "保存邀请活动成功");
		return "redirect:" + adminPath + "/ec/invite/inviteList";
	}

    @RequiresPermissions("ec:invite:edit")
	@RequestMapping(value = "deleteInvite")
	public String deleteInvite(InviteEarning inviteEarning, RedirectAttributes redirectAttributes) {
		inviteEarningService.delete(inviteEarning);
		addMessage(redirectAttributes, "删除活动成功");
		return "redirect:" + adminPath + "/ec/invite/inviteList";
	}

    @RequiresPermissions("ec:invite:edit")
	@RequestMapping(value = "activateInvite")
	public String activateInvite(InviteEarning inviteEarning, RedirectAttributes redirectAttributes) throws Exception {
		Integer i = inviteEarningService.activate(inviteEarning);
		String msg = i > 0 ? "激活成功" : "激活失败，该邀请活动已被激活或已被删除";
		addMessage(redirectAttributes, msg);
		return "redirect:" + adminPath + "/ec/invite/inviteList";
	}
}
