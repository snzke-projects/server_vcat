package com.vcat.api.web.exception;

import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.module.core.entity.MsgEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ylin on 2016/2/18.
 */
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.info(ex.getClass().getName());
        logger.error("ERROR - message:"+ex.getLocalizedMessage());
        logger.error("ERROR - cause:"+ex.getCause(), ex);
        final MsgEntity apiError = new MsgEntity("出错啦，请联系V猫小店", 500);
        return super.handleExceptionInternal(ex, apiError, headers, HttpStatus.OK, request);
    }

    // 400
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        final List<String> errors = new ArrayList<String>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ":" + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ":" + error.getDefaultMessage());
        }
        final MsgEntity apiError = new MsgEntity( errors.toString(), ApiMsgConstants.FAILED_CODE);
        return handleExceptionInternal(ex, apiError, headers, HttpStatus.OK, request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        final List<String> errors = new ArrayList<String>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        final MsgEntity apiError = new MsgEntity(ex.getLocalizedMessage() + errors.toString(), ApiMsgConstants.FAILED_CODE);
        return handleExceptionInternal(ex, apiError, headers, HttpStatus.OK, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        final String error = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type " + ex.getRequiredType();
        final MsgEntity apiError = new MsgEntity(error.toString(), ApiMsgConstants.FAILED_CODE);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        final String error = ex.getRequestPartName() + " part is missing";
        final MsgEntity apiError = new MsgEntity(error.toString(), ApiMsgConstants.FAILED_CODE);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        final String error = ex.getParameterName() + " parameter is missing";
        final MsgEntity apiError = new MsgEntity(error.toString(), ApiMsgConstants.FAILED_CODE);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        final String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();
        final MsgEntity apiError = new MsgEntity(error.toString(), ApiMsgConstants.FAILED_CODE);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        final List<String> errors = new ArrayList<String>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
        }
        final MsgEntity apiError = new MsgEntity(errors.toString(), ApiMsgConstants.FAILED_CODE);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.info(ex.getClass().getName());
        final MsgEntity apiError = new MsgEntity(ex.getLocalizedMessage(), ApiMsgConstants.FAILED_CODE);
        return this.handleExceptionInternal(ex, apiError, headers, HttpStatus.OK, request);
    }

    // 404
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        final String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        final MsgEntity apiError = new MsgEntity(error.toString(), ApiMsgConstants.NOTFIND_CODE);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

    // 405
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));
        final MsgEntity apiError = new MsgEntity(builder.toString(), 405);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

    // 415
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));
        final MsgEntity apiError = new MsgEntity(builder.substring(0, builder.length() - 2), 415);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

    // 500
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        logger.error("ERROR - message:"+ex.getLocalizedMessage());
        logger.error("ERROR - cause:"+ex.getCause(), ex);
        final MsgEntity apiError = new MsgEntity("出错啦，请联系V猫小店", 500);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

}