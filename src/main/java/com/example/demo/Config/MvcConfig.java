package com.example.demo.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    private final AdminInterceptor adminInterceptor;
    private final UserInterceptor userInterceptor;
    @Autowired
    public MvcConfig(AdminInterceptor adminInterceptor, UserInterceptor userInterceptor) {
        this.adminInterceptor = adminInterceptor;
        this.userInterceptor = userInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(adminInterceptor);
        registry.addInterceptor(userInterceptor);
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path bookUploadDir = Paths.get("book-photos");
        String bookUploadPath = bookUploadDir.toAbsolutePath().normalize().toString();
        registry.addResourceHandler("/book-photos/**").addResourceLocations("file:" + bookUploadPath + "/");
    }

}
