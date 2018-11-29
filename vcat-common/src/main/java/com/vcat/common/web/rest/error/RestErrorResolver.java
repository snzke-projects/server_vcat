package com.vcat.common.web.rest.error;

import org.springframework.web.context.request.ServletWebRequest;

public interface RestErrorResolver {
    RestError resolveError(ServletWebRequest request, Object handler, Exception ex);
}
