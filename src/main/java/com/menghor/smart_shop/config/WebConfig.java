package com.menghor.smart_shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // Add your Next.js app URL here bro
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // Important for cookies/auth
    }


    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configure) {
        configure
                .defaultContentType(MediaType.APPLICATION_JSON)
                .ignoreAcceptHeader(false)
                .useRegisteredExtensionsOnly(false);
    }
}
