package com.vcat.module.ec.web;

import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.ec.entity.Distribution;
import com.vcat.module.ec.service.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 配送方Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/distribution")
public class DistributionController extends BaseController {

    @Autowired
    private DistributionService distributionService;

    @RequiresPermissions("ec:distribution:list")
    @RequestMapping(value = "list")
    public String list(Distribution distribution, HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("page", distributionService.findPage(new Page<>(request, response), distribution));
        return "module/ec/distribution/list";
    }

    @RequiresPermissions("ec:distribution:edit")
    @RequestMapping(value = "form")
    public String form(Distribution distribution, Model model) {
        if(StringUtils.isNotEmpty(distribution.getId())){
            distribution = distributionService.get(distribution);
        }
        model.addAttribute("distribution",distribution);
        return "module/ec/distribution/form";
    }

    @RequiresPermissions("ec:distribution:edit")
    @RequestMapping(value = "save")
    public String save(Distribution distribution, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, distribution)){
            return form(distribution, model);
        }
        distributionService.save(distribution);
        addMessage(redirectAttributes, "保存配送方'" + StringUtils.abbr(distribution.getName(), 100) + "'成功");
        return "redirect:" + adminPath + "/ec/distribution/list";
    }
}
