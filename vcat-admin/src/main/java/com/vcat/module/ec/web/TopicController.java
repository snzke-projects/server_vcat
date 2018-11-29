package com.vcat.module.ec.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.ec.entity.Brand;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.entity.Topic;
import com.vcat.module.ec.service.BrandService;
import com.vcat.module.ec.service.TopicService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
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
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/ec/topic")
public class TopicController extends BaseController {
    @Autowired
   	private BrandService brandService;
    @Autowired
    private TopicService topicService;
	
	@RequiresPermissions("ec:topic:list")
	@RequestMapping(value = "list")
	public String list(Topic topic, Model model) {
		List<Topic> list = Lists.newArrayList();
		List<Topic> sourceList = topicService.findList(topic);
		Topic.sortList(list, sourceList, "0");
        model.addAttribute("list", list);
		return "module/ec/topic/list";	
	}

	@RequestMapping(value = "form")
	public String form(Topic topic, Model model) {
		if(StringUtils.isNotEmpty(topic.getId())){
			topic = topicService.get(topic);
			topic.setParent(topicService.get(topic.getParent().getId()));
		}

        // 获取排序号，最末节点排序号+10
        if (StringUtils.isEmpty(topic.getId()) && null != topic.getParent()){
            topic.setDisplayOrder(topicService.getHighestOrder(topic.getParent().getId()) + 10);
        }
		model.addAttribute("topic",topic);
		return "module/ec/topic/form";
	}

	@RequiresPermissions("ec:topic:edit")
	@RequestMapping(value = "save")
	public String save(Topic topic, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, topic)){
			return form(topic, model);
		}
		topicService.save(topic);
		addMessage(redirectAttributes, "保存专题'" + StringUtils.abbr(topic.getTitle(), 100) + "'成功");
		return "redirect:" + adminPath + "/ec/topic/list";
	}
	
	@RequiresPermissions("ec:topic:edit")
	@RequestMapping(value = "delete")
	public String delete(Topic topic, RedirectAttributes redirectAttributes) {
		topicService.delete(topic);
		addMessage(redirectAttributes, "删除成功");
		return "redirect:" + adminPath + "/ec/topic/list";
	}

    @RequiresPermissions("ec:topic:edit")
    @RequestMapping(value = "activate")
    public String activate(Topic topic, RedirectAttributes redirectAttributes) {
        topicService.activate(topic);
        addMessage(redirectAttributes, (topic.getIsActivate() == 0 ? "取消" : "") + "激活专题'" + StringUtils.abbr(topic.getTitle(), 100) + "'成功");
        return "redirect:" + adminPath + "/ec/topic/list";
    }
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Topic> list = topicService.findList(new Topic());
        Topic t = topicService.get(extId);
		for (int i=0; i<list.size(); i++){
			Topic e = list.get(i);
			if (null != t && e.getLft() >= t.getLft() && e.getRgt() <= t.getRgt()){
                continue;
			}
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", e.getId());
            map.put("pId", e.getParent()!=null?e.getParent().getId():0);
            map.put("name", e.getTitle());
            mapList.add(map);
		}
		return mapList;
	}

    @RequestMapping(value = "selectedProductList")
    public String selectedProductList(Product product,@RequestParam(value = "selectedIds",required = false)String selectedIds,
                                      HttpServletRequest request,HttpServletResponse response, Model model) {
        model.addAttribute("brandList", brandService.findList(new Brand()));
        model.addAttribute("page", topicService.findSelectProductPage(new Page(request,response), product));
        model.addAttribute("selectedIds", selectedIds);
        return "module/ec/topic/selectedProductList";
    }

    /**
     * 批量修改专题排序
     */
    @RequiresPermissions("ec:topic:edit")
    @RequestMapping(value = "updateSort")
    public String updateSort(String[] ids, Integer[] displayOrder, RedirectAttributes redirectAttributes) {
        int len = ids.length;
        Topic[] entityArray = new Topic[len];
        for (int i = 0; i < len; i++) {
            entityArray[i] = topicService.get(ids[i]);
            entityArray[i].setDisplayOrder(displayOrder[i]);
            topicService.save(entityArray[i]);
        }
        addMessage(redirectAttributes, "保存专题排序成功!");
        return "redirect:" + adminPath + "/ec/topic/list";
    }

    @ResponseBody
    @RequestMapping(value = "saveOrder")
    public void saveOrder(@RequestParam(value = "displayOrders")String[]displayOrders,@RequestParam(value = "refIds")String[]refIds){
        topicService.saveOrder(refIds, displayOrders);
    }

    @ResponseBody
    @RequestMapping(value = "cancelSelect")
    public void cancelSelect(Product product){
        topicService.cancelSelect(product.getSqlMap().get("topicId"), product.getId());
    }

    @ResponseBody
    @RequestMapping(value = "batchCancelSelect")
    public void batchCancelSelect(@RequestParam(value = "topicId")String topicId,@RequestParam(value = "selectProductId") String[] productIds){
        topicService.batchCancelSelect(topicId, productIds);
    }

    @ResponseBody
    @RequestMapping(value = "selectProduct")
    public void selectProduct(Product product){
        topicService.selectProduct(product.getSqlMap().get("topicId"), product.getId());
    }

    @ResponseBody
    @RequestMapping(value = "batchSelectProduct")
    public void batchSelectProduct(@RequestParam(value = "topicId")String topicId,@RequestParam(value = "selectProductId") String[] productIds){
        topicService.batchSelectProduct(topicId, productIds);
    }

    @ResponseBody
    @RequestMapping(value = "selectAllProduct")
    public void selectAllProduct(Product product){
        topicService.selectAllProduct(product);
    }

    @ResponseBody
    @RequestMapping(value = "checkHasProduct")
    public boolean checkHasProduct(String topicId){
        return topicService.checkHasProduct(topicId);
    }

    @ResponseBody
    @RequestMapping(value = "cancelAllSelect")
    public void cancelAllSelect(String topicId){
        topicService.cancelAllSelect(topicId);
    }

    @RequiresUser
    @ResponseBody
    @RequestMapping(value = "rebuild")
    public void rebuild() throws IllegalAccessException, InstantiationException {
        topicService.rebuild();
    }
}
