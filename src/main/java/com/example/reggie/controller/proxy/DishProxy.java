package com.example.reggie.controller.proxy;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(0)
@Aspect
@Component
public class DishProxy {

    //JDK类型的AOP必须为接口进行切面
    @Pointcut("execution(*  com.example.reggie.service.DishService.saveWithFlavor(..))")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void before() {
        System.out.println("运行开启");
    }

    //进行环绕
    @Around("pointCut()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("----------------------- around before --------------------------");
        joinPoint.proceed();//进行放行
        System.out.println("----------------------- around after --------------------------");
    }

    @AfterThrowing("pointCut()")
    public void afterThrowing() {
        System.out.println("抛出异常...");
    }

    @AfterReturning("pointCut()")
    public void afterReturning() {
        System.out.println("之后进行运行...");
    }
}
