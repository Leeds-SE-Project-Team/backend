package com.se.backend.config;

import com.se.backend.interceptor.JWTInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Bean
    public JWTInterceptor requestHandlerInterceptor() {
        return new JWTInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(requestHandlerInterceptor()).addPathPatterns("/**");
//                .excludePathPatterns("/users");
//        registry.addInterceptor(new JWTInterceptor())
//                //拦截
//                .addPathPatterns("/users")
//                //放行
//                .excludePathPatterns("/user/login");

//        @Override
//        public void addInterceptors(InterceptorRegistry registry) {
//            registry.addInterceptor(new MyInterceptor())
//                    .addPathPatterns("/**") // 拦截所有请求
//                    .excludePathPatterns("/api/specific/**") // 排除特定路径
//                    .excludePathPatterns(HttpMethod.GET, "/api/specific/**"); // 对特定路径下的GET请求不进行拦截
//        }
    }
}