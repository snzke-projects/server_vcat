package com.vcat.api.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vcat.api.service.*;
import com.vcat.api.service.scheduled.DeleteCustomerActivityJob;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.persistence.Pager;
import com.vcat.common.utils.IdcardUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.core.utils.DictUtils;
import com.vcat.module.ec.dto.QuestionDto;
import com.vcat.module.ec.entity.*;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

/**
 * 移动客户端 - 发现
 */
@RestController
@ApiVersion(1)
public class DiscoveryController extends RestBaseController {
	@Autowired
	private ForecastService forecastService;
	@Autowired
	private ActivityService activityService;
	@Autowired
	private CustomerActivityService customerActivityService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private QuestionnaireService questionnaireService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ActivityFeedBackService activityFeedbackService;
	@Autowired
	private AnswerSheetService answerSheetService;
    @Autowired
    private CustomerAddressService customerAddressService;
	@Autowired
	private ActivityStatService activityStatService;
    @Autowired
    private OrderService orderService;
	private static Logger logger = Logger.getLogger(DiscoveryController.class);

	/**
	 * 获取上架预告列表
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/getForecastList")
	public Object getForecastList(@RequestHeader(value = "token", defaultValue = "") String token,
							  @RequestParam(value = "pageNo", defaultValue = "1") String pageNo) {
		if (StringUtils.isEmpty(token)) {
			logger.debug("获取上架预告列表缺少Token参数！！！");
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,	ApiMsgConstants.FAILED_CODE);
		}
		Pager page = new Pager();
		page.setPageNo(Integer.parseInt(pageNo));
		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);

		try {
			page = forecastService.getPager(page);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new MsgEntity("获取上架预告列表失败：" + e.getMessage(), ApiMsgConstants.FAILED_CODE);
		}

		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("page", page);
		map.put("list", page.getList());
		page.setList(null);
		return map;
	}

	/**
	 * 获取预购活动详情
	 * @param id
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/getForecastDetail")
	public Object getForecastDetail(@RequestParam(value = "id", defaultValue = "") String id){
		if (StringUtils.isBlank(id)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        ProductForecast productForecast = new ProductForecast();
        productForecast.setId(id);
		map.putAll(forecastService.getForecastDetail(productForecast));
		return map;
	}
	
	/**
	 * 获取活动详情
	 * @param token
	 * @param activityId
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/getActivityDetail")
	public Object getActivityDetail(@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "activityId", defaultValue = "") String activityId){
		if (StringUtils.isEmpty(token) || StringUtils.isBlank(activityId)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		Activity activity = new Activity();
		activity.setId(activityId);
		activity = activityService.get(activity);
		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("priceName", activity.getFeeName());
        map.put("discription", activity.getDetails());
        String customerId = StringUtils.getCustomerIdByToken(token);
        map.put("isParticipate", activityService.getIsParticipate(activityId, customerId));
		return map;
	}

	/**
	 * 获取活动列表 V猫体验官 体验官任务列表
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/getActivityList")
	public Object getActivityList(@RequestHeader(value = "token", defaultValue = "") String token,
							  @RequestParam(value = "pageNo", defaultValue = "1") String pageNo) {
		if (StringUtils.isEmpty(token)) {
			logger.debug("获取活动列表缺少Token参数！！！");
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,	ApiMsgConstants.FAILED_CODE);
		}
		Pager page = new Pager();
		page.setPageNo(Integer.parseInt(pageNo));
		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);

		String shopId = StringUtils.getCustomerIdByToken(token);

		try {
			page = activityService.getPager(page, shopId);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new MsgEntity("获取活动列表失败：" + e.getMessage(), ApiMsgConstants.FAILED_CODE);
		}

		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("page", page);
		map.put("list", page.getList());
		page.setList(null);
		return map;
	}

	/**
	 * 获取我的活动列表
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/getActivityOfMy")
	public Object getActivityOfMy(@RequestHeader(value = "token", defaultValue = "") String token,
								  @RequestParam(value = "pageNo", defaultValue = "1") String pageNo) {
		if (StringUtils.isEmpty(token)) {
			logger.debug("获取我的活动列表缺少Token参数！！！");
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,	ApiMsgConstants.FAILED_CODE);
		}
		Pager page = new Pager();
		page.setPageNo(Integer.parseInt(pageNo));
		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);

		String shopId = StringUtils.getCustomerIdByToken(token);

		try {
			page = activityService.getActivityOfMy(page, shopId);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new MsgEntity("获取我的活动列表失败：" + e.getMessage(), ApiMsgConstants.FAILED_CODE);
		}

		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("page", page);
		map.put("list", page.getList());
		page.setList(null);
		return map;
	}

    /**
     * 获取卖家的活动参与情况
     * @return
     */
    @RequiresRoles("seller")
    @RequestMapping("/api/getActivityParticipation")
    public Object getActivityParticipation(@RequestHeader(value = "token", defaultValue = "") String token,
                               @RequestParam(value = "activityId", defaultValue = "") String activityId) {
        if(StringUtils.isEmpty(activityId)){
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        String buyerId = StringUtils.getCustomerIdByToken(token);
        Customer cus = new Customer();
        cus.setId(buyerId);
        //cus.setRegistered(ApiConstants.ALL_REGISTER);
        cus = customerService.get(cus);
        Activity activity = new Activity(activityId);
        activity.setId(activityId);
        activity = activityService.get(activity);
        CustomerActivity customerActivity = new CustomerActivity();
        customerActivity.setCustomer(cus);
        customerActivity.setActivity(activity);
        customerActivity = customerActivityService.get(customerActivity);
        Map<String, Object> map = new HashMap<>();
        map.put("price",activity.getFee());
        map.put("priceName",activity.getFeeName());
        Address list = customerAddressService.getDefaultAddressList(buyerId);
        map.put("addressList", list);
        if(null == customerActivity){
            map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
            map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
            map.put(ApiConstants.ACTIVITY_PARTICIPATION_STATE, ApiConstants.ACTIVITY_PARTICIPATION_STATE_NOT_PARTICIPATING);
			if(IdcardUtils.isPhoneNum(cus.getPhoneNumber())){
                map.put("phoneNum", cus.getPhoneNumber());
			}
            return map;
        }
        Order order = activityService.getActivityOrder(activityId,buyerId);
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        map.put("realName", customerActivity.getRealName());
        map.put("phoneNum", customerActivity.getPhoneNum());
        map.put("qq", customerActivity.getQq());
        map.put("email", customerActivity.getEmail());
        if(activity.getFee().compareTo(BigDecimal.ZERO) == 1 && null != order){  // 如果活动需支付活动费用，则判断是否已支付
            map.put("addressId", customerActivity.getAddressId());
            if("1".equals(order.getPaymentStatus())){   // 如果活动费用已支付，则返回收货地址ID
                // 暂无操纵
            }else{  // 如果活动费用未支付，则查询支付单ID和过期时间
                map.put("paymentId", order.getPayment().getId());
                Calendar calendar = Calendar.getInstance();
                Calendar now = Calendar.getInstance();
                Integer minute = Integer.parseInt(DictUtils.getDictValue(ApiConstants.ACTIVITY_FEES_PAID_TIME, "15"));
                calendar.setTime(order.getAddDate());
                calendar.add(Calendar.MINUTE, minute);
                if(calendar.before(now)){ // 如果支付单已经过期，则返回未参加状态
                    orderService.deleteOrderByPaymentId(order.getPayment().getId());
                    activityService.deleteActivityCustomer(activityId, buyerId);
                    map.put(ApiConstants.ACTIVITY_PARTICIPATION_STATE, ApiConstants.ACTIVITY_PARTICIPATION_STATE_NOT_PARTICIPATING);
                    map.put("phoneNum", cus.getPhoneNumber());
                    return map;
                }
                map.put("expiredTime", calendar.getTimeInMillis());
                map.put(ApiConstants.ACTIVITY_PARTICIPATION_STATE, ApiConstants.ACTIVITY_PARTICIPATION_STATE_NOT_PAID);
                return map;
            }
        }

        map.put(ApiConstants.ACTIVITY_PARTICIPATION_STATE, ApiConstants.ACTIVITY_PARTICIPATION_STATE_PARTICIPATED);

        return map;
    }

	/**
	 * 卖家参加活动
	 * @return
	 */
	@RequiresRoles("seller")
	@RequestMapping("/api/joinActivity")
	public Object joinActivity(@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param) {
		// 简单验证,保证服务端不会出异常
		JSONObject params;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			logger.error("params 出错"+e.getMessage());
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String activityId = params.getString("activityId");
		String realName = params.getString("realName");
		String phoneNum = params.getString("phoneNum");
		String qq = params.getString("qq");
		String email = params.getString("email");
		String addressId = params.getString("addressId");
		if (StringUtils.isBlank(activityId) ||StringUtils.isBlank(addressId) || StringUtils.isBlank(realName)
				|| !IdcardUtils.isPhoneNum(phoneNum)
				|| !IdcardUtils.isEmail(email) || StringUtils.isBlank(qq)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		//判断名额是否已满
		String isFull = activityService.getSeatStatus(activityId);
		if(ApiConstants.YES.equals(isFull)){
			return new MsgEntity(ApiMsgConstants.SEAT_IS_FULL,
					ApiMsgConstants.FAILED_CODE);
		}
		String shopId = StringUtils.getCustomerIdByToken(token);
		Activity activity = new Activity();
		activity.setId(activityId);
        activity = activityService.get(activity);
        if(activity.getStartDate().after(new Date()) || activity.getEndDate().before(new Date())){
            return new MsgEntity(ApiMsgConstants.ACTIVITY_TIME_OUT, ApiMsgConstants.FAILED_CODE);
        }
		Customer customer = new Customer();
		customer.setId(shopId);
		CustomerActivity ca = new CustomerActivity();
		ca.setCustomer(customer);
		ca.setActivity(activity);
		CustomerActivity cusa = customerActivityService.get(ca);
		//如果已经参加过活动，不再插入数据
		if(cusa!=null){
			return new MsgEntity(ApiMsgConstants.ACTIVITY_JOINED,
					ApiMsgConstants.FAILED_CODE);
		}
		ca.preInsert();
		ca.setRealName(realName);
		ca.setPhoneNum(phoneNum);
		ca.setEmail(email);
		ca.setQq(qq);
		ca.setAddressId(addressId);

        Address address = new Address();
        address.setId(addressId);
        // 参加活动，并使用活动费用下单
        Map<String, Object> resultMap = activityService.joinActivity(ca, address, shopId);

        Map<String, Object> map = new HashMap<>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);

        if (null != activity && activity.getFee().compareTo(BigDecimal.ZERO) == 1){
            String paymentId = resultMap.get("paymentId")+"";
            map.put("paymentId", paymentId);

            Calendar calendar = Calendar.getInstance();

            Integer minute = Integer.parseInt(DictUtils.getDictValue(ApiConstants.ACTIVITY_FEES_PAID_TIME, "15"));
            calendar.add(Calendar.MINUTE,minute);
            map.put("expiredTime", calendar.getTimeInMillis());

            // 增加删除未支付订单任务
            DeleteCustomerActivityJob.pushJob(activityId, paymentId, calendar.getTime());
        }

        logger.info("-------------------------------------"+realName+"参加活动成功:" + map);
		return map;
	}
	/**
	 * 获取当前活动的问卷和答卷
	 * @param token
	 * @param param
	 * @return
	 */
	@RequestMapping("/api/getQuestion")
	public Object getQuestion(@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param) {
		// 简单验证,保证服务端不会出异常
		JSONObject params;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			logger.error("params 出错" + e.getMessage());
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String activityId = params.getString("activityId");
		String customerId = StringUtils.getCustomerIdByToken(token);
		List<Questionnaire> list = questionnaireService.getQuestionnaireList(activityId);
		if (list != null && list.size() > 1){
			//个人题目
			Questionnaire q1 = list.get(0);
			q1.setQuestions(questionService.getPersonalQuestionList(q1.getId(),customerId,activityId));
			//其他题目
			for(int i=1;i<list.size();i++){
				Questionnaire qes = list.get(i);
				qes.setQuestions(questionService.getQuestionList(qes.getId(),customerId,activityId));
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		if (list != null && list.size() > 1){
			map.put("list", list);
			Questionnaire qes = list.get(1);
			if (qes.getQuestions() != null
					&& qes.getQuestions().size() > 0
					&& !StringUtils.isBlank((String)qes.getQuestions().get(0)
							.getAnswer())) {
				map.put("isSubmit", ApiConstants.YES);
			}else{
				map.put("isSubmit", ApiConstants.NO);
			}
		}
		return map;
	}
	/**
	 * 提交答卷
	 * @param token
	 * @param param
	 * @return
	 */
	@RequestMapping("/api/submitAnswer")
	public Object submitAnswer(@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "params", defaultValue = "") String param) {
		// 简单验证,保证服务端不会出异常
		JSONObject params;
		try {
			params = JSONObject.parseObject(param);
		} catch (Exception e) {
			logger.error("params 出错" + e.getMessage());
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		if (params == null) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		JSONArray list = params.getJSONArray("list");
		String activityId = params.getString("activityId");
		if (list == null || list.size() == 0||StringUtils.isBlank(activityId)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		String customerId = StringUtils.getCustomerIdByToken(token);
		//判断此活动是否已经提交报告
		List<AnswerSheet> as = answerSheetService.getAnswerSheet(customerId,activityId);
		if (as != null&&!as.isEmpty()){
			return new MsgEntity(ApiMsgConstants.ANSWER_IS_SUBMITTED,
					ApiMsgConstants.FAILED_CODE);
		}
		questionnaireService.saveQuantionAnswer(list,customerId,activityId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		return map;
	}
	
	/**
	 * 获取活动反馈报告列表
	 * @param token
	 * @return
	 */
	@RequestMapping("/api/getFeedBackList")
	public Object getFeedbackList(@RequestHeader(value = "token", defaultValue = "") String token,
			 @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo) {
		int count = activityFeedbackService.countFeedBackList();
		Pager page = new Pager();
		page.setPageNo(pageNo);
		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
		page.setRowCount(count);
		page.doPage();
		List<ActivityFeedBack> list = activityFeedbackService.getFeedBackList(page);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("list", list);
		map.put("page", page);
		return map;
	}
	
	/**
	 * 获取活动反馈报告详情
	 * @param token
	 * @return
	 */
	@RequestMapping("/api/getFeedBackDetail")
	public Object getFeedBackDetail(@RequestHeader(value = "token", defaultValue = "") String token,
			 @RequestParam(value = "feedBackId", defaultValue = "") String feedBackId) {
		if (StringUtils.isBlank(feedBackId)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		ActivityFeedBack feedBack = activityFeedbackService.getDetail(feedBackId);
		
		List<QuestionDto> questionList = activityStatService.getQuestionList(feedBackId);
		//计算百分比
		if(questionList != null&& questionList.size()>0){
			
			questionList = activityStatService.createPercent(questionList);
		}
		if(feedBack != null){
			feedBack.setQuestionlist(questionList);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("feedBack", feedBack);
		return map;
	}
}
