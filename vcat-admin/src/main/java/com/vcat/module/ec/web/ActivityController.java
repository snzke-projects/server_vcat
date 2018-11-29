package com.vcat.module.ec.web;

import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.core.entity.User;
import com.vcat.module.core.utils.excel.ExportExcel;
import com.vcat.module.ec.entity.*;
import com.vcat.module.ec.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * 体验官Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/activity")
public class ActivityController extends BaseController {
	@Autowired
	private ActivityService activityService;
    @Autowired
    private QuestionnaireService questionnaireService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ActivityFeedBackService activityFeedBackService;
    @Autowired
    private ActivityOfflineService activityOfflineService;

	@RequestMapping(value = "activityList")
	public String activityList(Activity activity, HttpServletRequest request, HttpServletResponse response,Model model) {
		model.addAttribute("activity", activity);
		model.addAttribute("page", activityService.findPage(new Page<>(request, response), activity));
		return "module/ec/activity/activityList";
	}

	@RequestMapping(value = "activityForm")
	public String activityForm(Activity activity,Model model) {
		if(StringUtils.isNotEmpty(activity.getId())){
			activity = activityService.get(activity);
		}
		model.addAttribute("activity", activity);
		return "module/ec/activity/activityForm";
	}

	@RequestMapping(value = "saveActivity")
	public String saveActivity(Activity activity, RedirectAttributes redirectAttributes) {
		activityService.save(activity);
		addMessage(redirectAttributes, "保存体验官'" + StringUtils.abbr(activity.getTitle(), 100) + "'成功");
		return "redirect:" + adminPath + "/ec/activity/activityList";
	}

	@RequestMapping(value = "deleteActivity")
	public String deleteActivity(Activity activity, RedirectAttributes redirectAttributes) {
		activityService.delete(activity);
		addMessage(redirectAttributes, "删除体验官成功");
		return "redirect:" + adminPath + "/ec/activity/activityList";
	}

	@RequestMapping(value = "activateActivity")
	public String activateActivity(Activity activity, RedirectAttributes redirectAttributes) {
		Integer i = activityService.activate(activity);
		String msg = i > 0 ? "激活成功" : "激活失败，该体验官已被激活或已被删除";
		addMessage(redirectAttributes, msg);
		return "redirect:" + adminPath + "/ec/activity/activityList";
	}

	@RequestMapping(value = "participationList")
	public String participationList(Activity activity, HttpServletRequest request, HttpServletResponse response,Model model) {
		model.addAttribute("activity", activityService.get(activity));
		model.addAttribute("page", activityService.participationList(new Page<>(request, response), activity));
		return "module/ec/activity/participationList";
	}

