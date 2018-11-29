package com.vcat.api.exception;

/**
 * Created by Administrator on 2015/7/4.
 */
public class ApiException extends RuntimeException {
    public ApiException() {
    }

    public ApiException(String message) {
        super(message);
    }
}
