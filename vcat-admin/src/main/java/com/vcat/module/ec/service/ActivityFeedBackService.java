package com.vcat.module.ec.service;

import com.google.common.collect.Lists;
import com.vcat.common.utils.IdGen;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ActivityFeedBackDao;
import com.vcat.module.ec.entity.ActivityFeedBack;
import com.vcat.module.ec.entity.Question;
import com.vcat.module.ec.entity.Questionnaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ActivityFeedBackService extends CrudService<ActivityFeedBackDao, ActivityFeedBack> {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private QuestionnaireService questionnaireService;

    @Transactional(readOnly = false)
    public void save(ActivityFeedBack feedBack, Set<String> questionIdSet) {
        // 删除之前发布的报告
        dao.deleteStatisticsAnswerByFeedBackId(feedBack.getId());
        dao.deleteStatisticsByFeedBackId(feedBack.getId());
        dao.delete(feedBack);

        // 保存报告
        dao.insert(feedBack);
        String activityId = feedBack.getActivity().getId();

        // 获取答案统计
        Map<String,Integer> answerMap = activityService.statisticsActivityAnswer(activityId);

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.getSqlMap().put("activityId", activityId);
        questionnaire = questionnaireService.get(questionnaire);

        List<Question> questionList = getQuestionListByQuestionnaire(questionnaire);

        for (int i = 0; i < questionList.size(); i++) {
            Question question = questionList.get(i);
            String questionId = question.getId();
            if(questionIdSet.contains(questionId)){
                String statisticsId = IdGen.uuid();
                question.getSqlMap().put("feedbackId",feedBack.getId());
                question.getSqlMap().put("id", statisticsId);
                dao.insertStatistics(question);
                String[] options = question.getOptions();
                for (int j = 0; j < options.length; j++) {
                    Integer count = answerMap.get(questionId + options[j]);
                    dao.insertStatisticsAnswer(statisticsId, options[j], null == count ? 0 : count);
                }
            }
        }

        activityService.feedback(activityId);
    }

    private List<Question> getQuestionListByQuestionnaire(Questionnaire questionnaire) {
        List<Question> questionList = Lists.newArrayList();
        if(null == questionnaire){
            return null;
        }

        questionList.addAll(questionnaire.getQuestionList());

        if(null != questionnaire.getChildList()){
            List<Questionnaire> questionnaireList = questionnaire.getChildList();
            for (int i = 0; i < questionnaireList.size(); i++) {
                questionList.addAll(getQuestionListByQuestionnaire(questionnaireList.get(i)));
            }
        }

        return questionList;
    }

    @Transactional(readOnly = false)
    public void activate(ActivityFeedBack feedback){
        dao.activate(feedback.getId(),feedback.getIsActivate());
    }

    public String getSelectedQuestionId(String feedbackId){
        return dao.getSelectedQuestionId(feedbackId);
    }
}
