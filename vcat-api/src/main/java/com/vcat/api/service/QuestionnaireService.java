package com.vcat.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.QuestionnaireDao;
import com.vcat.module.ec.dto.QuestionDto;
import com.vcat.module.ec.entity.Answer;
import com.vcat.module.ec.entity.AnswerSheet;
import com.vcat.module.ec.entity.Customer;
import com.vcat.module.ec.entity.Question;
import com.vcat.module.ec.entity.Questionnaire;
@Service
@Transactional(readOnly = true)
public class QuestionnaireService extends CrudService<Questionnaire> {

	@Autowired
	private QuestionnaireDao questionnaireDao;
	@Autowired
	private AnswerService answerService;
	@Autowired
	private AnswerSheetService answerSheetService;
	@Override
	protected CrudDao<Questionnaire> getDao() {
		return questionnaireDao;
	}

	public List<Questionnaire> getQuestionnaireList(String activityId) {
		return questionnaireDao.getQuestionnaireList(activityId);
		
	}

	@Transactional(readOnly = false)
	public void saveQuantionAnswer(JSONArray list, String customerId, String activityId) {
		Customer customer = new Customer();
		customer.setId(customerId);
		for(Object obj:list ){
			JSONObject gson = (JSONObject) obj;
			Questionnaire questionnaire = JSON.toJavaObject(gson,Questionnaire.class);
			//插入答卷表
			AnswerSheet answerSheet = new AnswerSheet();
			answerSheet.preInsert();
			answerSheet.setQuestionnaire(questionnaire);
			answerSheet.setCustomer(customer);
			answerSheetService.insert(answerSheet);
			//插入活动答案表
			questionnaireDao.insertActivityQuestionnaire(activityId,answerSheet.getId());
			//插入答案表
			List<QuestionDto> questionList = questionnaire.getQuestions();
			if (questionList != null && questionList.size() > 0) {
				for (QuestionDto dto : questionList) {
					Question question = new Question();
					question.setId(dto.getId());
					// 如果是多选题目
					if (dto.getType() == 2) {
						List<String> ansList = new ArrayList<>(
								Arrays.asList(dto.getAnswer().split("\\,")));
						if (ansList != null && ansList.size() > 0) {
							for (String anst : ansList) {
								Answer ans = new Answer();
								ans.preInsert();
								ans.setAnswerSheet(answerSheet);
								ans.setQuestion(question);
								ans.setAnswer(anst);
								answerService.insert(ans);
							}
						}
					} else {
						Answer ans = new Answer();
						ans.preInsert();
						ans.setAnswerSheet(answerSheet);
						ans.setQuestion(question);
						ans.setAnswer(dto.getAnswer());
						answerService.insert(ans);
					}
				}
			}

		}
		
	}

}
