package com.vcat.module.content.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.content.entity.Comment;

/**
 * 评论DAO接口
 */
@MyBatisDao
public interface CommentDao extends CrudDao<Comment> {

}
