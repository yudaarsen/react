package ru.gadzhiev.mirea_project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gadzhiev.mirea_project.filters.AuthFilter;
import ru.gadzhiev.mirea_project.filters.CorsFilter;

@Configuration
public class AuthConfig {

    @Autowired
    private AuthFilter authFilter;

    @Autowired
    private CorsFilter corsFilter;

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterReg(){
        FilterRegistrationBean<AuthFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(authFilter);
        registrationBean.addUrlPatterns("/api/appeal/*");
        registrationBean.addUrlPatterns("/api/appeals");
        registrationBean.addUrlPatterns("/api/categories");
        registrationBean.addUrlPatterns("/api/employees");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterReg() {
        FilterRegistrationBean<CorsFilter> corsFilterBean = new FilterRegistrationBean<>();
        corsFilterBean.setFilter(corsFilter);
        corsFilterBean.addUrlPatterns("/*");
        corsFilterBean.setOrder(1);
        return corsFilterBean;
    }
}
