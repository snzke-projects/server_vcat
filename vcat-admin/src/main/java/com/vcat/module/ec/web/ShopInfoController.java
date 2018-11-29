package com.vcat.module.ec.web;

import com.vcat.common.easyui.DataGrid;
import com.vcat.common.persistence.Page;
import com.vcat.common.utils.DateUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.config.CacheConfig;
import com.vcat.module.core.utils.excel.ExportExcel;
import com.vcat.module.ec.entity.ShopInfo;
import com.vcat.module.ec.service.ShopInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping(value = "${adminPath}/ec/shopInfo")
public class ShopInfoController extends BaseController {

    @Autowired
    private ShopInfoService shopInfoService;

    @RequiresPermissions("ec:shopInfo:list")
    @RequestMapping(value = "list")
    public String buyerList(ShopInfo shopInfo, Model model) {
        model.addAttribute("shopInfo",shopInfo);
        return "module/ec/shopInfo/list";
    }

    @ResponseBody
    @RequestMapping(value = "listData")
    public DataGrid listData(ShopInfo shopInfo, HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(shopInfoService.findPage(new Page<>(request, response), shopInfo));
    }

    @ResponseBody
    @RequestMapping(value = "getSame")
    public ShopInfo getSame(ShopInfo shopInfo) {
        return shopInfoService.getSame(shopInfo);
    }

    @ResponseBody
    @RequestMapping(value = "getSameFarmName")
    public String getSameFarmName(ShopInfo shopInfo) {
        return shopInfoService.getSameFarmName(shopInfo);
    }

    @RequiresPermissions("ec:shopInfo:edit")
    @RequestMapping(value = "form")
    public String form(ShopInfo shopInfo, Model model) {
        if(StringUtils.isNotEmpty(shopInfo.getId())){
            shopInfo = shopInfoService.get(shopInfo);
        }
        model.addAttribute("shopInfo", shopInfo);
        return "module/ec/shopInfo/form";
    }

    @RequiresPermissions("ec:shopInfo:edit")
    @RequestMapping(value = "save")
    @CacheEvict(value = {CacheConfig.GET_LEROY_INFO_CACHE}, cacheManager = "apiCM", allEntries = true)
    public String save(ShopInfo shopInfo, RedirectAttributes redirectAttributes) {
        shopInfoService.save(shopInfo);
        addMessage(redirectAttributes, "保存庄园主信息成功");
        return "redirect:" + adminPath + "/ec/shopInfo/list";
    }

    @RequestMapping(value = "exportList")
    public String exportList(ShopInfo shopInfo, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Page<ShopInfo> page = shopInfoService.findPage(new Page<>(request, response, -1), shopInfo);

        String fileName = "庄园主信息列表_" + DateUtils.getDate("yyyyMMddHHmmssSSS") + ".xlsx";
        new ExportExcel("庄园主信息列表", ShopInfo.class).setDataList(page.getList()).write(response, fileName).dispose();
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "getHistory")
    public ShopInfo getHistory(String shopId) {
        return shopInfoService.getShopInfoByShopId(shopId);
    }
}
