package com.hcmut.lms.common.helper;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

@Component
public class CurrentUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) &&
                parameter.getParameterType().isAssignableFrom(CurrentUserInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        System.out.println("Resolving Current User Argument");
        //take the request
        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        //take our Authorization header from request and get access token
        String userIdHeader = httpServletRequest.getHeader("x-user-id");
        String userRoleHeader = httpServletRequest.getHeader("x-role");
        return new CurrentUserInfo(UUID.fromString(userIdHeader), userRoleHeader);
    }
}
