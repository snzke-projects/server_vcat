package com.vcat.module.content.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vcat.common.persistence.Page;
import com.vcat.common.utils.CookieUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.content.entity.Site;
import com.vcat.module.content.service.SiteService;
import com.vcat.module.core.utils.UserUtils;

/**
 * 站点Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/cms/site")
public class SiteController extends BaseController {

	@Autowired
	private SiteService siteService;

	@ModelAttribute
	public Site get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return siteService.get(id);
		} else {
			return new Site();
		}
	}

	@RequiresPermissions("cms:site:view")
	@RequestMapping(value = { "list", "" })
	public String list(Site site, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<Site> page = siteService.findPage(
				new Page<Site>(request, response), site);
		model.addAttribute("page", page);
		return "module/cms/siteList";
	}

	@RequiresPermissions("cms:site:view")
	@RequestMapping(value = "form")
	public String form(Site site, Model model) {
		model.addAttribute("site", site);
		return "module/cms/siteForm";
	}

	@RequiresPermissions("cms:site:edit")
	@RequestMapping(value = "save")
	public String save(Site site, Model model,
			RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, site)) {
			return form(site, model);
		}
		siteService.save(site);
		addMessage(redirectAttributes, "保存站点'" + site.getName() + "'成功");
		return "redirect:" + adminPath + "/cms/site/?repage";
	}

	@RequiresPermissions("cms:site:edit")
	@RequestMapping(value = "delete")
	public String delete(Site site,
			@RequestParam(required = false) Boolean isRe,
			RedirectAttributes redirectAttributes) {
		if (Site.isDefault(site.getId())) {
			addMessage(redirectAttributes, "删除站点失败, 不允许删除默认站点");
		} else {
			siteService.delete(site, isRe);
			addMessage(redirectAttributes, (isRe != null && isRe ? "恢复" : "")
					+ "删除站点成功");
		}
		return "redirect:" + adminPath + "/cms/site/?repage";
	}

	/**
	 * 选择站点
	 * 
	 * @param id
	 * @return
	 */
	@RequiresPermissions("cms:site:select")
	@RequestMapping(value = "select")
	public String select(String id, boolean flag, HttpServletResponse response) {
		if (id != null) {
			UserUtils.putCache("siteId", id);
			// 保存到Cookie中，下次登录后自动切换到该站点
			CookieUtils.setCookie(response, "siteId", id);
		}
		if (flag) {
			return "redirect:" + adminPath;
		}
		return "module/cms/siteSelect";
	}
}
