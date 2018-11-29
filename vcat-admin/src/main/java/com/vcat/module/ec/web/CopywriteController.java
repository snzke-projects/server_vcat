package com.vcat.module.ec.web;

import com.vcat.common.easyui.DataGrid;
import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.config.CacheConfig;
import com.vcat.module.ec.entity.Copywrite;
import com.vcat.module.ec.service.CopywriteService;
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

/**
 * 文案Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/copywrite")
public class CopywriteController extends BaseController {
    @Autowired
    private CopywriteService copywriteService;

    @RequestMapping(value = "list")
    public String list(Copywrite copywrite, Model model) {
        model.addAttribute("copywrite", copywrite);
        return "module/ec/copywrite/list";
    }

    @ResponseBody
    @RequestMapping(value = "listData")
    public DataGrid listData(Copywrite copywrite,HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(copywriteService.findPage(new Page(request, response), copywrite));
    }

    @RequiresPermissions("ec:copywrite:edit")
    @RequestMapping(value = "form")
    public String form(Copywrite copywrite,Model model) {
        if(StringUtils.isNotEmpty(copywrite.getId())){
            copywrite = copywriteService.get(copywrite);
        }
        model.addAttribute("copywrite", copywrite);
        return "module/ec/copywrite/form";
    }

    @RequiresPermissions("ec:copywrite:edit")
    @RequestMapping(value = "save")
    @CacheEvict(value = {CacheConfig.GET_COPYWRITE_LIST_CACHE}, cacheManager = "apiCM", allEntries = true)
    public String save(Copywrite copywrite, RedirectAttributes redirectAttributes) {
        copywriteService.save(copywrite);
        addMessage(redirectAttributes, "保存文案【" + copywrite.getTitle() + "】成功");
        return "redirect:" + adminPath + "/ec/copywrite/list";
    }

    @ResponseBody
    @RequiresPermissions("ec:copywrite:edit")
    @RequestMapping(value = "activate")
    public int activate(Copywrite copywrite) throws Exception {
        return copywriteService.activate(copywrite);
    }

}
