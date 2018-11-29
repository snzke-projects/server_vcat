package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.GroupBuyCustomerDto;
import com.vcat.module.ec.entity.GroupBuyCustomer;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Code.Ai on 16/5/11.
 * Description:
 */
@MyBatisDao
public interface GroupBuyCustomerDao extends CrudDao<GroupBuyCustomer> {
    void insertGroupBuyCustomer(GroupBuyCustomerDto groupBuyCustomerDto);
    List<Map<String,Object>> getJoinedCustomers(@Param("groupBuySponsorId")String groupBuySponsorId);
    List<Map<String,Object>> getGroupBuyInfo(@Param("paymentId")String paymentId);
    GroupBuyCustomerDto getGroupBuyCustomer(GroupBuyCustomerDto groupBuyCustomerDto);
    void updateGroupBuyCustomer(GroupBuyCustomerDto groupBuyCustomerDto);
    List<GroupBuyCustomer> findListBySponsorId(String sponsorId);
    String getShopIdByGroupBuySponsorId(@Param("groupBuySponsorId")String groupBuySponsorId);
}
