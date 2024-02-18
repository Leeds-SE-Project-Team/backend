package com.se.backend.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.backend.services.TokenService;
import com.se.backend.utils.IgnoreToken;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JWTInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            // 配置该注解，说明不进行拦截
            IgnoreToken annotation = handlerMethod.getBeanType().getAnnotation(IgnoreToken.class); //从类上获取注解
            if (annotation == null) {
                annotation = handlerMethod.getMethodAnnotation(IgnoreToken.class); // 从方法上获取注解
            }
            if (annotation != null) {
                return true;
            }
        }

        Map<String, Object> map = new HashMap<>();
        // 获取请求头中的令牌
        String token = request.getHeader("Authorization");
        if (tokenService.validateToken(token)) {
            log.info("valid token");
            return true;
        }

        map.put("state", "false");
        map.put("msg", "token无效");
        //将map转为json
        String json = new ObjectMapper().writeValueAsString(map);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
        return false;
    }

    @Override
    public void postHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, Exception ex) {
    }
}