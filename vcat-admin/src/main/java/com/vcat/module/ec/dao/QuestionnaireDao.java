package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Questionnaire;

@MyBatisDao
public interface QuestionnaireDao extends CrudDao<Questionnaire> {
}

