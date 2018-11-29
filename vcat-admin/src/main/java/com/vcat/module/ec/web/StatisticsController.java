package com.vcat.module.ec.web;

import com.vcat.common.chart.Histogram;
import com.vcat.common.easyui.DataGrid;
import com.vcat.common.persistence.Page;
import com.vcat.common.utils.DateUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.core.utils.excel.ExportExcel;
import com.vcat.module.ec.entity.*;
import com.vcat.module.ec.service.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;

/**
 * 客户Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/statistics")
public class StatisticsController extends BaseController {
	@Autowired
	private StatisticsService statisticsService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private DistributionService distributionService;

	@RequiresPermissions("ec:statistics:overview")
	@RequestMapping(value = "overview")
	public String overview(String st,String et,Model model) {
        st = initST(st);
        if(StringUtils.isEmpty(et)){
            Calendar calendar = Calendar.getInstance();
            et = DateUtils.formatDateTime(calendar.getTime());
        }
		model.addAllAttributes(statisticsService.overview(st, et));
        model.addAttribute("st",st);
        model.addAttribute("et",et);
		return "module/ec/statistics/overview";
	}

    @RequiresPermissions("ec:statistics:productStatistics")
    @RequestMapping(value = "productStatistics")
    public String productStatistics(ProductStatistics productStatistics, Model model) {
        model.addAttribute("supplierList",supplierService.findList(new Supplier()));
        model.addAttribute("brandList",brandService.findList(new Brand()));
        model.addAttribute("productStatistics", productStatistics);
        return "module/ec/statistics/productStatistics";
    }

    @ResponseBody
    @RequiresPermissions("ec:statistics:productStatistics")
    @RequestMapping(value = "productStatisticsData")
    public DataGrid productStatisticsData(ProductStatistics productStatistics, HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(statisticsService.productStatistics(new Page(request,response),productStatistics));
    }

    @RequiresPermissions("ec:statistics:productStatistics")
    @RequestMapping(value = "exportProductStatistics")
    public void exportProductStatistics(ProductStatistics productStatistics, HttpServletRequest request, HttpServletResponse response) {
        String fileName = "商品销量统计表"+ DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
        Page<ProductStatistics> page = statisticsService.productStatistics(new Page(request, response),productStatistics);
        try {
            new ExportExcel("商品销量统计表", ProductStatistics.class).setDataList(page.getList()).write(response, fileName).dispose();
        } catch (IOException e) {
            logger.error("导出" + fileName + "失败：" + e.getMessage());
        }
    }

    @RequestMapping(value = "productOrder")
    public String productOrder(Order order, Model model) {
        if(StringUtils.isEmpty(order.getSqlMap().get("type"))){
            order.getSqlMap().put("type","all");
        }
        model.addAttribute("distributionList",distributionService.findList(new Distribution()));
        model.addAttribute("supplierList",supplierService.findList(new Supplier()));
        model.addAttribute("order",order);
        return "module/ec/statistics/productOrder";
    }

    @ResponseBody
    @RequestMapping(value = "productOrderData")
    public DataGrid productOrderData(Order order, HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isEmpty(order.getSqlMap().get("type"))){
            order.getSqlMap().put("type","all");
        }
        return DataGrid.parse(orderService.findPage(new Page<>(request, response), order));
    }

    @RequestMapping(value = "buyerList")
    public String buyerList(ProductBuyer productBuyer, Model model) {
        model.addAttribute("productBuyer",productBuyer);
        return "module/ec/statistics/buyerList";
    }

    @ResponseBody
    @RequestMapping(value = "buyerListData")
    public DataGrid buyerListData(ProductBuyer productBuyer, HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(statisticsService.buyerPage(new Page<>(request, response), productBuyer));
    }

    @RequestMapping(value = "exportBuyerList")
    public void exportBuyerList(ProductBuyer productBuyer, HttpServletRequest request, HttpServletResponse response) {
        String fileName = "商品购买用户统计表_"+ DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
        Page<ProductBuyer> page = statisticsService.buyerPage(new Page<>(request, response, -1),productBuyer);
        try {
            new ExportExcel("商品购买用户统计表", ProductBuyer.class).setDataList(page.getList()).write(response, fileName).dispose();
        } catch (IOException e) {
            logger.error("导出" + fileName + "失败：" + e.getMessage());
        }
    }

    @RequestMapping(value = "sellerList")
    public String sellerList(ProductSeller productSeller, Model model) {
        model.addAttribute("productSeller",productSeller);
        return "module/ec/statistics/sellerList";
    }

    @ResponseBody
    @RequestMapping(value = "sellerListData")
    public DataGrid sellerListData(ProductSeller productSeller, HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(statisticsService.sellerPage(new Page<>(request, response), productSeller));
    }

    @RequestMapping(value = "exportSellerList")
    public void exportSellerList(ProductSeller productSeller, HttpServletRequest request, HttpServletResponse response) {
        String fileName = "商品代理用户统计表_"+ DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
        Page<ProductSeller> page = statisticsService.sellerPage(new Page<>(request, response, -1),productSeller);
        try {
            new ExportExcel("商品代理用户统计表", ProductSeller.class).setDataList(page.getList()).write(response, fileName).dispose();
        } catch (IOException e) {
            logger.error("导出" + fileName + "失败：" + e.getMessage());
        }
    }

    @RequiresPermissions("ec:statistics:shopStatistics")
    @RequestMapping(value = "shopStatistics")
    public String shopStatistics(ShopStatistics shopStatistics, Model model) {
        model.addAttribute("shopStatistics",shopStatistics);
        return "module/ec/statistics/shopStatistics";
    }

    @ResponseBody
    @RequiresPermissions("ec:statistics:shopStatistics")
    @RequestMapping(value = "shopStatisticsData")
    public DataGrid shopStatisticsData(ShopStatistics shopStatistics, HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(statisticsService.shopStatistics(new Page(request,response),shopStatistics));
    }

    @RequiresPermissions("ec:statistics:shopStatistics")
    @RequestMapping(value = "exportShopStatistics")
    public void exportShopStatistics(ShopStatistics shopStatistics, HttpServletRequest request, HttpServletResponse response) {
        String fileName = "店铺销量统计表"+ DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
        Page<ShopStatistics> page = statisticsService.shopStatistics(new Page(request, response, -1),shopStatistics);
        try {
            new ExportExcel("店铺销量统计表", ShopStatistics.class).setDataList(page.getList()).write(response, fileName).dispose();
        } catch (IOException e) {
            logger.error("导出" + fileName + "失败：" + e.getMessage());
        }
    }

    @RequestMapping(value = "shopOrder")
    public String shopOrder(Order order, HttpServletRequest request, HttpServletResponse response, Model model) {
        if(StringUtils.isEmpty(order.getSqlMap().get("type"))){
            order.getSqlMap().put("type","all");
        }
        model.addAttribute("order",order);
        model.addAttribute("page", orderService.findPage(new Page<>(request, response), order));
        return "module/ec/statistics/shopOrder";
    }

    @RequestMapping(value = "kpi")
    public String kpi(Supplier supplier) {
        return "module/ec/statistics/kpi";
    }

    @ResponseBody
    @RequestMapping(value = "kpiData")
    public Histogram kpiData(Supplier supplier){
        return statisticsService.kpiToColumn(supplier);
    }

    private String initST(String st){
        if(StringUtils.isEmpty(st)){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
            st = DateUtils.formatDateTime(calendar.getTime());
        }
        return st;
    }
}

