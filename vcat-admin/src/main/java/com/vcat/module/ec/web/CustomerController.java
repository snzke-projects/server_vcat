package com.vcat.module.ec.web;

import com.vcat.common.easyui.DataGrid;
import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.config.CacheConfig;
import com.vcat.module.core.utils.excel.ExportExcel;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * 客户Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/customer")
public class CustomerController extends BaseController {
	@Autowired
	private CustomerService customerService;
	@Autowired
	private WithdrawalService withdrawalService;
	@Autowired
	private RefundService refundService;
	@Autowired
	private LevelService levelService;
	@Autowired
	private BgImageService bgImageService;
	@Autowired
	private EarningSpecService earningSpecService;
	@Autowired
	private HeadImageService headImageService;
	@Autowired
	private SupplierService supplierService;
    @Autowired
    private MessageRegisterService messageRegisterService;
    @Autowired
    private FeaturedPageService featuredPageService;
    @Autowired
    private UpgradeRequestService upgradeRequestService;
    @Autowired
    private InvitationCodeService invitationCodeService;

	@RequiresPermissions("ec:customer:sellerList")
	@RequestMapping(value = "sellerList")
	public String sellerList(Customer customer,@RequestParam(value = "type",required = false)String type,
                             @RequestParam(value = "init",required = false)boolean init,
                             HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isEmpty(type)){
			type = "sellerList";
		}
        return list(customer,type,init,request,response,model);
	}

    @RequiresPermissions("ec:customer:buyerList")
    @RequestMapping(value = "buyerList")
    public String buyerList(Customer customer,@RequestParam(value = "type",required = false)String type,
                            @RequestParam(value = "init",required = false)boolean init,
                            HttpServletRequest request, HttpServletResponse response, Model model) {
        if(StringUtils.isEmpty(type)){
            type = "buyerList";
        }
        return list(customer,type,init,request,response,model);
    }

    private String list(Customer customer,String type, boolean init, HttpServletRequest request, HttpServletResponse response, Model model){
        customer.getSqlMap().put("type",type);
        if(!init){
            model.addAttribute("page", customerService.findPage(new Page<>(request, response), customer));
        }
        model.addAttribute("type", type);
        model.addAttribute("customer",customer);
        return "module/ec/customer/list";
    }

    @ResponseBody
    @RequestMapping(value = "listData")
    public DataGrid listData(Customer customer, HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(customerService.findPage(new Page<>(request, response), customer));
    }

    @RequestMapping(value = "select")
    public String select(Customer customer,String idsKey, boolean radio, boolean showClearButton, String selectedIds, Model model) {
        model.addAttribute("customer", customer);
        model.addAttribute("idsKey", idsKey);
        model.addAttribute("radio", radio);
        model.addAttribute("showClearButton", showClearButton);
        model.addAttribute("selectedIds", selectedIds);
        return "module/ec/customer/select";
    }

    @ResponseBody
    @RequestMapping(value = "findListByIds")
    public List<Customer> findListByIds(String ids) {
        return customerService.findListByIds(ids);
    }


    @RequestMapping(value = "exportCustomerList")
    public String exportCustomerList(Customer customer,@RequestParam(value = "type",required = false)String type, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            customer.getSqlMap().put("type",type);
            Page<Customer> page = customerService.findPage(new Page<>(request, response, -1), customer);

            String fileName = "会员列表.xlsx";
            new ExportExcel("会员列表", Customer.class).setDataList(page.getList()).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导出失败！失败信息："+e.getMessage());
        }
        return "redirect:" + adminPath + "/ec/customer/sellerList?type="+type;
    }

	@ResponseBody
	@RequestMapping(value = "get")
	public Customer get(Customer customer) {
		return customerService.get(customer);
	}

	@RequestMapping(value = "withdrawalLog")
	public String withdrawalLog(Withdrawal withdrawal, HttpServletRequest request, HttpServletResponse response,Model model) {
		withdrawal.getSqlMap().put("type", "historyWithdrawalList");
		model.addAttribute("withdrawal", withdrawal);
		model.addAttribute("page",withdrawalService.findPage(new Page<>(request, response), withdrawal));
		return "module/ec/withdrawal/log";
	}

    @RequestMapping(value = "team")
    public String team(Shop shop, HttpServletRequest request, HttpServletResponse response,Model model) {
        model.addAttribute("shop", shop);
        model.addAttribute("page",customerService.findTeamPage(new Page<>(request, response), shop));
        return "module/ec/customer/team";
    }

	@RequestMapping(value = "refundLog")
	public String refundLog(Refund refund, HttpServletRequest request, HttpServletResponse response,Model model) {
		refund.getSqlMap().put("type", "historyRefundList");
		model.addAttribute("refund", refund);
		model.addAttribute("supplierList", supplierService.findList(new Supplier()));
		model.addAttribute("page", refundService.findPage(new Page<>(request, response), refund));
		return "module/ec/refund/log";
	}

    @ResponseBody
    @RequestMapping(value = "setRecommend")
    public void setRecommend(Shop shop) {
        customerService.setRecommend(shop);
    }

    @ResponseBody
    @RequestMapping(value = "updateRecommend")
    public void updateRecommend(Shop shop) {
        customerService.updateRecommend(shop);
    }

    @ResponseBody
    @RequestMapping(value = "saveRecommendOrder")
    public void saveRecommendOrder(@RequestParam(value = "shopId")String[] shopIdArray,
                                   @RequestParam(value = "recommendOrder")String[] recommendOrderArray) {
        customerService.saveRecommendOrder(shopIdArray,recommendOrderArray);
    }

	@RequiresPermissions("ec:customer:levelList")
	@RequestMapping(value = "levelList")
	public String levelList(Level level, HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("page", levelService.findPage(new Page<>(request, response), level));
		model.addAttribute("level", level);
		return "module/ec/customer/levelList";
	}

	@RequiresPermissions("ec:customer:editLevel")
	@ResponseBody
	@RequestMapping(value = "updateLevel")
	public void updateLevel(Level level) {
		levelService.save(level);
	}

    @RequiresPermissions("ec:customer:deleteLevel")
    @RequestMapping(value = "deleteLevel")
    public String deleteLevel(Level level, RedirectAttributes redirectAttributes) {
        levelService.delete(level);
        addMessage(redirectAttributes, "等级" + level.getName() + "删除成功");
        return "redirect:" + adminPath + "/ec/customer/levelList";
    }


	@RequiresPermissions("ec:customer:pushAppMsg")
	@RequestMapping(value = "pushAppMsgForm")
	public String pushAppMsgForm(Model model) {
		model.addAttribute("levelList", levelService.findList(new Level()));
		return "module/ec/customer/pushAppMsg";
	}

	@RequiresPermissions("ec:customer:pushAppMsg")
	@RequestMapping(value = "pushAppMsg")
	public String pushAppMsg(String levels,String sendTime,String articleId, RedirectAttributes redirectAttributes) {
		addMessage(redirectAttributes, "消息发送成功，共发送" + customerService.pushAppMessage(levels, sendTime, articleId) + "条消息");
		return "redirect:" + adminPath + "/ec/customer/pushAppMsgForm";
	}

	@RequiresPermissions("ec:customer:handlerBackground")
	@RequestMapping(value = "toHandlerBackground")
	public String pushAppMsg(Model model) {
		model.addAttribute("imageList", bgImageService.findList(new BgImage()));
		return "module/ec/customer/handlerBackground";
	}

	@ResponseBody
	@RequiresPermissions("ec:customer:handlerBackground")
	@RequestMapping(value = "handlerBackground")
	public void handlerBackground(BgImage bgImage) {
		bgImageService.save(bgImage);
	}

	@ResponseBody
	@RequiresPermissions("ec:customer:handlerBackground")
	@RequestMapping(value = "deleteBackground")
	public void deleteBackground(BgImage bgImage) {
		bgImageService.delete(bgImage);
	}

    @ResponseBody
    @RequiresPermissions("ec:customer:handlerBackground")
    @RequestMapping(value = "activateBackground")
    public void activateBackground(BgImage bgImage) {
        bgImageService.activate(bgImage);
    }

	@RequestMapping(value = "specList")
	public String specList(EarningSpec earningSpec, HttpServletRequest request, HttpServletResponse response,Model model) {
		model.addAttribute("spec", earningSpec);
		model.addAttribute("page", earningSpecService.findPage(new Page<>(request, response), earningSpec));
		return "module/ec/customer/specList";
	}

	@RequiresPermissions("ec:customer:spec")
	@RequestMapping(value = "saveSpec")
	public String saveSpec(EarningSpec earningSpec, RedirectAttributes redirectAttributes) {
		earningSpecService.save(earningSpec);
		addMessage(redirectAttributes, "保存成功");
		return "redirect:" + adminPath + "/ec/customer/specList";
	}

	@RequiresPermissions("ec:customer:spec")
	@RequestMapping(value = "deleteSpec")
	public String deleteSpec(EarningSpec earningSpec, RedirectAttributes redirectAttributes) {
		earningSpecService.delete(earningSpec);
		addMessage(redirectAttributes, "删除成功");
		return "redirect:" + adminPath + "/ec/customer/specList";
	}

	@RequiresPermissions("ec:customer:headImage")
	@RequestMapping(value = "headImageList")
	public String headImageList(HeadImage headImage, HttpServletRequest request, HttpServletResponse response,Model model) {
		model.addAttribute("headImage", headImage);
		model.addAttribute("page", headImageService.findPage(new Page<>(request, response), headImage));
		return "module/ec/customer/headImageList";
	}

    @RequiresPermissions("ec:customer:headImage")
	@RequestMapping(value = "saveHeadImage")
    @CacheEvict(value = CacheConfig.GET_INDEX_IMAGE_LIST_CACHE, cacheManager = "apiCM", allEntries = true)
	public String saveHeadImage(HeadImage headImage, RedirectAttributes redirectAttributes) {
        try {
            headImage.setPageUrl(URLDecoder.decode(headImage.getPageUrl(),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            headImage.setPageUrl(URLDecoder.decode(headImage.getPageUrl()));
        }
		headImageService.save(headImage);
		addMessage(redirectAttributes, "保存图片成功");
		return "redirect:" + adminPath + "/ec/customer/headImageList";
	}

    @RequiresPermissions("ec:customer:headImage")
	@RequestMapping(value = "activateHeadImage")
    @CacheEvict(value = CacheConfig.GET_INDEX_IMAGE_LIST_CACHE, cacheManager = "apiCM", allEntries = true)
	public String activateHeadImage(HeadImage headImage, RedirectAttributes redirectAttributes) {
		headImageService.activate(headImage);
		addMessage(redirectAttributes, "操作成功");
		return "redirect:" + adminPath + "/ec/customer/headImageList";
	}

    @RequiresPermissions("ec:customer:headImage")
	@RequestMapping(value = "deleteHeadImage")
    @CacheEvict(value = CacheConfig.GET_INDEX_IMAGE_LIST_CACHE, cacheManager = "apiCM", allEntries = true)
	public String deleteHeadImage(HeadImage headImage, RedirectAttributes redirectAttributes) {
		headImageService.delete(headImage);
		addMessage(redirectAttributes, "删除图片成功");
		return "redirect:" + adminPath + "/ec/customer/headImageList";
	}


    @RequestMapping(value = "messageRegisterList")
    public String messageRegisterList(MessageRegister messageRegister, HttpServletRequest request, HttpServletResponse response,Model model) {
        model.addAttribute("messageRegister", messageRegister);
        model.addAttribute("page", messageRegisterService.findPage(new Page<>(request, response), messageRegister));
        return "module/ec/customer/messageRegisterList";
    }

    @RequiresPermissions("ec:customer:messageRegister:edit")
    @RequestMapping(value = "messageRegisterForm")
    public String messageRegisterForm(MessageRegister messageRegister,Model model) {
        if(StringUtils.isNotEmpty(messageRegister.getId())){
            messageRegister = messageRegisterService.get(messageRegister);
        }
        model.addAttribute("messageRegister", messageRegister);
        return "module/ec/customer/messageRegisterForm";
    }

    @RequiresPermissions("ec:customer:messageRegister:edit")
    @RequestMapping(value = "saveMessageRegister")
    public String saveShare(MessageRegister messageRegister, RedirectAttributes redirectAttributes) {
        messageRegisterService.save(messageRegister);
        addMessage(redirectAttributes, "保存注册消息'" + StringUtils.abbr(messageRegister.getArticle().getTitle(), 100) + "'成功");
        return "redirect:" + adminPath + "/ec/customer/messageRegisterList";
    }

    @RequiresPermissions("ec:customer:messageRegister:edit")
    @RequestMapping(value = "deleteMessageRegister")
    public String deleteShare(MessageRegister messageRegister, RedirectAttributes redirectAttributes) {
        messageRegisterService.delete(messageRegister);
        addMessage(redirectAttributes, "删除注册消息成功");
        return "redirect:" + adminPath + "/ec/customer/messageRegisterList";
    }

    @RequiresPermissions("ec:customer:messageRegister:edit")
    @RequestMapping(value = "activateMessageRegister")
    public String activateShare(MessageRegister messageRegister, RedirectAttributes redirectAttributes) throws Exception {
        Integer i = messageRegisterService.activate(messageRegister);
        String msg = i > 0 ? "激活成功" : "激活失败，该注册消息已被激活或已被删除";
        addMessage(redirectAttributes, msg);
        return "redirect:" + adminPath + "/ec/customer/messageRegisterList";
    }

    @RequiresPermissions("ec:customer:featuredPage:edit")
    @RequestMapping(value = "featuredPage")
    public String featuredPage(FeaturedPage featuredPage,HttpServletRequest request, HttpServletResponse response,Model model) throws Exception {
        model.addAttribute("page", featuredPageService.findPage(new Page(request, response), featuredPage));
        return "module/ec/customer/featuredPage";
    }

    @RequiresPermissions("ec:customer:featuredPage:edit")
    @RequestMapping(value = "saveFeaturedPage")
    @CacheEvict(value = {CacheConfig.GET_VCAT_SHOP_CONFIG_CACHE}, cacheManager = "apiCM", allEntries = true)
    public String saveFeaturedPage(FeaturedPage featuredPage, RedirectAttributes redirectAttributes) throws Exception {
        featuredPage.setCodeByMap();
        featuredPageService.save(featuredPage);
        addMessage(redirectAttributes, "保存"+featuredPage.getName()+"成功");
        return "module/ec/customer/featuredPage";
    }

    @ResponseBody
    @RequestMapping(value = "getUpgradeRequest")
    public UpgradeRequest getUpgradeRequest(UpgradeRequest upgradeRequest){
        return upgradeRequestService.get(upgradeRequest);
    }

    @ResponseBody
    @RequiresPermissions("ec:customer:approvalUpgradeRequest")
    @RequestMapping(value = "approvalUpgradeRequest")
    public void approvalUpgradeRequest(UpgradeRequest upgradeRequest){
        upgradeRequestService.approval(upgradeRequest);
    }

    @ResponseBody
    @RequiresPermissions("ec:customer:approvalUpgradeRequest")
    @RequestMapping(value = "addUpgradeRequest")
    public void addUpgradeRequest(String shopId, String shopName){
        upgradeRequestService.add(shopId, shopName);
    }

    @RequiresPermissions("ec:customer:setUpgradeLimit")
    @RequestMapping(value = "toSetUpgradeLimit")
    public String toSetUpgradeLimit(Model model){
        model.addAttribute("currentUpgradeLimit", customerService.getCurrentUpgradeLimit());
        model.addAttribute("levelList", levelService.findList(new Level()));
        return "module/ec/customer/setUpgradeLimit";
    }

    @RequiresPermissions("ec:customer:setUpgradeLimit")
    @RequestMapping(value = "setUpgradeLimit")
    public String setUpgradeLimit(Level level){
        customerService.setUpgradeLimit(level);
        return "redirect:" + adminPath + "/ec/customer/toSetUpgradeLimit";
    }

    @RequiresPermissions("ec:customer:invitationCodeList")
    @RequestMapping(value = "invitationCodeList")
    public String invitationCodeList(InvitationCode invitationCode, Model model){
        model.addAttribute("invitationCode", invitationCode);
        return "module/ec/customer/invitationCodeList";
    }

    @ResponseBody
    @RequiresPermissions("ec:customer:invitationCodeList")
    @RequestMapping(value = "invitationCodeListData")
    public DataGrid invitationCodeListData(InvitationCode invitationCode,HttpServletRequest request,HttpServletResponse response){
        return DataGrid.parse(invitationCodeService.findPage(new Page(request,response),invitationCode));
    }

    @ResponseBody
    @RequiresPermissions("ec:customer:generateCode")
    @RequestMapping(value = "generateCode")
    public void generateCode(Integer count){
        invitationCodeService.batchInsert(count);
    }

    @ResponseBody
    @RequiresPermissions("ec:customer:generateCode")
    @RequestMapping(value = "stopCode")
    public void stopCode(InvitationCode invitationCode){
        invitationCodeService.stopCode(invitationCode);
    }
}
