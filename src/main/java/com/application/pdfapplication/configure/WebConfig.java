package com.application.pdfapplication.configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 全てのパスに対してCORSを適用
                .allowedOrigins("*")  // 全てのオリジンを許可
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // 許可するHTTPメソッドを指定
                .allowedHeaders("*")  // 許可するヘッダーを指定
                .maxAge(3600);  // キャッシュする秒数
    }
}
