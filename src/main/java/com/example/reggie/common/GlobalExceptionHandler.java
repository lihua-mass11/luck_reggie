package com.example.reggie.common;

import com.example.reggie.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
//只对这些异常信息进行处理
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法,当重复添加时会抛出这个异常
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());

        //判断异常信息中是否存在这个
        if (ex.getMessage().contains("Duplicate entry")) {
            //当我么重复输入时
            //Duplicate entry 'tangsan' for key 'employee.idx_username'
            String[] split = ex.getMessage().split(" ");
            String value = split[2];
            String msg =  value.substring(1, value.length()-1)+ " 已存在"  ;
            return R.error(msg);
        }

        return R.error("未知错误");
    }

    /**
     * 业务逻辑异常捕获
     * @param ex
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public R<String> exceptionHandler(BusinessException ex) {
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
