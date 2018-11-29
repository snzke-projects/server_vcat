package com.vcat.module.ec.entity;

import java.util.List;

import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.ec.dto.QuestionDto;

/**
 * 问卷
 */
public class Questionnaire extends DataEntity<Questionnaire> {
	private static final long serialVersionUID = 1L;
    private Questionnaire parent;   // 父问卷
    private String title;           // 问卷标题
    private Integer display_order;  // 排序
    private List<QuestionDto> questions;//问卷的题目

    private List<Questionnaire> childList;  // 子问卷
    private AnswerSheet answerSheet;        // 答卷
    private List<Question> questionList;    // 问题集合
    public Questionnaire getParent() {
        return parent;
    }

    public void setParent(Questionnaire parent) {
        this.parent = parent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDisplay_order() {
        return display_order;
    }

    public void setDisplay_order(Integer display_order) {
        this.display_order = display_order;
    }



    public List<Questionnaire> getChildList() {
        return childList;
    }

    public void setChildList(List<Questionnaire> childList) {
        this.childList = childList;
    }

    public AnswerSheet getAnswerSheet() {
        return answerSheet;
    }

    public void setAnswerSheet(AnswerSheet answerSheet) {
        this.answerSheet = answerSheet;
    }

	public List<QuestionDto> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionDto> questions) {
		this.questions = questions;
	}


    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }
}
