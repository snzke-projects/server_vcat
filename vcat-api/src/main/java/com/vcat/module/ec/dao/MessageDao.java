package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Message;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
import java.util.List;

@MyBatisDao
public interface MessageDao extends CrudDao<Message> {
    /**
     * 阅读店铺消息
     * @param shopId
     */
    void read(String shopId);

    /**
     * 添加消息是否阅读标识
     * @param message
     */
    void insertFlag(Message message);

    /**
     * 根据等级集合批量添加消息
     * @param messageId
     * @param levels
     */
    void insertFlags(@Param("messageId")String messageId,@Param("levels")String[] levels);

    /**
     * 获取V猫新动态列表
     * @param message
     * @return
     */
    List<Map<String,Object>> findMessageList(Message message);
    List<Map<String,Object>> findMessageListV2(Message message);

    /**
     * 获取最新消息
     * @param shopId
     * @return
     */
    Map<String,Object> getNewestMessage(@Param("shopId")String shopId);
}
