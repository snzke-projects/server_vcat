package com.vcat.module.ec.service;

import com.vcat.common.persistence.Page;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.QuestionDao;
import com.vcat.module.ec.entity.Question;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 问题Service
 */
@Service
@Transactional(readOnly = true)
public class QuestionService extends CrudService<QuestionDao, Question> {
    /**
     * 获取答卷所属问题集合
     * @param answerSheetId
     * @return
     */
    public List<Question> findListBySheetId(String answerSheetId){
        return dao.findListBySheetId(answerSheetId);
    }
    public Page<Map<String,String>> findShortAnswerList(Page page,Question question){
        question.setPage(page);
        page.setList(dao.findShortAnswerList(question));
        return page;
    }
}
