package com.se.backend.interceptor;

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
        // 不需要验证Token
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

        // 需要验证Token，获取请求头中的令牌和操作对象
        String token = request.getHeader("Authorization");
        String id = request.getHeader("User-ID");

        // 如果使用了管理员 token, 则无条件允许
        if (Objects.nonNull(token) && token.equals("root")) {
            // 使用管理员Token
            if (Objects.nonNull(id)) {
                try {
                    request.setAttribute("user", userService.getUserById(Long.valueOf(id)));
                } catch (AuthException e) {
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
                    Object json = JSON.toJSON(ApiResponse.error("Invalid token"));
                    response.getWriter().println(json);
                    return false;
                }
            }
        }

        // 非管理员，则需要验证传入的token，其对应的用户是否具有该参数下的接口调用权限
        try {
            User user = tokenService.getUserByToken(token);
            if (user.getId().toString().equals(id)) {
                request.setAttribute("user", user);
            }
            return true;
        } catch (AuthException e) {
            //将map转为json
            Object json = JSON.toJSON(ApiResponse.error(e.getMessage()));
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