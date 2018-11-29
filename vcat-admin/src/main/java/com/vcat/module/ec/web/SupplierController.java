package com.vcat.module.ec.web;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 供货商Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/supplier")
public class SupplierController extends BaseController {

    @Autowired
    private SupplierService supplierService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private GatewayService gatewayService;
    @Autowired
    private ReconciliationDetailService reconciliationDetailService;
    @Autowired
    private ProductReconciliationService productReconciliationService;
    @Autowired
    private AccountingService accountingService;
    @Autowired
    private ReconciliationFreightService reconciliationFreightService;

    @RequiresPermissions("ec:supplier:list")
    @RequestMapping(value = "list")
    public String list(Supplier supplier, HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("page", supplierService.findPage(new Page<>(request, response), supplier));
        return "module/ec/supplier/list";
    }

    @RequiresPermissions("ec:supplier:edit")
    @RequestMapping(value = "form")
    public String form(Supplier supplier, Model model) {
        if(StringUtils.isNotEmpty(supplier.getId())){
            supplier = supplierService.get(supplier);
        }
        model.addAttribute("supplier",supplier);
        return "module/ec/supplier/form";
    }

    @RequiresPermissions("ec:supplier:edit")
    @RequestMapping(value = "save")
    public String save(Supplier supplier, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, supplier)){
            return form(supplier, model);
        }
        supplierService.save(supplier);
        addMessage(redirectAttributes, "保存供货商'" + StringUtils.abbr(supplier.getName(), 100) + "'成功");
        return "redirect:" + adminPath + "/ec/supplier/list";
    }

    @RequiresPermissions("ec:finance:supplier:statementOfAccount")
    @RequestMapping(value = "statementOfAccount")
    public String statementOfAccount(Supplier supplier,@RequestParam(value = "init" ,defaultValue = "false")boolean init, HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("supplierList", supplierService.findList(new Supplier()));
        model.addAttribute("brandList", brandService.findList(new Brand()));

        if (!init) {
            model.addAttribute("page", supplierService.statementOfAccount(new Page<>(request, response), supplier));
            model.addAttribute("totalList", supplierService.statementOfAccountTotal(supplier));
        }

        return "module/ec/supplier/statementOfAccount";
    }

    @RequiresPermissions("ec:finance:supplier:statementOfAccount")
    @RequestMapping(value = "exportStatementOfAccount", method= RequestMethod.POST)
    public void exportStatementOfAccount(Supplier supplier, HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = "供应商对账单"+ DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            Page<StatementOfAccount> page = supplierService.statementOfAccount(new Page<>(request, response, -1), supplier);
            page.getList().addAll(supplierService.statementOfAccountTotal(supplier));
            new ExportExcel("V猫小店对账单", StatementOfAccount.class).setDataList(page.getList()).write(response, fileName).dispose();
        } catch (Exception e) {
            logger.error("导出V猫小店供应商对账单失败！失败信息：" + e.getMessage());
        }
    }

    @RequiresPermissions("ec:finance:supplier:statementOfFinancial")
    @RequestMapping(value = "statementOfFinancial")
    public String statementOfFinancial(Supplier supplier,Model model) {
        model.addAttribute("supplier", supplier);
        model.addAttribute("supplierList", supplierService.findList(new Supplier()));
        model.addAttribute("brandList", brandService.findList(new Brand()));
        model.addAttribute("gatewayList", gatewayService.findList(new Gateway()));

        return "module/ec/supplier/statementOfFinancial";
    }

    @ResponseBody
    @RequiresPermissions("ec:finance:supplier:statementOfFinancial")
    @RequestMapping(value = "statementOfFinancialData")
    public DataGrid statementOfFinancialData(Supplier supplier, HttpServletRequest request, HttpServletResponse response) {
        DataGrid dataGrid = DataGrid.parse(supplierService.statementOfFinancial(new Page<>(request, response), supplier));
        dataGrid.setFooter(supplierService.statementOfFinancialTotal(supplier));
        return dataGrid;
    }

    @RequiresPermissions("ec:finance:supplier:statementOfFinancial")
    @RequestMapping(value = "exportStatementOfFinancial", method= RequestMethod.POST)
    public void exportStatementOfFinancial(Supplier supplier, HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = "财务对账单"+ DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            supplier.getSqlMap().put("mode","export");
            Page<StatementOfFinancial> page = supplierService.statementOfFinancial(new Page<>(request, response, -1), supplier);
            page.getList().addAll(supplierService.statementOfFinancialTotal(supplier));
            new ExportExcel("V猫小店对账单", StatementOfFinancial.class).setDataList(page.getList()).write(response, fileName).dispose();
        } catch (Exception e) {
            logger.error("导出V猫小店财务对账单失败！失败信息："+e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = "getSupplierList")
    public List<Supplier> getSupplierList() {
        return supplierService.findList(new Supplier());
    }

    @ResponseBody
    @RequestMapping(value = "getBrandList")
    public List<Brand> getBrandList() {
        return brandService.findList(new Brand());
    }

    @RequiresPermissions("ec:brand:list")
    @RequestMapping(value = "brandList")
    public String brandList(Brand brand, HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("supplierList", supplierService.findList(new Supplier()));
        model.addAttribute("page", brandService.findPage(new Page<>(request, response), brand));
        return "module/ec/brand/list";
    }

    @RequiresPermissions("ec:brand:edit")
    @RequestMapping(value = "brandForm")
    public String brandForm(Brand brand, Model model) {
        if(StringUtils.isNotEmpty(brand.getId())){
            brand = brandService.get(brand);
        }
        model.addAttribute("brand", brand);
        model.addAttribute("supplierList", supplierService.findList(new Supplier()));
        return "module/ec/brand/form";
    }

    @RequiresPermissions("ec:brand:edit")
    @RequestMapping(value = "saveBrand")
    public String brandSave(Brand brand, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, brand)){
            return brandForm(brand, model);
        }
        brandService.save(brand);
        addMessage(redirectAttributes, "保存品牌'" + StringUtils.abbr(brand.getName(), 100) + "'成功");
        return "redirect:" + adminPath + "/ec/supplier/brandList";
    }

    @ResponseBody
    @RequiresPermissions("ec:finance:supplier:statementOfFinancial")
    @RequestMapping(value = "reconciliationOrder")
    public void reconciliationOrder(ReconciliationDetail reconciliationDetail) {
        reconciliationDetailService.reconciliationByOrder(reconciliationDetail);
    }

    @ResponseBody
    @RequiresPermissions("ec:finance:supplier:statementOfFinancial")
    @RequestMapping(value = "reconciliationItem")
    public void reconciliationItem(ReconciliationDetail reconciliationDetail) {
        reconciliationDetailService.reconciliationByItem(reconciliationDetail);
    }

    @ResponseBody
    @RequiresPermissions("ec:finance:supplier:statementOfFinancial")
    @RequestMapping(value = "batchReconciliationOrder")
    public int batchReconciliationOrder(String[] orderIdArray) {
        return reconciliationDetailService.batchReconciliationOrder(orderIdArray);
    }

    @ResponseBody
    @RequiresPermissions("ec:finance:supplier:statementOfFinancial")
    @RequestMapping(value = "reconciliationByQueryConditions")
    public int reconciliationByQueryConditions(Supplier supplier) {
        return reconciliationDetailService.reconciliationByQueryConditions(supplier);
    }

    @ResponseBody
    @RequiresPermissions("ec:finance:supplier:statementOfFinancial")
    @RequestMapping(value = "getReconciliationDetailList")
    public List<ReconciliationDetail> getReconciliationDetailList(ReconciliationDetail reconciliationDetail) {
        return reconciliationDetailService.findList(reconciliationDetail);
    }

    @RequiresPermissions("ec:supplier:productReconciliation")
    @RequestMapping(value = "productReconciliation")
    public String productReconciliation(ProductReconciliation productReconciliation,HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("page", productReconciliationService.findPage(new Page<>(request, response), productReconciliation));
        model.addAttribute("productReconciliation",productReconciliation);
        model.addAttribute("brandList", brandService.findList(new Brand()));
        return "module/ec/supplier/productReconciliation";
    }

    @ResponseBody
    @RequiresPermissions("ec:supplier:productReconciliation")
    @RequestMapping(value = "saveProductReconciliation")
    public void saveProductReconciliation(@RequestParam(value = "itemIdArray")String[] itemIdArray,
                                          @RequestParam(value = "productIdArray")String[] productIdArray,
                                          @RequestParam(value = "surplusQuantityArray")int[] surplusQuantityArray) {
        productReconciliationService.saveList(itemIdArray, productIdArray, surplusQuantityArray);
    }

    @ResponseBody
    @RequiresPermissions("ec:supplier:productReconciliation")
    @RequestMapping(value = "addProductReconciliation")
    public void addProductReconciliation(@RequestParam(value = "itemIdArray")String[] itemIdArray,
                                          @RequestParam(value = "surplusQuantityArray")int[] surplusQuantityArray) {
        productReconciliationService.addReconciliation(itemIdArray, surplusQuantityArray);
    }

    @ResponseBody
    @RequiresPermissions("ec:finance:supplier:statementOfFinancial")
    @RequestMapping(value = "accountingOrder")
    public void accountingOrder(Accounting accounting) {
        accountingService.accountingByOrder(accounting);
    }

    @ResponseBody
    @RequiresPermissions("ec:finance:supplier:statementOfFinancial")
    @RequestMapping(value = "batchAccountingOrder")
    public int batchAccountingOrder(@RequestParam(required = false, value = "orderIdArray") String[] orderIdArray) {
        return accountingService.batchAccountingOrder(orderIdArray);
    }

    @ResponseBody
    @RequiresPermissions("ec:finance:supplier:statementOfFinancial")
    @RequestMapping(value = "accountingByQueryConditions")
    public int accountingByQueryConditions(Supplier supplier) {
        return accountingService.accountingByQueryConditions(supplier);
    }

    @ResponseBody
    @RequiresPermissions("ec:finance:supplier:statementOfFinancial")
    @RequestMapping(value = "reconciliationFreight")
    public void reconciliationFreightOrder(ReconciliationFreight reconciliationFreight) {
        reconciliationFreightService.reconciliationFreightByOrder(reconciliationFreight);
    }

    @ResponseBody
    @RequiresPermissions("ec:finance:supplier:statementOfFinancial")
    @RequestMapping(value = "batchReconciliationFreight")
    public int batchReconciliationFreightOrder(@RequestParam(required = false, value = "orderIdArray") String[] orderIdArray) {
        return reconciliationFreightService.batchReconciliationFreight(orderIdArray);
    }

    @ResponseBody
    @RequiresPermissions("ec:finance:supplier:statementOfFinancial")
    @RequestMapping(value = "reconciliationFreightByQueryConditions")
    public int reconciliationFreightByQueryConditions(Supplier supplier) {
        return reconciliationFreightService.reconciliationFreightByQueryConditions(supplier);
    }

    @ResponseBody
    @RequiresPermissions("ec:finance:supplier:statementOfFinancial")
    @RequestMapping(value = "reconciliationFreightAndOrder")
    public void reconciliationFreightAndOrder(Order order) {
        ReconciliationDetail reconciliationDetail = new ReconciliationDetail();
        ReconciliationFreight reconciliationFreight = new ReconciliationFreight();
        reconciliationDetail.setOrder(order);
        reconciliationFreight.setOrder(order);
        reconciliationDetailService.reconciliationByOrder(reconciliationDetail);
        reconciliationFreightService.reconciliationFreightByOrder(reconciliationFreight);
    }
}
