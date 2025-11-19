package com.controlimage.api.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Caminho absoluto — dentro do Docker vira /app/uploads
        Path uploadPath = Paths.get(uploadDir)
                               .toAbsolutePath()
                               .normalize();

        String fullLocation = "file:" + uploadPath.toString() + "/";

        registry.addResourceHandler("/images/**")
                .addResourceLocations(fullLocation);
    }
}
