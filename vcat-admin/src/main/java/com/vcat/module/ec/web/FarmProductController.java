package com.vcat.module.ec.web;

import com.vcat.common.easyui.DataGrid;
import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.ec.entity.FarmProduct;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.service.FarmProductService;
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

@Controller
@RequestMapping(value = "${adminPath}/ec/farmProduct")
public class FarmProductController extends BaseController {

    @Autowired
    private FarmProductService farmProductService;

    @RequiresPermissions("ec:farmProduct:list")
    @RequestMapping(value = "list")
    public String buyerList(FarmProduct farmProduct, Model model) {
        model.addAttribute("farmProduct",farmProduct);
        return "module/ec/farmProduct/list";
    }

    @ResponseBody
    @RequestMapping(value = "listData")
    public DataGrid listData(FarmProduct farmProduct, HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(farmProductService.findPage(new Page<>(request, response), farmProduct));
    }

    @RequestMapping(value = "form")
    public String form(FarmProduct farmProduct, Model model) {
        if(null != farmProduct.getFarm() && StringUtils.isNotEmpty(farmProduct.getFarm().getId())){
            farmProduct = farmProductService.get(farmProduct);
        }
        model.addAttribute("farmProduct",farmProduct);
        return "module/ec/farmProduct/form";
    }

    @RequestMapping(value = "save")
    public String save(FarmProduct farmProduct, RedirectAttributes redirectAttributes) {
        farmProductService.save(farmProduct);
        addMessage(redirectAttributes, "保存庄园关联商品'" + StringUtils.abbr(farmProduct.getFarm().getName(), 100) + "'成功");
        return "redirect:" + adminPath + "/ec/farmProduct/list";
    }

    @ResponseBody
    @RequestMapping(value = "checkSame")
    public List<Product> checkSame(@RequestParam("farmId") String farmId, @RequestParam("productIds") String productIds) {
        return farmProductService.checkSame(farmId, productIds);
    }

    @ResponseBody
    @RequestMapping(value = "updateWechatNo")
    public void updateWechatNo(FarmProduct farmProduct) {
        farmProductService.updateWechatNo(farmProduct);
    }
}
