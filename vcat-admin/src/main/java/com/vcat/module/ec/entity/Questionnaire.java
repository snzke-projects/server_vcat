package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.util.List;

/**
 * 问卷
 */
public class Questionnaire extends DataEntity<Questionnaire> {
	private static final long serialVersionUID = 1L;
    private Questionnaire parent;   // 父问卷
    private String title;           // 问卷标题
    private Integer displayOrder;   // 排序

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

    public List<Questionnaire> getChildList() {
        return childList;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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


    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }
}