    @RequestMapping(value = "exportParticipationList")
    public String exportParticipationList(Activity activity, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            Page<Participation> page = activityService.participationList(new Page<User>(request, response, -1), activity);

            String fileName = "体验官参与人员列表.xlsx";
            String title = "";
            if(null != page.getList() && !page.getList().isEmpty()){
                title = page.getList().get(0).getTitle();
                fileName = title + fileName;
            }
            new ExportExcel(title + " 体验官参与人员列表", Participation.class).setDataList(page.getList()).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导出失败！失败信息："+e.getMessage());
        }
        return "redirect:" + adminPath + "/ec/activity/participationList";
    }

    @RequestMapping(value = "viewAnswerSheet")
    public String viewAnswerSheet(@RequestParam(value = "customerId")String customerId,
                                  @RequestParam(value = "activityId")String activityId,Model model) {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.getSqlMap().put("customerId",customerId);
        questionnaire.getSqlMap().put("activityId",activityId);
        model.addAttribute("letterArray", StringUtils.LETTER_ARRAY);
        model.addAttribute("questionnaire", questionnaireService.get(questionnaire));
        return "module/ec/activity/answerSheet";
    }

    @RequestMapping(value = "viewActivityQuestionnaireReport")
    public String viewActivityQuestionnaireReport(@RequestParam(value = "activityId")String activityId,Model model) {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.getSqlMap().put("activityId", activityId);
        model.addAttribute("reportedCount", activityService.countReported(activityId));
        model.addAttribute("questionMap", activityService.statisticsActivityAnswer(activityId));
        model.addAttribute("questionnaire", questionnaireService.get(questionnaire));
        model.addAttribute("activity", activityService.get(activityId));
        model.addAttribute("letterArray", StringUtils.LETTER_ARRAY);
        ActivityFeedBack feedBack = activityFeedBackService.get(activityId);
        model.addAttribute("feedback", null == feedBack ? new ActivityFeedBack() : feedBack);
        model.addAttribute("selectedQuestionId", activityFeedBackService.getSelectedQuestionId(activityId));
        return "module/ec/activity/activityQuestionnaireReport";
    }

    @RequestMapping(value = "viewShortAnswerList")
    public String viewShortAnswerList(Question question,HttpServletRequest request, HttpServletResponse response,Model model) {
        model.addAttribute("question",question);
        model.addAttribute("page",questionService.findShortAnswerList(new Page(request, response), question));
        return "module/ec/activity/shortAnswerList";
    }

    @ResponseBody
    @RequestMapping(value = "feedBack")
    public void feedBack(ActivityFeedBack activityFeedBack,@RequestParam(value = "questionIdSet")Set<String> questionIdSet) {
        activityFeedBackService.save(activityFeedBack,questionIdSet);
    }

    @ResponseBody
    @RequestMapping(value = "activateFeedback")
    public void activateFeedback(ActivityFeedBack activityFeedBack) {
        activityFeedBackService.activate(activityFeedBack);
    }

    @RequestMapping(value = "activityOfflineList")
    public String activityOfflineList(ActivityOffline activityOffline, HttpServletRequest request, HttpServletResponse response,Model model) {
        model.addAttribute("activityOffline", activityOffline);
        model.addAttribute("page", activityOfflineService.findPage(new Page<>(request, response), activityOffline));
        return "module/ec/activity/activityOfflineList";
    }

    @RequestMapping(value = "activityOfflineForm")
    public String activityOfflineForm(ActivityOffline activityOffline,Model model) {
        if(StringUtils.isNotEmpty(activityOffline.getId())){
            activityOffline = activityOfflineService.get(activityOffline);
        }
        model.addAttribute("activityOffline", activityOffline);
        return "module/ec/activity/activityOfflineForm";
    }

    @RequestMapping(value = "saveActivityOffline")
    public String saveActivityOffline(ActivityOffline activityOffline, RedirectAttributes redirectAttributes) {
        activityOfflineService.save(activityOffline);
        addMessage(redirectAttributes, "保存活动'" + StringUtils.abbr(activityOffline.getTitle(), 100) + "'成功");
        return "redirect:" + adminPath + "/ec/activity/activityOfflineList";
    }

    @RequestMapping(value = "deleteActivityOffline")
    public String deleteActivityOffline(ActivityOffline activityOffline, RedirectAttributes redirectAttributes) {
        activityOfflineService.delete(activityOffline);
        addMessage(redirectAttributes, "删除活动成功");
        return "redirect:" + adminPath + "/ec/activity/activityOfflineList";
    }

    @RequestMapping(value = "activateActivityOffline")
    public String activateActivityOffline(ActivityOffline activityOffline, RedirectAttributes redirectAttributes) {
        Integer i = activityOfflineService.activate(activityOffline);
        String msg = i > 0 ? "激活成功" : "激活失败，该活动已被激活或已被删除";
        addMessage(redirectAttributes, msg);
        return "redirect:" + adminPath + "/ec/activity/activityOfflineList";
    }

    @RequestMapping(value = "changeOpenStatus")
    public String changeOpenStatus(ActivityOffline activityOffline, RedirectAttributes redirectAttributes) {
        activityOfflineService.changeOpenStatus(activityOffline);
        addMessage(redirectAttributes, "设置成功");
        return "redirect:" + adminPath + "/ec/activity/activityOfflineList";
    }
    @RequestMapping(value = "offlineParticipationList")
    public String offlineParticipationList(ActivityOffline activityOffline, HttpServletRequest request, HttpServletResponse response,Model model) {
        model.addAttribute("activityOffline", activityOfflineService.get(activityOffline));
        model.addAttribute("page", activityOfflineService.participationList(new Page<>(request, response), activityOffline));
        return "module/ec/activity/offlineParticipationList";
    }

    @RequestMapping(value = "exportOfflineParticipationList")
    public String exportOfflineParticipationList(ActivityOffline activityOffline, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            Page<Participation> page = activityOfflineService.participationList(new Page<ActivityOffline>(request, response, -1), activityOffline);

            String fileName = "活动参与人员列表.xlsx";
            String title = "";
            if(null != page.getList() && !page.getList().isEmpty()){
                title = page.getList().get(0).getTitle();
                fileName = title + fileName;
            }
            new ExportExcel(title + " 活动官参与人员列表", Participation.class).setDataList(page.getList()).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导出失败！失败信息："+e.getMessage());
        }
        return "redirect:" + adminPath + "/ec/activity/participationList";
    }
}
