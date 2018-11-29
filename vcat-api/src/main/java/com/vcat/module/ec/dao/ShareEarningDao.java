package com.vcat.module.ec.dao;

import java.util.List;
import java.util.Map;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Remind;
import com.vcat.module.ec.entity.ShareEarning;

/**
 * 分享奖励奖励DAO接口
 */
@MyBatisDao
public interface ShareEarningDao extends CrudDao<ShareEarning> {
    List<ShareEarning> getShareEarningList(String productId);
    Integer activate(ShareEarning shareEarning);
    
	ShareEarning getAvaShareEarning(ShareEarning shareEarning);

    /**
     * 移动端获取分享提醒列表
     * @return
     */
    List<Map<String,Object>> getShareMessageList(ShareEarning shareEarning);

    /**
     * 移动端订阅分享消息
     * @param remind
     */
    void remindShare(Remind remind);

    /**
     * 获取订阅数量
     * @param remind
     * @return
     */
    Integer remindCount(Remind remind);

    /**
     * 检查该分享活动商品是否正在分享中
     * @param shareEarning
     * @return
     */
    boolean checkSameProductActivated(ShareEarning shareEarning);

    /**
     * 获取饼状图需要的数据
     * @param id
     * @return
     */
    List<Map<String,Object>> getPieChartList(String id);

    /**
     * 根据商品ID获取需要推送的设备TOKEN
     * @param productId
     * @return
     */
    List<String> getPushTokenListByProductId(String productId);
}

