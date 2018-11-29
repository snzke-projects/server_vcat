package com.vcat.module.ec.service;

import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.BaseEntity;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.DataChangeLogDao;
import com.vcat.module.ec.entity.DataChangeLog;
import com.vcat.module.ec.entity.DataChangeLogField;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Date;

@Service
@Transactional(readOnly = true)
public class DataChangeLogService extends CrudService<DataChangeLogDao, DataChangeLog> {

    public <T extends BaseEntity> void saveLog(String tableName, String changeSource, T before, T after) {
        if (null == before || null == after
                || StringUtils.isEmpty(before.getId())
                || StringUtils.isEmpty(after.getId())
                || !before.getId().equals(after.getId())) {
            return;
        }
        Class<?> compareClass = before.getClass();
        String entityName = compareClass.getName();
        String associationId = before.getId();
        String changeNo = IdGen.uuid();
        String changeBy = before.getCurrentUser().getId();
        Date changeDate = new Date();

        Field[] fieldArray = compareClass.getDeclaredFields();
        for (int i = 0; i < fieldArray.length; i++) {
            Field field = fieldArray[i];
            DataChangeLogField dataChangeLogField = field.getAnnotation(DataChangeLogField.class);
            if(dataChangeLogField == null){
                continue;
            }
            String fieldName = field.getName();
            String fieldTitle = dataChangeLogField.title();
            Class fieldType = field.getType();
            field.setAccessible(true);
            Object beforeValue = ReflectionUtils.getField(field, before);
            Object afterValue = ReflectionUtils.getField(field, after);
            if (hasDifference(beforeValue,afterValue)) {
                dao.insert(DataChangeLog.create(tableName, entityName, associationId, changeNo, fieldName, fieldTitle, fieldType.getName(),
                        beforeValue, afterValue, changeSource, changeBy, changeDate));
            }
        }
    }

    private <T> boolean hasDifference(T beforeValue, T afterValue){
        if((null == beforeValue && null != afterValue)
                || (null != beforeValue && null == afterValue)){
            return true;
        }
        if(null == beforeValue && null == afterValue){
            return false;
        }
        if(beforeValue instanceof Number){
            return ((Number) beforeValue).doubleValue() != ((Number) afterValue).doubleValue();
        }else{
            return !beforeValue.equals(afterValue);
        }
    }
}
