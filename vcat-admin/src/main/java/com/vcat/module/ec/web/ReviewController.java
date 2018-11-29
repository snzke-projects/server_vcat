package com.vcat.module.ec.web;

import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.ec.entity.ProductPerformanceLog;
import com.vcat.module.ec.entity.ReviewDetail;
import com.vcat.module.ec.entity.ReviewLibrary;
import com.vcat.module.ec.service.ProductPerformanceLogService;
import com.vcat.module.ec.service.ReviewDetailService;
import com.vcat.module.ec.service.ReviewLibraryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "${adminPath}/ec/review")
public class ReviewController extends BaseController {
    @Autowired
    private ReviewLibraryService reviewLibraryService;
    @Autowired
    private ReviewDetailService reviewDetailService;
    @Autowired
    private ProductPerformanceLogService performanceLogService;

    @RequiresPermissions("ec:review:list")
    @RequestMapping(value = "list")
    public String list(ReviewDetail reviewDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("reviewDetail",reviewDetail);
        model.addAttribute("page", reviewDetailService.findPage(new Page<>(request, response), reviewDetail));
        return "module/ec/review/list";
    }

    @RequiresPermissions("ec:review:edit")
    @RequestMapping(value = "display")
    public String display(ReviewDetail reviewDetail) {
        reviewDetailService.display(reviewDetail);
        return "redirect:" + adminPath + "/ec/review/list";
    }

    @RequiresPermissions("ec:review:edit")
    @RequestMapping(value = "form")
    public String form(Model model) {
        model.addAttribute("productPerformanceLog", new ProductPerformanceLog());
        return "module/ec/review/form";
    }

    @RequiresPermissions("ec:review:edit")
    @RequestMapping(value = "save")
    public String save(ProductPerformanceLog productPerformanceLog, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, productPerformanceLog)){
            return form(model);
        }
        addMessage(redirectAttributes, "设置成功，"+performanceLogService.addPerformance(productPerformanceLog)+"！");
        return "redirect:" + adminPath + "/ec/review/list";
    }

    @RequestMapping(value = "log")
    public String log(ProductPerformanceLog productPerformanceLog,HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("productPerformanceLog",productPerformanceLog);
        model.addAttribute("page", performanceLogService.findPage(new Page<>(request, response), productPerformanceLog));
        return "module/ec/review/performanceLog";
    }

    @RequiresPermissions("ec:review:library:list")
    @RequestMapping(value = "libraryList")
    public String libraryList(ReviewLibrary reviewLibrary, HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("reviewLibrary",reviewLibrary);
        model.addAttribute("page", reviewLibraryService.findPage(new Page<>(request, response), reviewLibrary));
        return "module/ec/review/libraryList";
    }
    @RequiresPermissions("ec:review:library:edit")
    @RequestMapping(value = "libraryForm")
    public String libraryForm(ReviewLibrary reviewLibrary, Model model) {
        if(StringUtils.isNotEmpty(reviewLibrary.getId())){
            reviewLibrary = reviewLibraryService.get(reviewLibrary);
        }
        model.addAttribute("reviewLibrary", reviewLibrary);
        return "module/ec/review/libraryForm";
    }
    @RequiresPermissions("ec:review:library:edit")
    @RequestMapping(value = "librarySave")
    public String librarySave(ReviewLibrary reviewLibrary, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, reviewLibrary)){
            return libraryForm(reviewLibrary, model);
        }
        reviewLibraryService.save(reviewLibrary);
        addMessage(redirectAttributes, "保存评论成功！");
        return "redirect:" + adminPath + "/ec/review/libraryList";
    }
    @RequiresPermissions("ec:review:library:edit")
    @RequestMapping(value = "libraryDelete")
    public String libraryDelete(ReviewLibrary reviewLibrary, RedirectAttributes redirectAttributes) {
        reviewLibraryService.delete(reviewLibrary);
        addMessage(redirectAttributes, "删除评论成功！");
        return "redirect:" + adminPath + "/ec/review/libraryList";
    }
}

