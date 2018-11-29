package com.vcat.api.service;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.lock.DistLockHelper;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.service.CrudService;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.dao.ActivityOfflineDao;
import com.vcat.module.ec.dto.OfflineActivityDto;
import com.vcat.module.ec.entity.ActivityOffline;
import com.vcat.module.ec.entity.Address;
import com.vcat.module.ec.entity.CustomerOfflineActivity;
import org.redisson.core.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
@Transactional(readOnly = true)
public class ActivityOfflineService extends CrudService<ActivityOffline> {
    @Autowired
    private ActivityOfflineDao activityOfflineDao;
    @Autowired
    private CustomerOfflineActivityService customerOfflineActivityService;
    @Override
    protected CrudDao<ActivityOffline> getDao() {
        return activityOfflineDao;
    }

    /**
     * 获取V猫会活动列表
     * @param pageNo 当前页
     * @param cid 客户ID
     * @return 返回分页数据
     */
    @Transactional(readOnly = false)
    public Map<String, Object> getOfflineActivityList(int pageNo, String cid) {
        int   count = activityOfflineDao.countOfflineActivityList();
        Pager page  = new Pager();
        page.setPageNo(pageNo);
        page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
        page.setRowCount(count);
        page.doPage();
        List<OfflineActivityDto> list              = activityOfflineDao.getOfflineActivityList(page, cid);
        String                   offlineActivityId = "";
        String                   templateUrl       = "";

        // 将活动ID拼接到H5页面之后
        for(OfflineActivityDto offlineActivityDto : list){
            offlineActivityId = offlineActivityDto.getId();
            templateUrl = offlineActivityDto.getTemplateUrl().replaceAll(ApiConstants.VCAT_DOMAIN,"") + "?offlineActivityId=" + offlineActivityId;
            offlineActivityDto.setTemplateUrl(templateUrl);
            // 如果人数满了,则更新 openStatus=0
            if(offlineActivityDto.isFull()){
                //修改 open_status 状态为0
                activityOfflineDao.updateStatus(offlineActivityDto.getId());
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        map.put("page", page);
        map.put("list", list);
        return map;
    }

    /**
     * 根据ID查询线下活动信息
     * @param offlineActivityId V猫会活动ID
     * @param cid 客户ID
     * @return 返回活动详情Map
     */
    public Map<String,Object> getOfflineActivity(String offlineActivityId, String cid){
        //返回ActivityOffline对象
        ActivityOffline activityOffline= activityOfflineDao.getOfflineActivity(offlineActivityId);
        Map<String, Object> map = new HashMap<>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        map.put("id",activityOffline.getId());
        map.put("title",activityOffline.getTitle());
        map.put("imgUrl", QCloudUtils.createOriginalDownloadUrl(activityOffline.getImgUrl()));
        map.put("startDate",activityOffline.getStartDate());
        map.put("endDate",activityOffline.getEndDate());
        map.put("details",activityOffline.getDetails());
        map.put("address",activityOffline.getAddress());
        map.put("seat",activityOffline.getSeat());
        map.put("lastSeat",activityOffline.getLastSeat());
        //map.put("openStatus",activityOffline.getOpenStatus());
        // 判断活动是否还在进行中
        // 如果人数未满且 openStatus=1 isProgress = true
        if(activityOffline.getOpenStatus() == 1 && activityOffline.getLastSeat() != 0){
            map.put("isProgress",true);
        }else map.put("isProgress",false);
        //判断当前客户是否参加了此项活动
        if(activityOfflineDao.isJoin(offlineActivityId, cid)){
            map.put("isSelfJoined",true);
            map.put("ticketUrl",QCloudUtils.createOriginalDownloadUrl(activityOffline.getTicketUrl()));
        }else map.put("isSelfJoined",false);
        return map;
    }

    //判断报名名额是否已满
    public String getSeatStatus(String offlineActivityId) {
        return activityOfflineDao.getSeatStatus(offlineActivityId);
    }
    public void updateOpenStatus(String customerId){
        activityOfflineDao.updateStatus(customerId);
    }
    //参加活动
    @Transactional(readOnly = false)
    public Map<String, Object> joinOfflineActivity(CustomerOfflineActivity coa){
        Map<String, Object> map = new HashMap<>();
        RLock lock = DistLockHelper.getLock("joinOfflineActivity");
        try {
            lock.lock(DistLockHelper.MAX_HOLD_TIME, TimeUnit.SECONDS);
            String isFull = this.getSeatStatus(coa.getActivityOffline().getId());
            if(ApiConstants.YES.equals(isFull)){
                map.put(ApiMsgConstants.SEAT_IS_FULL,
                        ApiMsgConstants.FAILED_CODE);
                return map;
            }
            customerOfflineActivityService.insert(coa);
        } finally {
            if (lock.isLocked())
                lock.unlock();
        }
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        return map;
    }
}
