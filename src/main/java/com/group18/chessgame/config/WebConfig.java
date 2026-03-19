package com.group18.chessgame.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final AdminInterceptor adminInterceptor;
    private final BanCheckInterceptor banCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(banCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/webjars/**", "/ws/**", "/login", "/register");

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**");
    }
}
