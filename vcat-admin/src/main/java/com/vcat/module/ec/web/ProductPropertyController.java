package com.vcat.module.ec.web;

import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.ec.entity.ProductProperty;
import com.vcat.module.ec.service.ProductPropertyService;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 属性Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/property")
public class ProductPropertyController extends BaseController {
    @Autowired
   	private ProductPropertyService productPropertyService;

	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "save")
	public ProductProperty save(ProductProperty productProperty) {
		productPropertyService.save(productProperty);
        return productProperty;
	}

	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "listData")
	public List<ProductProperty> treeData(@RequestParam(required=false,value = "categoryId")String categoryId,HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		if(StringUtils.isNotEmpty(categoryId)){
			return productPropertyService.findCategoryProperty(categoryId);
		}
		List<ProductProperty> productPropertyList = productPropertyService.findAll();
		return productPropertyList;
	}
}
