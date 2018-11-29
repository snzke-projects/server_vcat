package com.vcat.module.ec.web;

import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.core.entity.Area;
import com.vcat.module.core.service.AreaService;
import com.vcat.module.ec.entity.Express;
import com.vcat.module.ec.entity.ExpressTemplate;
import com.vcat.module.ec.entity.FreightConfig;
import com.vcat.module.ec.service.ExpressService;
import com.vcat.module.ec.service.ExpressTemplateService;
import com.vcat.module.ec.service.FreightConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 物流Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/express")
public class ExpressController extends BaseController {

    @Autowired
   	private ExpressService expressService;
    @Autowired
    private ExpressTemplateService expressTemplateService;
    @Autowired
    private FreightConfigService freightConfigService;
    @Autowired
    private AreaService areaService;
	
	@RequiresPermissions("ec:express:list")
	@RequestMapping(value = "list")
	public String list(Express express, Model model) {
        model.addAttribute("list", expressService.findList(express));
		return "module/ec/express/list";	
	}

	@RequiresPermissions("ec:express:edit")
	@RequestMapping(value = "form")
	public String form(Express express, Model model) {
		if(StringUtils.isNotEmpty(express.getId())){
			express = expressService.get(express);
		}
		model.addAttribute("express",express);
		return "module/ec/express/form";
	}

	@RequiresPermissions("ec:express:edit")
	@RequestMapping(value = "save")
	public String save(Express express, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, express)){
			return form(express, model);
		}
		expressService.save(express);
		addMessage(redirectAttributes, "保存物流公司'" + StringUtils.abbr(express.getName(), 100) + "'成功");
		return "redirect:" + adminPath + "/ec/express/list";
	}

    @RequiresPermissions("ec:expressTemplate:list")
    @RequestMapping(value = "expressTemplateList")
    public String expressTemplateList(ExpressTemplate expressTemplate, Model model) {
        model.addAttribute("list", expressTemplateService.findList(expressTemplate));
        return "module/ec/express/templateList";
    }

    @RequiresPermissions("ec:expressTemplate:edit")
    @RequestMapping(value = "expressTemplateForm")
    public String expressTemplateForm(ExpressTemplate expressTemplate, Model model) {
        if(StringUtils.isNotEmpty(expressTemplate.getId())){
            expressTemplate = expressTemplateService.get(expressTemplate);
            FreightConfig freightConfig = new FreightConfig();
            freightConfig.setExpressTemplate(expressTemplate);
            expressTemplate.setFreightConfigList(freightConfigService.findList(freightConfig));
        }
        model.addAttribute("expressTemplate", expressTemplate);

        Area parent = new Area();
        parent.setId("100000");
        Area area = new Area();
        area.setParent(parent);
        model.addAttribute("multipleAreaList",areaService.findList(area));
        return "module/ec/express/templateForm";
    }

    @RequiresPermissions("ec:expressTemplate:edit")
    @RequestMapping(value = "expressTemplateSave")
    public String expressTemplateSave(ExpressTemplate expressTemplate, Model model, RedirectAttributes redirectAttributes) {
        expressTemplateService.save(expressTemplate);
        addMessage(redirectAttributes, "保存成功");
        return "redirect:" + adminPath + "/ec/express/expressTemplateList";
    }

    @ResponseBody
    @RequiresPermissions("ec:expressTemplate:edit")
    @RequestMapping(value = "setExpressTemplateDefault")
    public void setExpressTemplateDefault(ExpressTemplate expressTemplate) {
        expressTemplateService.setDefault(expressTemplate);
    }

    @ResponseBody
    @RequiresPermissions("ec:expressTemplate:edit")
    @RequestMapping(value = "deleteFreightConfig")
    public void deleteFreightConfig(FreightConfig freightConfig) {
        freightConfigService.delete(freightConfig);
    }
}
