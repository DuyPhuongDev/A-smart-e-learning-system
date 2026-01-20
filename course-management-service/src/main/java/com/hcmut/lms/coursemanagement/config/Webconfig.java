package com.hcmut.lms.coursemanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Webconfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Áp dụng cho TẤT CẢ các API
                // 1. Cho phép các nguồn (Frontend) nào được gọi
                // Bạn nên liệt kê cụ thể port của FE (ví dụ 3000, 3001)
                // Dùng "*" nếu muốn chấp nhận tất cả (không khuyến khích nếu có Auth)
                .allowedOrigins("http://localhost:3000", "http://localhost:4200")

                // 2. Cho phép các method nào
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")

                // 3. Cho phép các header nào (Auth, Content-Type,...)
                .allowedHeaders("*")

                // 4. Cho phép gửi kèm cookie/credential (quan trọng nếu dùng JWT/Session)
                .allowCredentials(true)

                // 5. Cache lại cấu hình này trong bao lâu (đỡ phải hỏi đi hỏi lại)
                .maxAge(3600);
    }
}
