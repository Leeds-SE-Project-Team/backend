package com.se.backend.interceptor;

//import com.fasterxml.jackson.databind.ObjectMapper;

import com.alibaba.fastjson2.JSON;
import com.se.backend.exceptions.AuthException;
import com.se.backend.models.User;
import com.se.backend.services.TokenService;
import com.se.backend.services.UserService;
import com.se.backend.utils.AdminToken;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


import java.io.IOException;
import java.util.Objects;

@Slf4j
public class JWTInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) throws IOException {
        if (handler instanceof HandlerMethod handlerMethod) {
            // 配置该注解，说明不进行拦截
            IgnoreToken annotation = handlerMethod.getBeanType().getAnnotation(IgnoreToken.class); //从类上获取注解
            if (annotation == null) {
                annotation = handlerMethod.getMethodAnnotation(IgnoreToken.class); // 从方法上获取注解
            }
            if (annotation != null) {
                // 不要求携带Token
                return true;
            }
        }
        // 要求携带Token

        // 获取请求头中的令牌
        String token = request.getHeader("Authorization");
        String id = request.getHeader("User-ID");
//        ObjectMapper objectMapper = new ObjectMapper();
//        Map<String, Object> map = new HashMap<>();

        if (Objects.nonNull(token) && token.equals("root")) {
            // 使用管理员Token
            if (Objects.nonNull(id)) {
                try {
                    request.setAttribute("user", userService.getUserById(Long.valueOf(id)));
                } catch (AuthException e) {
//                    response.setContentType("application/json;charset=UTF-8");
//                    String json = objectMapper.writeValueAsString(ApiResponse.error(e.getMessage()));
                    Object json = JSON.toJSON(ApiResponse.error(e.getMessage()));
                    response.getWriter().println(json);
                    return false;
                }
            }
            return true;
        } else {
            // 使用非管理员Token
            if (handler instanceof HandlerMethod handlerMethod) {
                // 配置该注解，说明不进行拦截
                AdminToken annotation = handlerMethod.getBeanType().getAnnotation(AdminToken.class); //从类上获取注解
                if (annotation == null) {
                    annotation = handlerMethod.getMethodAnnotation(AdminToken.class); // 从方法上获取注解
                }
                if (annotation != null) {
                    // 如果要求使用管理员Token
//                    map.put("state", "false");
//                    map.put("msg", "Invalid token");
                    //将map转为json
//                    String json = new ObjectMapper().writeValueAsString(map);
//                    response.setContentType("application/json;charset=UTF-8");
                    Object json = JSON.toJSON(ApiResponse.error("Invalid token"));
//                    String json = objectMapper.writeValueAsString(ApiResponse.error("Invalid token"));
                    response.getWriter().println(json);
                    return false;
                }
            }
        }

        // 使用非管理员Token 且 不要求使用管理员Token
        try {
            User user = tokenService.getUserByToken(token);
            if (user.getId().toString().equals(id)) {
                request.setAttribute("user", user);
            }
            return true;
        } catch (AuthException e) {
            //将map转为json
//            response.setContentType("application/json;charset=UTF-8");
            Object json = JSON.toJSON(ApiResponse.error(e.getMessage()));
//            String json = objectMapper.writeValueAsString(ApiResponse.error(e.getMessage()));
            response.getWriter().println(json);
            return false;
        }
    }

    @Override
    public void postHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, Exception ex) {
    }
}