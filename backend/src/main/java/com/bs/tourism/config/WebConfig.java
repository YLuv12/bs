package com.bs.tourism.config;

import com.bs.tourism.interceptor.AdminAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path:uploads}")
    private String uploadPath;

    @Autowired
    private AdminAuthInterceptor adminAuthInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        String location = uploadDir.toUri().toString();
        
        System.out.println("Static resource mapping: /uploads/** -> " + location);
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);

        registry.addResourceHandler("/admin/static/**")
                .addResourceLocations("classpath:/static/admin/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/auth/login",
                        "/admin/setup/**",
                        "/admin/page/login",
                        "/admin/page/login/**",
                        "/admin/static/**",
                        "/admin/upload/**");
    }
}
