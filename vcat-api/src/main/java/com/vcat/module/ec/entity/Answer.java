package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * 答案
 */
public class Answer extends DataEntity<Answer> {
	private static final long serialVersionUID = 1L;
    private AnswerSheet answerSheet;    // 所属答卷
    private Question question;          // 所属问题
    private String answer;              // 答案
    private String questionnaireQuestionId;//问题题目id

    public AnswerSheet getAnswerSheet() {
        return answerSheet;
    }

    public void setAnswerSheet(AnswerSheet answerSheet) {
        this.answerSheet = answerSheet;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

	public String getQuestionnaireQuestionId() {
		return questionnaireQuestionId;
	}

	public void setQuestionnaireQuestionId(String questionnaireQuestionId) {
		this.questionnaireQuestionId = questionnaireQuestionId;
	}
}
