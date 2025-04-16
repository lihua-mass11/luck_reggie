package com.example.reggie.configuration;

import com.example.reggie.controller.filter.LoginCheckFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Servlet的Bean
 */
@Slf4j
@Configuration
public class ServletViewConfig {

    /**
     * 拦截器的注册
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        LoginCheckFilter loginCheckFilter = new LoginCheckFilter();
        filterRegistrationBean.setFilter(loginCheckFilter);
        filterRegistrationBean.addUrlPatterns("/*");

        log.info("拦截器的Bean容器:{}", filterRegistrationBean);
        return filterRegistrationBean;
    }

}
