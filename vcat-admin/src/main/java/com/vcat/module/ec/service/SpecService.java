package com.vcat.module.ec.service;

import com.google.common.collect.Sets;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.ProductCategoryDao;
import com.vcat.module.ec.dao.SpecDao;
import com.vcat.module.ec.entity.ProductCategory;
import com.vcat.module.ec.entity.Spec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 规格属性Service
 */
@Service
@Transactional(readOnly = true)
public class SpecService extends CrudService<SpecDao, Spec> {
    @Autowired
    private ProductCategoryDao categoryDao;
    @Transactional(readOnly = false)
    public void deleteByProductId(String productId) {
        dao.deleteByProductId(productId);
    }

    @Transactional(readOnly = false)
    public void deleteByProductItemId(String productItemId) {
        dao.deleteByProductItemId(productItemId);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Spec entity) {
        if(entity.getIsNewRecord()){
            Spec oldEntity = dao.getByName(entity);
            if(null != oldEntity && StringUtils.isNotEmpty(oldEntity.getId())){
                entity.setId(oldEntity.getId());
                addSpecValue(entity);
            }
        }
        super.save(entity);
    }

    public void insertSpecValue(Spec spec){
        spec.preInsert();
        dao.insertSpecValue(spec);
    }

    public String[] getSpecNameArrayByProduct(String id) {
        return dao.getSpecNameArrayByProduct(id);
    }

    @Override
    public List<Spec> findList(Spec entity) {
        List<Spec> list = new ArrayList<>();
        if(null != entity.getCategory()){
            list.addAll(findListByCategory(entity.getCategory(),true));
        }
        Collections.sort(list, (Spec o1, Spec o2) -> -1);   // 倒序该集合
        return list;
    }

    private List<Spec> findListByCategory(ProductCategory category,boolean editable) {
        List<Spec> list = dao.findListByCategory(category,editable);
        if(null != category && !category.getIsRoot()){
            list.addAll(findListByCategory(categoryDao.findParent(category),false));
        }
        return list;
    }

    @Transactional(readOnly = false)
    public void addSpecValue(Spec spec) {
        String id = spec.getId();
        String specValue = spec.getValue();
        if(StringUtils.isEmpty(id) || StringUtils.isEmpty(specValue)){
            return;
        }
        Spec source = get(spec);
        String[] sourceArray = source.getValueArray();
        String[] valueArray = spec.getValueArray();
        Set<String> valueSet = Sets.newLinkedHashSet();
        for (int i = 0; i < sourceArray.length; i++) {
            valueSet.add(sourceArray[i]);
        }
        for (int i = 0; i < valueArray.length; i++) {
            valueSet.add(valueArray[i]);
        }

        spec.setValue(StringUtils.join(valueSet.toArray(),","));
        dao.addSpecValue(spec);
    }
}
