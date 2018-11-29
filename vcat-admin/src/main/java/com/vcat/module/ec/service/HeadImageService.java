package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.BgImageDao;
import com.vcat.module.ec.dao.HeadImageDao;
import com.vcat.module.ec.entity.BgImage;
import com.vcat.module.ec.entity.HeadImage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 首页背景图片Service
 */
@Service
@Transactional(readOnly = true)
public class HeadImageService extends CrudService<HeadImageDao, HeadImage> {
    @Transactional(readOnly = false)
    public void activate(HeadImage headImage){
        dao.activate(headImage);
    }
}
