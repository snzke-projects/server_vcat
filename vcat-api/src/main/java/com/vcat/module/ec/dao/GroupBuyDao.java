package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.GroupBuyDto;
import com.vcat.module.ec.entity.GroupBuy;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * Created by Code.Ai on 16/5/11.
 * Description:
 */
@MyBatisDao
public interface GroupBuyDao extends CrudDao<GroupBuy> {
    GroupBuyDto getGroupBuy(GroupBuyDto groupBuyDto);
    String getGroupBuyByProductId(String productId);
}
