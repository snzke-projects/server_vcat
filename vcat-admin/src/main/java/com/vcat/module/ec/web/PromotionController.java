package com.vcat.module.ec.web;

import com.vcat.common.easyui.DataGrid;
import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.ec.entity.Promotion;
import com.vcat.module.ec.service.PromotionService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "${adminPath}/ec/promotion")
public class PromotionController extends BaseController {
    @Autowired
    private PromotionService promotionService;
    
    @RequiresPermissions("ec:promotion:list")
    @RequestMapping(value = "list")
    public String list(Promotion promotion, Model model) {
        model.addAttribute("promotion",promotion);
        return "module/ec/promotion/list";
    }

    @ResponseBody
    @RequiresPermissions("ec:promotion:list")
    @RequestMapping(value = "listData")
    public DataGrid listData(Promotion promotion, HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(promotionService.findPage(new Page<>(request, response), promotion));
    }

    @RequiresPermissions("ec:promotion:edit")
    @RequestMapping(value = "form")
    public String form(Promotion promotion, Model model) {
        if(StringUtils.isNotEmpty(promotion.getId())){
            promotion = promotionService.get(promotion);
        }
        model.addAttribute("promotion",promotion);
        return "module/ec/promotion/form";
    }

    @RequiresPermissions("ec:promotion:edit")
    @RequestMapping(value = "save")
    public String save(Promotion promotion, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, promotion)){
            return form(promotion, model);
        }
        promotionService.save(promotion);
        addMessage(redirectAttributes, "保存优惠卷'" + StringUtils.abbr(promotion.getName(), 100) + "'成功");
        return "redirect:" + adminPath + "/ec/promotion/list";
    }

    @ResponseBody
    @RequestMapping(value = "activate")
    public void activate(Promotion promotion) {
        promotionService.activate(promotion);
    }
}
