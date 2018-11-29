package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.AnswerSheetDao;
import com.vcat.module.ec.entity.AnswerSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 答卷Service
 */
@Service
@Transactional(readOnly = true)
public class AnswerSheetService extends CrudService<AnswerSheetDao, AnswerSheet> {
    @Autowired
    private QuestionService questionService;
    @Override
    public AnswerSheet get(AnswerSheet entity) {
        entity = dao.get(entity);
        if(null != entity){
            entity.setQuestionList(questionService.findListBySheetId(entity.getId()));
        }
        return entity;
    }

    @Override
    public AnswerSheet get(String id) {
        AnswerSheet answerSheet = new AnswerSheet();
        answerSheet.setId(id);
        return get(answerSheet);
    }
}
