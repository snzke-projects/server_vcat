package com.vcat.module.ec.web;

import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.ec.entity.Coupon;
import com.vcat.module.ec.service.CouponService;
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
 * 购物卷Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/coupon")
public class CouponController extends BaseController {
    @Autowired
    private CouponService couponService;

    @RequestMapping(value = "couponList")
    public String couponList(Coupon coupon, HttpServletRequest request, HttpServletResponse response,Model model) {
        model.addAttribute("coupon", coupon);
        model.addAttribute("page", couponService.findPage(new Page<>(request, response), coupon));
        return "module/ec/coupon/couponList";
    }

    @RequiresPermissions("ec:coupon:edit")
    @RequestMapping(value = "couponForm")
    public String couponForm(Coupon coupon,Model model) {
        if(StringUtils.isNotEmpty(coupon.getId())){
            coupon = couponService.get(coupon);
        }
        model.addAttribute("coupon", coupon);
        return "module/ec/coupon/couponForm";
    }

    @RequestMapping(value = "couponLog")
    public String couponLog(Coupon coupon, HttpServletRequest request, HttpServletResponse response,Model model) {
        model.addAttribute("coupon", couponService.get(coupon));
        model.addAttribute("page", couponService.findLogPage(new Page<>(request, response), coupon));
        model.addAttribute("chartList", couponService.getPieChartList(coupon));
        return "module/ec/coupon/couponLog";
    }

    @ResponseBody
    @RequestMapping(value = "couponPieChart")
    public List<Map<String, Object>> couponPieChart(Coupon coupon){
        return couponService.getPieChartList(coupon);
    }

    @RequiresPermissions("ec:coupon:edit")
    @RequestMapping(value = "saveCoupon")
    public String saveCoupon(Coupon coupon, RedirectAttributes redirectAttributes) {
        couponService.save(coupon);
        addMessage(redirectAttributes, "保存券成功");
        return "redirect:" + adminPath + "/ec/coupon/couponList";
    }

    @RequiresPermissions("ec:coupon:edit")
    @RequestMapping(value = "deleteCoupon")
    public String deleteCoupon(Coupon coupon, RedirectAttributes redirectAttributes) {
        couponService.delete(coupon);
        addMessage(redirectAttributes, "删除券成功");
        return "redirect:" + adminPath + "/ec/coupon/couponList";
    }

    @RequiresPermissions("ec:coupon:edit")
    @RequestMapping(value = "activateCoupon")
    public String activateCoupon(Coupon coupon, RedirectAttributes redirectAttributes) throws Exception {
        Integer i = couponService.activate(coupon);
        String msg = i > 0 ? "激活成功" : "激活失败，该券已被激活或已被删除";
        addMessage(redirectAttributes, msg);
        return "redirect:" + adminPath + "/ec/coupon/couponList";
    }

}
