package com.vcat.module.ec.service;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.Role;
import com.vcat.module.core.entity.User;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.ec.dao.DistributionDao;
import com.vcat.module.ec.entity.Distribution;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DistributionService extends CrudService<DistributionDao, Distribution> {
    @Override
    @Transactional(readOnly = false)
    public void save(Distribution entity) {
        super.save(entity);

        addToRoleOffice(entity.getId());
    }

    /**
     * 将当前供应商添加到当前用户拥有供应商权限的角色机构中
     * @param DistributionId 供应商ID
     */
    private void addToRoleOffice(String DistributionId){
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        if(!user.isAdmin() && null != roleList && roleList.size() > 0){
            StringBuffer sb = new StringBuffer();
            for (Role role : roleList){
                String officeId = role.getOffice().getId();
                Boolean hasRole = dao.hasDistributionRole(officeId);
                if(null != hasRole && hasRole && sb.indexOf(officeId) < 0){
                    sb.append(officeId + ",");
                    insertOfficeDistribution(officeId, DistributionId);
                }
            }
        }
    }

    @Transactional(readOnly = false)
    public void insertOfficeDistribution(String officeId, String distributionIds){
        if(StringUtils.isEmpty(distributionIds)){
            return;
        }
        distributionIds = "'"+ distributionIds +"'";
        distributionIds = distributionIds.replaceAll(",","','");
        Distribution distribution = new Distribution();
        distribution.preInsert();
        distribution.getSqlMap().put("officeId", officeId);
        distribution.getSqlMap().put("distributionIds", distributionIds);
        dao.insertOfficeDistribution(distribution);
    }

    @Transactional(readOnly = false)
    public void deleteOfficeDistribution(String officeId){
        dao.deleteOfficeDistribution(officeId);
    }

    public List<Distribution> findListByOfficeId(String officeId){
        return dao.findListByOfficeId(officeId);
    }
}
