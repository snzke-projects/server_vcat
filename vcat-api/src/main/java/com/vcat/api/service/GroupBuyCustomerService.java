package com.vcat.api.service;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.GroupBuyCustomerDao;
import com.vcat.module.ec.dto.GroupBuyCustomerDto;
import com.vcat.module.ec.entity.GroupBuyCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by Code.Ai on 16/5/11.
 * Description:
 */
@Service
@Transactional(readOnly = true)
public class GroupBuyCustomerService extends CrudService<GroupBuyCustomer> {
    @Autowired
    private GroupBuyCustomerDao groupBuyCustomerDao;
    @Override
    protected CrudDao<GroupBuyCustomer> getDao() {
        return groupBuyCustomerDao;
    }
    @Transactional(readOnly = false)
    public void insertGroupBuyCustomer(GroupBuyCustomerDto groupBuyCustomerDto){
        groupBuyCustomerDao.insertGroupBuyCustomer(groupBuyCustomerDto);
    }

    public List<Map<String, Object>> getJoinedCustomers(String groupBuySponsorId){
        return groupBuyCustomerDao.getJoinedCustomers(groupBuySponsorId);
    }

    public List<Map<String,Object>> getGroupBuyInfo(String paymentId){
        return groupBuyCustomerDao.getGroupBuyInfo(paymentId);
    }


    public GroupBuyCustomerDto getGroupBuyCustomer(GroupBuyCustomerDto groupBuyCustomerDto){
        return groupBuyCustomerDao.getGroupBuyCustomer(groupBuyCustomerDto);
    }
    @Transactional(readOnly = false)
    public void updateGroupBuyCustomer(GroupBuyCustomerDto groupBuyCustomerDto){
        groupBuyCustomerDao.updateGroupBuyCustomer(groupBuyCustomerDto);
    }

    public List<GroupBuyCustomer> findListBySponsorId(String sponsorId) {
        return groupBuyCustomerDao.findListBySponsorId(sponsorId);
    }
    public String getShopIdByGroupBuySponsorId(String groupBuySponsorId){
        return groupBuyCustomerDao.getShopIdByGroupBuySponsorId(groupBuySponsorId);
    }

}
