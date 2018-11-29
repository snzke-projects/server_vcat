package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.GroupBuySponsorDto;
import com.vcat.module.ec.entity.GroupBuySponsor;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Code.Ai on 16/5/11.
 * Description:
 */
@MyBatisDao
public interface GroupBuySponsorDao extends CrudDao<GroupBuySponsor> {
    void insertGroupBuySponsor(GroupBuySponsorDto groupBuySponsorDto);
    GroupBuySponsorDto getGroupBuySponsor(GroupBuySponsorDto groupBuySponsorDto);
    int getGroupBuyActivityCount(@Param("shopId")String shopId,@Param("type")int type);
    List<Map<String,Object>> getMyGroupBuyActivityList(@Param("shopId")String shopId,@Param("type")int type,@Param("page")Pager page);
    void updateGroupBuySponsor(GroupBuySponsorDto groupBuySponsorDto);
    void lockGroupBuy(@Param("groupBuySponsorId")String groupBuySponsorId,@Param("lockType")int lockType);
    void updateGroupBuyActivity(@Param("groupBuySponsorId")String groupBuySponsorId);
    int closeSponsor(String sponsorId);
    Map<String,Object> gotoPageByStatus(@Param("customerId")String customerId, @Param("groupBuySponsorId")String groupBuySponsorId);
}
