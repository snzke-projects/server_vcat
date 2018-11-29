package com.vcat.api.web.validation;

import com.vcat.common.mapper.JsonMapper;
import com.vcat.common.utils.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * Created by Dean on 16/1/30.
 */
public class JsonStringValidator implements ConstraintValidator<JsonString, String> {

    @Override
    public void initialize(JsonString validJsonParam) {
        validJsonParam.value();
    }

    @Override
    public boolean isValid(String param, ConstraintValidatorContext constraintValidatorContext) {
        if(StringUtils.isNullBlank(param)){
            return false;
        }
        try {
            JsonMapper.getInstance().fromJson(param, Map.class);
        } catch (Exception e){
            return false;
        }
        return true;
    }
}

