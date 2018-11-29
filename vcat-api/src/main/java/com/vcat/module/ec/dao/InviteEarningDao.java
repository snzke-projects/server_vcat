package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Customer;
import com.vcat.module.ec.entity.InviteEarning;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface InviteEarningDao extends CrudDao<InviteEarning> {
    /**
     * 获取当前正在使用的邀请奖励
     * @return
     */
	InviteEarning getInviteEarning(InviteEarning inviteEarning);

    /**
     * 激活邀请活动
     * @param inviteEarning
     * @return
     */
    Integer activate(InviteEarning inviteEarning);

    /**
     * 检查该邀请活动是否可激活
     * @param inviteEarning
     * @return
     */
    boolean checkCaneBeActivated(InviteEarning inviteEarning);

    /**
     * 获取饼状图需要的数据
     * @param id
     * @return
     */
    List<Map<String,Object>> getPieChartList(String id);

    /**
     * 获取可获得邀请奖励小店列表
     * @return
     */
    List<Customer> getAvailableInviteEarningShopList();
}
