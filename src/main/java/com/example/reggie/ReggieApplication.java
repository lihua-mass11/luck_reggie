package com.example.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@ServletComponentScan
@SpringBootApplication
@EnableTransactionManagement//开启事务注解
public class ReggieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动成功......");

    }
}
