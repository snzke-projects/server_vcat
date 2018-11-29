package com.vcat.module.ec.web;

import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.ec.entity.ShareCopywrite;
import com.vcat.module.ec.service.ShareCopywriteService;
import com.vcat.module.ec.service.ShareCopywriteService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 分享文案Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/shareCopywrite")
public class ShareCopywriteController extends BaseController {
	@Autowired
	private ShareCopywriteService shareCopywriteService;

	@RequestMapping(value = "list")
	public String list(ShareCopywrite shareCopywrite, HttpServletRequest request, HttpServletResponse response,Model model) {
		model.addAttribute("shareCopywrite", shareCopywrite);
		model.addAttribute("page", shareCopywriteService.findPage(new Page<>(request, response), shareCopywrite));
		return "module/ec/shareCopywrite/list";
	}

	@RequiresPermissions("ec:shareCopywrite:edit")
	@RequestMapping(value = "form")
	public String form(ShareCopywrite shareCopywrite,Model model) {
		if(StringUtils.isNotEmpty(shareCopywrite.getId())){
			shareCopywrite = shareCopywriteService.get(shareCopywrite);
		}
		model.addAttribute("shareCopywrite", shareCopywrite);
		return "module/ec/shareCopywrite/form";
	}

    @RequiresPermissions("ec:shareCopywrite:edit")
	@RequestMapping(value = "save")
	public String save(ShareCopywrite shareCopywrite, RedirectAttributes redirectAttributes) {
        shareCopywriteService.save(shareCopywrite);
		addMessage(redirectAttributes, "保存分享文案模板成功");
		return "redirect:" + adminPath + "/ec/shareCopywrite/list";
	}

    @RequiresPermissions("ec:shareCopywrite:edit")
	@RequestMapping(value = "delete")
	public String deleteInvite(ShareCopywrite shareCopywrite, RedirectAttributes redirectAttributes) {
        shareCopywriteService.delete(shareCopywrite);
		addMessage(redirectAttributes, "删除分享文案模板成功");
		return "redirect:" + adminPath + "/ec/shareCopywrite/list";
	}
}
