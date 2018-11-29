package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.util.Date;
import java.util.List;

/**
 * 答卷
 */
public class AnswerSheet extends DataEntity<AnswerSheet> {
	private static final long serialVersionUID = 1L;
    private Customer customer;              // 答卷人
    private Questionnaire questionnaire;    // 所属问卷
    private Date answerTime;                // 答卷时间
    private List<Question> questionList;    // 答卷问题集合(带答案)

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public Date getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(Date answerTime) {
        this.answerTime = answerTime;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }
}
