package com.vcat.module.ec.web;

import com.vcat.common.easyui.DataGrid;
import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.ec.entity.UpgradeBonus;
import com.vcat.module.ec.entity.UpgradeCondition;
import com.vcat.module.ec.service.UpgradeBonusService;
import com.vcat.module.ec.service.UpgradeConditionService;
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

@Controller
@RequestMapping(value = "${adminPath}/ec/upgrade")
public class UpgradeController extends BaseController {
	@Autowired
	private UpgradeBonusService upgradeBonusService;
    @Autowired
    private UpgradeConditionService upgradeConditionService;

    @RequiresPermissions("ec:upgradeBonus:list")
    @RequestMapping(value = "bonusList")
    public String bonusList(UpgradeBonus upgradeBonus, Model model) {
        model.addAttribute("upgradeBonus", upgradeBonus);
        return "module/ec/upgrade/bonusList";
    }

    @ResponseBody
    @RequiresPermissions("ec:upgradeBonus:list")
    @RequestMapping(value = "bonusListData")
    public DataGrid bonusListData(UpgradeBonus upgradeBonus, HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(upgradeBonusService.findPage(new Page<>(request, response), upgradeBonus));
    }

    @ResponseBody
    @RequiresPermissions("ec:upgradeBonus:issueBonus")
    @RequestMapping(value = "issueBonus")
    public void issueBonus(UpgradeBonus upgradeBonus) {
        upgradeBonusService.issueBonus(upgradeBonus);
    }

    @ResponseBody
    @RequiresPermissions("ec:upgradeBonus:issueBonus")
    @RequestMapping(value = "batchIssueBonus")
    public void batchIssueBonus(@RequestParam(value = "idStrArray") String[] idStrArray, UpgradeBonus upgradeBonus) {
        upgradeBonusService.batchIssueBonus(idStrArray, upgradeBonus);
    }


    @RequiresPermissions("ec:upgradeCondition:list")
    @RequestMapping(value = "conditionList")
    public String conditionList(UpgradeCondition upgradeCondition, Model model) {
        model.addAttribute("upgradeCondition", upgradeCondition);
        return "module/ec/upgrade/conditionList";
    }

    @ResponseBody
    @RequiresPermissions("ec:upgradeCondition:list")
    @RequestMapping(value = "conditionListData")
    public DataGrid listData(UpgradeCondition upgradeCondition, HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(upgradeConditionService.findPage(new Page<>(request, response), upgradeCondition));
    }

    @RequiresPermissions("ec:upgradeCondition:edit")
    @RequestMapping(value = "conditionForm")
    public String conditionForm(UpgradeCondition upgradeCondition, Model model) {
        if(StringUtils.isNotBlank(upgradeCondition.getId())){
            model.addAttribute("upgradeCondition", upgradeConditionService.get(upgradeCondition));
        }
        return "module/ec/upgrade/conditionForm";
    }

    @RequiresPermissions("ec:upgradeCondition:edit")
    @RequestMapping(value = "conditionSave")
    public String conditionSave(UpgradeCondition upgradeCondition, RedirectAttributes redirectAttributes) {
        upgradeConditionService.save(upgradeCondition);
        addMessage(redirectAttributes, "保存规则成功");
        return "redirect:" + adminPath + "/ec/upgrade/conditionList";
    }
}
