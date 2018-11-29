package com.vcat.module.core.service;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.dao.OfficeDao;
import com.vcat.module.core.entity.Office;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.ec.service.DistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 机构Service
 */
@Service
@Transactional(readOnly = true)
public class OfficeService extends TreeService<OfficeDao, Office> {
	@Autowired
	private DistributionService distributionService;

	public List<Office> findAll(){
		return UserUtils.getOfficeList();
	}

	public List<Office> findList(Boolean isAll){
		if (isAll != null && isAll){
			return UserUtils.getOfficeAllList();
		}else{
			return UserUtils.getOfficeList();
		}
	}
	
	@Transactional(readOnly = true)
	public List<Office> findList(Office office){
        String parentIds = office.getParentIds();
		office.setParentIds(StringUtils.isEmpty(parentIds) || "null".equalsIgnoreCase(parentIds) ? "%" : parentIds + "%");
		return dao.findByParentIdsLike(office);
	}
	
	@Transactional(readOnly = false)
	public void save(Office office) {
		super.save(office);
        distributionService.deleteOfficeDistribution(office.getId());
        distributionService.insertOfficeDistribution(office.getId(), office.getDistributionIds());
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}
	
	@Transactional(readOnly = false)
	public void delete(Office office) {
		super.delete(office);
        distributionService.deleteOfficeDistribution(office.getId());
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}
	
}

