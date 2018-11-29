package com.vcat.module.ec.web;

import com.vcat.common.easyui.DataGrid;
import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.ec.entity.Suggest;
import com.vcat.module.ec.service.SuggestService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 建议反馈Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/suggest")
public class SuggestController extends BaseController {

    @Autowired
   	private SuggestService suggestService;
	
	@RequestMapping(value = "list")
	public String list(Suggest suggest, Model model) {
        model.addAttribute("suggest",suggest);
		return "module/ec/suggest/list";
	}

    @ResponseBody
    @RequestMapping(value = "listData")
    public DataGrid listData(Suggest suggest, HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(suggestService.findPage(new Page<>(request, response), suggest));
    }

    @ResponseBody
	@RequestMapping(value = "get")
	public Suggest get(Suggest suggest) {
		if(StringUtils.isNotEmpty(suggest.getId())){
            suggest = suggestService.get(suggest);
		}
		return suggest;
	}

    @ResponseBody
	@RequiresPermissions("ec:suggest:process")
	@RequestMapping(value = "process")
	public void process(Suggest suggest) {
        suggestService.process(suggest);
	}
}
