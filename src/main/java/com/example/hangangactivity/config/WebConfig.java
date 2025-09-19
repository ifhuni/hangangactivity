package com.example.hangangactivity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CompanyAuthInterceptor companyAuthInterceptor;

    public WebConfig(CompanyAuthInterceptor companyAuthInterceptor) {
        this.companyAuthInterceptor = companyAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(companyAuthInterceptor)
                .addPathPatterns("/fragments/company-dashboard");
    }
}
