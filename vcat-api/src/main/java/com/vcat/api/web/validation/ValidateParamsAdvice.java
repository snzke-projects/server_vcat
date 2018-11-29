package com.vcat.api.web.validation;

/**
 * Created by ylin on 2016/2/19.
 */
import java.lang.reflect.Method;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ValidateParamsAdvice {

    private final ValidatorFactory validatorFactory;


    @Autowired
    public ValidateParamsAdvice(final ValidatorFactory jsr303ValidationFactory) {
        this.validatorFactory = jsr303ValidationFactory;
    }

    @Before("execution(public * com.vcat.api.web.*.*(..)) && @annotation(com.vcat.api.web.validation.ValidateParams)")
    public void validateMethodParams(final JoinPoint joinPoint) {
        final Object controller = joinPoint.getThis();
        final Method controllerMethod = ((MethodSignature)joinPoint.getSignature()).getMethod();
        final Object[] params = joinPoint.getArgs();

        final ExecutableValidator executableValidator = this.validatorFactory.getValidator().forExecutables();
        final Set<ConstraintViolation<Object>> violations =
                executableValidator.validateParameters(controller, controllerMethod, params);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}