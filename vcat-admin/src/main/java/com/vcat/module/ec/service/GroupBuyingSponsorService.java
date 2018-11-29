package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.GroupBuyingSponsorDao;
import com.vcat.module.ec.entity.GroupBuyingSponsor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GroupBuyingSponsorService extends CrudService<GroupBuyingSponsorDao, GroupBuyingSponsor> {
}
