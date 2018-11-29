package com.vcat.module.ec.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.ec.entity.ProductCategory;
import com.vcat.module.ec.service.ProductCategoryService;
import com.vcat.module.ec.service.ProductPropertyService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 商品分类Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/category")
public class ProductCategoryController extends BaseController {
    @Autowired
   	private ProductCategoryService productCategoryService;
	@Autowired
	private ProductPropertyService productPropertyService;
	
	@RequiresPermissions("ec:category:list")
	@RequestMapping(value = "list")
	public String list(ProductCategory productCategory, Model model) {
		List<ProductCategory> list = Lists.newArrayList();
		List<ProductCategory> sourcelist = productCategoryService.findList(productCategory);
		ProductCategory.sortList(list, sourcelist, "0");
        model.addAttribute("list", list);
		return "module/ec/category/list";	
	}

	@RequestMapping(value = "form")
	public String form(ProductCategory productCategory, Model model) {
		if(StringUtils.isNotEmpty(productCategory.getId())){
			productCategory = productCategoryService.get(productCategory);
			productCategory.setParent(productCategoryService.get(productCategory.getParent().getId()));
			productCategory.setPropertyList(productPropertyService.findCategoryProperty(productCategory.getId()));
		}

        // 获取排序号，最末节点排序号+10
        if (StringUtils.isEmpty(productCategory.getId()) && null != productCategory.getParent()){
            productCategory.setDisplayOrder(productCategoryService.getHighestOrder(productCategory.getParent().getId()) + 10);
        }
		model.addAttribute("category",productCategory);
		return "module/ec/category/form";
	}

	@RequiresPermissions("ec:category:edit")
	@RequestMapping(value = "save")
	public String save(ProductCategory productCategory, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, productCategory)){
			return form(productCategory, model);
		}
		productCategoryService.save(productCategory);
		addMessage(redirectAttributes, "保存分类'" + StringUtils.abbr(productCategory.getName(), 100) + "'成功");
		return "redirect:" + adminPath + "/ec/category/list";
	}
	
	@RequiresPermissions("ec:category:delete")
	@RequestMapping(value = "delete")
	public String delete(ProductCategory productCategory, RedirectAttributes redirectAttributes) {
		productCategoryService.delete(productCategory);
		addMessage(redirectAttributes, "删除成功");
		return "redirect:" + adminPath + "/ec/category/list";
	}

    @RequiresPermissions("ec:category:edit")
    @RequestMapping(value = "activate")
    public String activate(ProductCategory productCategory, RedirectAttributes redirectAttributes) {
        productCategoryService.activate(productCategory);
        addMessage(redirectAttributes, (productCategory.getIsActivate() == 0 ? "取消" : "") + "激活分类'" + StringUtils.abbr(productCategory.getName(), 100) + "'成功");
        return "redirect:" + adminPath + "/ec/category/list";
    }
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<ProductCategory> list = productCategoryService.findList(new ProductCategory());
		for (int i=0; i<list.size(); i++){
			ProductCategory e = list.get(i);
			if (StringUtils.isEmpty(extId) || (StringUtils.isNotEmpty(extId) && !extId.equals(e.getId()))){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParent()!=null?e.getParent().getId():0);
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}

    @ResponseBody
    @RequiresUser
    @RequestMapping(value = "rebuild")
    public void rebuild() throws IllegalAccessException, InstantiationException {
        productCategoryService.rebuild();
    }

    /**
     * 批量修改栏目排序
     */
    @RequiresPermissions("ec:category:edit")
    @RequestMapping(value = "updateSort")
    public String updateSort(String[] ids, Integer[] displayOrder, RedirectAttributes redirectAttributes) {
        int len = ids.length;
        ProductCategory[] entitys = new ProductCategory[len];
        for (int i = 0; i < len; i++) {
            entitys[i] = productCategoryService.get(ids[i]);
            entitys[i].setDisplayOrder(displayOrder[i]);
            productCategoryService.save(entitys[i]);
        }
        addMessage(redirectAttributes, "保存栏目排序成功!");
        return "redirect:" + adminPath + "/ec/category/list";
    }

    @ResponseBody
    @RequestMapping(value = "getCategory")
    public ProductCategory getCategory(String id){
        return productCategoryService.get(id);
    }
}
