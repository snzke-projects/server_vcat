package com.vcat.module.ec.service;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.QuestionnaireDao;
import com.vcat.module.ec.entity.AnswerSheet;
import com.vcat.module.ec.entity.Customer;
import com.vcat.module.ec.entity.Question;
import com.vcat.module.ec.entity.Questionnaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 答案Service
 */
@Service
@Transactional(readOnly = true)
public class QuestionnaireService extends CrudService<QuestionnaireDao, Questionnaire> {
    @Autowired
    private AnswerSheetService answerSheetService;
    @Autowired
    private QuestionService questionService;

    @Override
    public Questionnaire get(Questionnaire entity) {
        String customerId = entity.getSqlMap().get("customerId");
        Map<String, String> sqlMap = entity.getSqlMap();
        entity = dao.get(entity);
        Questionnaire childObject = new Questionnaire();
        childObject.setParent(entity);
        List<Questionnaire> childList = findList(childObject);

        if(StringUtils.isNotEmpty(customerId)){
            Customer customer = new Customer();
            customer.setId(customerId);
            customer.getSqlMap().putAll(sqlMap);
            if(null != childList && !childList.isEmpty()){
                for (int i = 0; i < childList.size(); i++) {
                    setAnswerSheet(childList.get(i),customer);
                }
            }
            setAnswerSheet(entity,customer);
        }else{
            if(null != childList && !childList.isEmpty()){
                for (int i = 0; i < childList.size(); i++) {
                    setQuestionList(childList.get(i));
                }
            }
            setQuestionList(entity);
        }

        entity.setChildList(childList);

        return entity;
    }

    private Questionnaire setAnswerSheet(Questionnaire questionnaire,Customer customer){
        AnswerSheet answerSheet = new AnswerSheet();
        answerSheet.setCustomer(customer);
        answerSheet.setQuestionnaire(questionnaire);
        answerSheet.getSqlMap().putAll(customer.getSqlMap());
        questionnaire.setAnswerSheet(answerSheetService.get(answerSheet));
        return questionnaire;
    }

    @Override
    public Questionnaire get(String id) {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setId(id);
        return get(questionnaire);
    }

    public void setQuestionList(Questionnaire questionnaire) {
        Question question = new Question();
        question.getSqlMap().put("questionnaireId", questionnaire.getId());
        questionnaire.setQuestionList(questionService.findList(question));
    }
}
