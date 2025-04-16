package com.example.reggie.configuration;

import com.alibaba.fastjson.support.spring.messaging.MappingFastJsonMessageConverter;
import com.example.reggie.common.JacksonObjectMapper;
import com.example.reggie.controller.interceptors.LoginInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
@MapperScan("com.example.reggie.mapper")
public class WebMvcConfiguration {

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {

            //静态资源页面配置
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                log.info("开始进行静态资源映射......");
                registry.addResourceHandler("/backend/**").addResourceLocations("classpath:backend/");
                registry.addResourceHandler("/front/**").addResourceLocations("classpath:front/");
            }

            //拦截器

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                //registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**").
                        //excludePathPatterns("/backend/api/**","/backend/images/**","/backend/js/**",
                                //"/backend/plugins/**","/backend/styles/**","/backend/page/login/login.html","/employee/login");
            }

            //扩展mvc框架的消息转换器

            @Override
            public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
                log.info("扩展消息转换器......");
                //json的转换器
                MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
                //设置对象转换器,底层使用Jackson将java对象转为json
                mappingJackson2HttpMessageConverter.setObjectMapper(new JacksonObjectMapper());
                //将上面的消息转换器最佳到转换器中
                //参数1 :转换器容器的索引,我们让他优先执行我们的
                //参数2 :封装我们的转换器
                converters.add(0,mappingJackson2HttpMessageConverter);
            }
        };
    }
}
