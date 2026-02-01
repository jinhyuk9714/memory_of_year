package com.demo.album.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS는 SecurityConfig에서 통합 관리합니다.
 * 로컬 스티커 리소스가 필요한 경우 application.yml에
 * app.stickers.local-path 를 설정하면 /stickers/** 에 매핑됩니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.stickers.local-path:}")
    private String stickersLocalPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (StringUtils.hasText(stickersLocalPath)) {
            String path = stickersLocalPath.endsWith("/") ? stickersLocalPath : stickersLocalPath + "/";
            registry.addResourceHandler("/stickers/**")
                    .addResourceLocations("file:" + path);
        }
    }
}
