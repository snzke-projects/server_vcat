package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Message;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface MessageDao extends CrudDao<Message> {
    /**
     * 根据等级集合批量添加消息
     * @param messageId
     * @param levels
     */
    void insertFlags(@Param("messageId")String messageId,@Param("levels")String[] levels);
}
