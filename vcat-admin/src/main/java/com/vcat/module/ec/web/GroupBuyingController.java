package com.vcat.module.ec.web;

import com.vcat.common.easyui.DataGrid;
import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.ec.entity.GroupBuying;
import com.vcat.module.ec.entity.GroupBuyingSponsor;
import com.vcat.module.ec.entity.Order;
import com.vcat.module.ec.service.GroupBuyingService;
import com.vcat.module.ec.service.GroupBuyingSponsorService;
import com.vcat.module.ec.service.OrderService;
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
@RequestMapping(value = "${adminPath}/ec/groupBuying")
public class GroupBuyingController extends BaseController {
    @Autowired
    private GroupBuyingService groupBuyingService;
    @Autowired
    private GroupBuyingSponsorService groupBuyingSponsorService;
    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "list")
    public String list(GroupBuying groupBuying, Model model) {
        model.addAttribute("groupBuying", groupBuying);
        return "module/ec/groupBuying/list";
    }

    @ResponseBody
    @RequestMapping(value = "listData")
    public DataGrid listData(GroupBuying groupBuying, HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(groupBuyingService.findPage(new Page(request, response), groupBuying));
    }

    @RequiresPermissions("ec:groupBuying:edit")
    @RequestMapping(value = "form")
    public String form(GroupBuying groupBuying,Model model) {
        if(StringUtils.isNotEmpty(groupBuying.getId())){
            groupBuying = groupBuyingService.get(groupBuying);
        }
        model.addAttribute("groupBuying", groupBuying);
        return "module/ec/groupBuying/form";
    }

    @RequiresPermissions("ec:groupBuying:edit")
    @RequestMapping(value = "save")
    public String save(GroupBuying groupBuying, RedirectAttributes redirectAttributes) {
        groupBuyingService.save(groupBuying);
        addMessage(redirectAttributes, "保存团购活动成功");
        return "redirect:" + adminPath + "/ec/groupBuying/list";
    }

    @ResponseBody
    @RequiresPermissions("ec:groupBuying:edit")
    @RequestMapping(value = "activate")
    public void activate(GroupBuying groupBuying) throws Exception {
        groupBuyingService.activate(groupBuying);
    }

    @RequestMapping(value = "activityList")
    public String activityList(GroupBuyingSponsor sponsor, Model model) {
        model.addAttribute("sponsor", sponsor);
        return "module/ec/groupBuying/activityList";
    }

    @ResponseBody
    @RequestMapping(value = "activityListData")
    public DataGrid activityListData(GroupBuyingSponsor sponsor, HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(groupBuyingSponsorService.findPage(new Page(request, response), sponsor));
    }

    @RequestMapping(value = "groupBuyingOrder")
    public String groupBuyingOrder(Order order, HttpServletRequest request, HttpServletResponse response, Model model) {
        if(StringUtils.isEmpty(order.getSqlMap().get("type"))){
            order.getSqlMap().put("type","all");
        }
        model.addAttribute("order",order);
        return "module/ec/groupBuying/groupBuyingOrder";
    }

    @ResponseBody
    @RequestMapping(value = "groupBuyingOrderData")
    public DataGrid groupBuyingOrderData(Order order, HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isEmpty(order.getSqlMap().get("type"))){
            order.getSqlMap().put("type","all");
        }
        return DataGrid.parse(orderService.findPage(new Page<>(request, response), order));
    }

    @ResponseBody
    @RequestMapping(value = "getAnother")
    public GroupBuying getAnother(GroupBuying groupBuying) {
        return groupBuyingService.getAnother(groupBuying);
    }
}
