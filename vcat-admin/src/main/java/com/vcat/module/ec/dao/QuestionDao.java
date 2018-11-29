package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Question;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface QuestionDao extends CrudDao<Question> {
    /**
     * 获取答卷所属问题集合
     * @param answerSheetId
     * @return
     */
    List<Question> findListBySheetId(String answerSheetId);

    /**
     * 获取简答题所有回答
     * @param question
     * @return
     */
    List<Map<String,String>> findShortAnswerList(Question question);
}

