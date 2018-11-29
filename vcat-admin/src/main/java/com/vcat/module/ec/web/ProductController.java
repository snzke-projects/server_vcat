package com.vcat.module.ec.web;

import com.vcat.common.easyui.DataGrid;
import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.config.CacheConfig;
import com.vcat.module.ec.entity.*;
import com.vcat.module.ec.service.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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

/**
 * 商品Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/product")
public class ProductController extends BaseController {
    private final String listProduct = "listProduct";

    @Autowired
   	private ProductService productService;
	@Autowired
	private ProductImageService productImageService;
	@Autowired
	private ProductItemService productItemService;
	@Autowired
	private ShareEarningService shareEarningService;
	@Autowired
	private ProductForecastService productForecastService;
	@Autowired
	private SpecService specService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private DistributionService distributionService;
    @Autowired
    private ExpressTemplateService expressTemplateService;
    @Autowired
    private DataChangeLogService dataChangeLogService;
    @Autowired
    private ProductLimitService productLimitService;
    @Autowired
    private RecommendService recommendService;

	@RequiresPermissions("ec:product:list")
	@RequestMapping(value = "list")
	public String list(Product product,@RequestParam(value = "type",required = false)String type,
                       @RequestParam(value = "mode",required = false)String mode, HttpServletRequest request,
                       HttpServletResponse response, Model model) {
        if(StringUtils.isEmpty(type)){
        	type = "all";
        }
        if(StringUtils.isEmpty(mode)){
            mode = "NORMAL";
        }
        product.getSqlMap().put("mode",mode);
        if(!"all".equals(type)){
        	product.getSqlMap().put("type", type);
        }
        if("1".equals(request.getParameter("useHisOrderParam"))){
            product = (Product)request.getSession().getAttribute(listProduct);
        }
        model.addAttribute("page", productService.findPage(new Page<>(request, response), product));
        model.addAttribute("brandList",brandService.findList(new Brand()));
        model.addAttribute("supplierList",supplierService.findList(new Supplier()));
        model.addAttribute("type",type);
        model.addAttribute("mode",mode);
        model.addAttribute("product",product);
        request.getSession().setAttribute(listProduct, product);
        if("NORMAL".equals(mode)){
            return "module/ec/product/list";
        }else{
            return "module/ec/product/recycleBin";
        }
	}

    @RequestMapping(value = "listData")
    @ResponseBody
    public DataGrid listData(Product product, HttpServletRequest request,
                             HttpServletResponse response){
        return DataGrid.parse(productService.findPage(new Page<>(request, response), product));
    }

	@RequestMapping(value = "form")
	public String form(Product product,@RequestParam(defaultValue = "false",value = "isView",required = false)boolean isView, Model model) {
		if(StringUtils.isNotEmpty(product.getId())){
			product = productService.get(product);
			product.setImageList(productImageService.findListByProductId(product.getId()));
			product.setItemList(productItemService.findListByProductId(product));
		}
		model.addAttribute("product",product);
        model.addAttribute("isView",isView);
		model.addAttribute("brandList", brandService.findList(new Brand()));
        model.addAttribute("distributionList", distributionService.findList(new Distribution()));
        model.addAttribute("templateList", expressTemplateService.findList(new ExpressTemplate()));
        model.addAttribute("defaultTemplate", expressTemplateService.getDefault());
		return "module/ec/product/form";
	}

	@RequestMapping(value = "save")
    @CacheEvict(value = {CacheConfig.FIND_LIST_BY_PRODUCT_CACHE
            ,CacheConfig.FIND_LIST_BY_PRODUCT_ID_CACHE
            ,CacheConfig.GET_CATEGORY_PRODUCT_LIST_CACHE
            ,CacheConfig.GET_LEROY_PRODUCT_LIST_CACHE
            ,CacheConfig.GET_NEW_PRODUCT_LIST_CACHE
            ,CacheConfig.GET_RESERVE_PRODUCT_LIST_CACHE
            ,CacheConfig.GET_SELLER_PRODUCT_DETAIL_CACHE
            ,CacheConfig.GET_VCAT_PRODUCT_DETAIL_CACHE
            ,CacheConfig.GET_VCAT_SHOP_PRODUCT_LIST_CACHE}, cacheManager = "apiCM", allEntries = true)
	public String save(Product product,@RequestParam(value = "oldArchived") String oldArchived, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, product)){
			return form(product,false, model);
		}
		product.getSqlMap().put("oldArchived", oldArchived);
		productService.save(product);
		addMessage(redirectAttributes, "保存商品'" + StringUtils.abbr(product.getName(), 100) + "'成功");
        Product listP = (Product)request.getSession().getAttribute(listProduct);

		return "redirect:" + adminPath + "/ec/product/list?useHisOrderParam=1&pageNo=" + listP.getPage().getPageNo() + "&pageSize=" + listP.getPage().getPageSize();
	}

    @ResponseBody
	@RequiresPermissions("ec:product:edit")
	@RequestMapping(value = "archived")
	public void archived(Product product) {
		productService.archived(product.getId(),product.getArchived());
	}

    @ResponseBody
    @RequiresPermissions("ec:product:edit")
    @RequestMapping(value = "batchArchived")
    public void batchArchived(@RequestParam(value = "archivedProductId")String[]productIds,@RequestParam(value = "archived")Integer archived) {
        productService.batchArchived(productIds, archived);
    }

    @ResponseBody
    @RequiresPermissions("ec:product:edit")
    @RequestMapping(value = "saveOrder")
    public void saveOrder(@RequestParam(value = "productIds")String[]productIds,@RequestParam(value = "displayOrders")String[]displayOrders){
        productService.saveOrder(productIds, displayOrders);
    }

    @RequiresPermissions("ec:product:edit")
    @RequestMapping(value = "delete")
    public String delete(Product product, RedirectAttributes redirectAttributes){
        productService.delete(product);
        addMessage(redirectAttributes, "删除商品'" + StringUtils.abbr(product.getName(), 100) + "'成功");
        return "redirect:" + adminPath + "/ec/product/list";
    }

    @RequiresPermissions("ec:product:recover")
    @RequestMapping(value = "recover")
    public String recover(Product product, RedirectAttributes redirectAttributes){
        productService.recover(product);
        addMessage(redirectAttributes, "恢复商品'" + StringUtils.abbr(product.getName(), 100) + "'成功");
        return "redirect:" + adminPath + "/ec/product/list?mode=recycleBin";
    }



	@RequiresPermissions("ec:product:edit")
	@RequestMapping(value = "deleteProductItem")
	public String deleteProductItem(ProductItem productItem,@RequestParam(value = "productId") String productId, RedirectAttributes redirectAttributes) {
		productItemService.delete(productItem);
		addMessage(redirectAttributes, "删除成功");
		return "redirect:" + adminPath + "/ec/product/form?id="+productId;
	}

    @ResponseBody
    @RequiresPermissions("ec:product:edit")
    @RequestMapping(value = "getProductItemList")
    public List<ProductItem> getProductItemList(Product product) {
        return productItemService.findListByProductId(product);
    }

    @ResponseBody
    @RequestMapping(value = "getProductItemListByIds")
    public List<ProductItem> getProductItemListByIds(String ids) {
        return productItemService.findListByIds(ids.split("\\|"));
    }

    @ResponseBody
    @RequestMapping(value = "getProductItem")
    public ProductItem getProductItem(ProductItem productItem) {
        return productItemService.get(productItem);
    }

    @ResponseBody
    @RequiresPermissions("ec:product:edit")
    @RequestMapping(value = "saveProductItem")
    public void saveProductItem(Product product){
        if(null == product || null == product.getItemList() || product.getItemList().size() == 0){
            return;
        }
        for (int i = 0; i < product.getItemList().size(); i++) {
            productItemService.save(product.getItemList().get(i),"商品列表");
        }
    }

	@RequiresPermissions("ec:product:edit")
	@RequestMapping(value = "shareList")
	public String shareList(ShareEarning shareEarning, HttpServletRequest request, HttpServletResponse response,Model model) {
		model.addAttribute("share", shareEarning);
		model.addAttribute("page", shareEarningService.findPage(new Page<>(request, response), shareEarning));
		return "module/ec/product/shareList";
	}

	@RequiresPermissions("ec:product:edit")
	@RequestMapping(value = "shareForm")
	public String shareForm(ShareEarning shareEarning,Model model) {
		model.addAttribute("productList", productService.findList(new Product()));
		if(StringUtils.isNotEmpty(shareEarning.getId())){
			shareEarning = shareEarningService.get(shareEarning);
		}
		model.addAttribute("share", shareEarning);
		return "module/ec/product/shareForm";
	}

    @RequestMapping(value = "shareLog")
    public String shareLog(ShareEarning shareEarning, HttpServletRequest request, HttpServletResponse response,Model model) {
        model.addAttribute("shareEarning", shareEarningService.get(shareEarning));
        model.addAttribute("page", shareEarningService.findLogPage(new Page<>(request, response), shareEarning));
        model.addAttribute("chartList", shareEarningService.getPieChartList(shareEarning));
        return "module/ec/product/shareLog";
    }

    @ResponseBody
    @RequestMapping(value = "sharePieChart")
    public List<Map<String, Object>> sharePieChart(ShareEarning shareEarning){
        return shareEarningService.getPieChartList(shareEarning);
    }

	@RequestMapping(value = "saveShare")
	public String saveShare(ShareEarning shareEarning, RedirectAttributes redirectAttributes) {
		shareEarningService.save(shareEarning);
		addMessage(redirectAttributes, "保存分享活动'" + StringUtils.abbr(shareEarning.getTitle(), 100) + "'成功");
		return "redirect:" + adminPath + "/ec/product/shareList";
	}

	@RequestMapping(value = "deleteShare")
	public String deleteShare(ShareEarning shareEarning, RedirectAttributes redirectAttributes) {
		shareEarningService.delete(shareEarning);
		addMessage(redirectAttributes, "删除活动成功");
		return "redirect:" + adminPath + "/ec/product/shareList";
	}

	@RequestMapping(value = "activateShare")
	public String activateShare(ShareEarning shareEarning, RedirectAttributes redirectAttributes) throws Exception {
		Integer i = shareEarningService.activate(shareEarning);
		String msg = i > 0 ? "激活成功" : "激活失败，该分享已被激活或已被删除";
		addMessage(redirectAttributes, msg);
		return "redirect:" + adminPath + "/ec/product/shareList";
	}

	@RequestMapping(value = "forecastList")
	public String forecastList(ProductForecast productForecast, HttpServletRequest request, HttpServletResponse response,Model model) {
		model.addAttribute("productForecast", productForecast);
		model.addAttribute("page", productForecastService.findPage(new Page<>(request, response), productForecast));
		return "module/ec/product/forecastList";
	}

	@RequestMapping(value = "forecastForm")
	public String forecastForm(ProductForecast productForecast,Model model) {
		if(StringUtils.isNotEmpty(productForecast.getId())){
			productForecast = productForecastService.get(productForecast);
		}
		model.addAttribute("productForecast", productForecast);
		return "module/ec/product/forecastForm";
	}

	@RequestMapping(value = "saveForecast")
	public String saveForecast(ProductForecast productForecast, RedirectAttributes redirectAttributes) {
		productForecastService.save(productForecast);
		addMessage(redirectAttributes, "保存上架预告'" + StringUtils.abbr(productForecast.getTitle(), 100) + "'成功");
		return "redirect:" + adminPath + "/ec/product/forecastList";
	}

	@RequestMapping(value = "deleteForecast")
	public String deleteForecast(ProductForecast productForecast, RedirectAttributes redirectAttributes) {
		productForecastService.delete(productForecast);
		addMessage(redirectAttributes, "删除活动成功");
		return "redirect:" + adminPath + "/ec/product/forecastList";
	}

	@RequestMapping(value = "activateForecast")
	public String activateForecast(ProductForecast productForecast, RedirectAttributes redirectAttributes) throws Exception {
		Integer i = productForecastService.activate(productForecast);
		String msg = i > 0 ? "激活成功" : "激活失败，该上架预告已被激活或已被删除";
		addMessage(redirectAttributes, msg);
		return "redirect:" + adminPath + "/ec/product/forecastList";
	}

	@RequestMapping(value = "select")
	public String select(Product product,String type,
                         @RequestParam(value = "radio",required = false)boolean radio,
                         @RequestParam(value = "selectedIdsKey",required = false)String selectedIdsKey,
                         @RequestParam(value = "selectedIds",required = false)String selectedIds,Model model) {
        model.addAttribute("radio", radio);
        model.addAttribute("selectedIdsKey", selectedIdsKey);
        model.addAttribute("selectedIds", selectedIds);
		return "module/ec/product/select";
	}

	@ResponseBody
	@RequestMapping(value = "findListByIds")
	public List<Product> findListByIds(String ids) {
		return productService.findListByIds(ids);
	}

	@RequestMapping(value = "spec")
	public String spec(Spec spec,HttpServletRequest request, HttpServletResponse response,Model model) {
		model.addAttribute("page", specService.findPage(new Page<>(request, response), spec));
		return "module/ec/product/spec";
	}

	@ResponseBody
	@RequestMapping(value = "saveSpec")
	public void saveSpec(Spec spec) {
		specService.save(spec);
	}

	@ResponseBody
	@RequestMapping(value = "deleteSpec")
	public void deleteSpec(Spec spec) {
		specService.delete(spec);
	}

	@ResponseBody
	@RequestMapping(value = "specName")
	public List<Spec> specName(Spec spec) {
		return specService.findList(spec);
	}


    @ResponseBody
    @RequestMapping(value = "addSpecValue")
    public void addSpecValue(Spec spec) {
        specService.addSpecValue(spec);
    }

    @ResponseBody
    @RequestMapping(value = "stick")
    public void stick(Product product){
        productService.stick(product);
    }

    @RequestMapping(value = "selectItem")
    public String selectItem(ProductItem productItem,
                             @RequestParam(value = "radio",required = false)boolean radio,
                             @RequestParam(value = "selectedItemIdsName",required = false)String selectedItemIdsName,
                             @RequestParam(value = "selectedIds",required = false)String selectedIds,
                             HttpServletRequest request, HttpServletResponse response,Model model) {
        model.addAttribute("page", productItemService.findPage(new Page<>(request, response), productItem));
        model.addAttribute("brandList", brandService.findList(new Brand()));
        model.addAttribute("radio", radio);
        model.addAttribute("selectedItemIdsName", selectedItemIdsName);
        model.addAttribute("selectedIds",selectedIds);
        return "module/ec/product/selectItem";
    }

    @RequestMapping(value = "findItemChangeLog")
    public String findItemChangeLog(DataChangeLog dataChangeLog,HttpServletRequest request, HttpServletResponse response,Model model){
        dataChangeLog.setTableName("ec_product_item");
        model.addAttribute("page",dataChangeLogService.findPage(new Page(request,response),dataChangeLog));
        return "module/ec/product/itemChangeLog";
    }

    @RequiresPermissions("ec:product:limit:list")
    @RequestMapping(value = "limitList")
    public String limitList(ProductLimit productLimit,HttpServletRequest request, HttpServletResponse response,Model model) {
        model.addAttribute("page", productLimitService.findPage(new Page<>(request, response), productLimit));
        return "module/ec/product/limitList";
    }

    @RequestMapping(value = "limitForm")
    public String limitForm(ProductLimit productLimit,Model model) {
        if(StringUtils.isNotEmpty(productLimit.getId())){
            productLimit = productLimitService.get(productLimit);
        }
        model.addAttribute("productLimit", productLimit);
        return "module/ec/product/limitForm";
    }

    @RequiresPermissions("ec:product:limit:edit")
    @RequestMapping(value = "saveLimit")
    public String saveLimit(ProductLimit productLimit, RedirectAttributes redirectAttributes) {
        productLimitService.save(productLimit);
        addMessage(redirectAttributes, "保存成功");
        return "redirect:" + adminPath + "/ec/product/limitList";
    }

    @ResponseBody
    @RequiresPermissions("ec:product:limit:edit")
    @RequestMapping(value = "deleteLimit")
    public void deleteLimit(ProductLimit productLimit) {
        productLimitService.delete(productLimit);
    }

    @RequestMapping(value = "reserveList")
    public String reserveList(RecommendEntity reserve,Model model) {
        model.addAttribute("reserve", reserve);
        return "module/ec/product/reserveList";
    }

    @ResponseBody
    @RequestMapping(value = "reserveData")
    public DataGrid reserveData(RecommendEntity reserve,HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(recommendService.findReservePage(new Page(request, response), reserve));
    }

    @RequestMapping(value = "reserveForm")
    public String reserveForm(RecommendEntity reserve,Model model) {
        if(StringUtils.isNotEmpty(reserve.getId())){
            reserve = recommendService.getReserve(reserve);
        }
        model.addAttribute("reserve", reserve);
        return "module/ec/product/reserveForm";
    }

    @RequestMapping(value = "saveReserve")
    public String saveReserve(RecommendEntity reserve) {
        reserve.setTypeCode(ReserveRecommend.RECOMMEND_RESERVE);
        recommendService.save(reserve);
        return "redirect:" + adminPath + "/ec/product/reserveList";
    }

    @ResponseBody
    @RequestMapping(value = "getExistsReserve")
    public List<Product> getExistsReserve(String productIds) {
        return recommendService.getExistsReserve(productIds);
    }

    @ResponseBody
    @RequestMapping(value = "getExistsHot")
    public List<Product> getExistsHot(String productIds) {
        return recommendService.getExistsHot(productIds);
    }
}
