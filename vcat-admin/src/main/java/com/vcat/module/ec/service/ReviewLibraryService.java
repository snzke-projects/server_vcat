package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ReviewLibraryDao;
import com.vcat.module.ec.entity.ReviewLibrary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReviewLibraryService extends CrudService<ReviewLibraryDao, ReviewLibrary> {

}
