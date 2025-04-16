package com.example.reggie.controller.filter;

import cn.hutool.core.text.AntPathMatcher;
import com.alibaba.fastjson.JSON;
import com.example.reggie.utils.BaseContext;
import com.example.reggie.common.R;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 检查我们是否以及完成登录
 */
@Slf4j
//@WebFilter(filterName ="loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器,支持通配符
    private final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("拦截到请求:{}","");
        //System.out.println("过滤器启动");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        /**
         * 思路
         * 1,获取请求的uri地址
         * 2,判断本次请求是否需要处理
         * 3,如果不需要处理,则直接放行
         * 4,判断登录状态,如果已经登录直接放行
         * 5,如果未登录返回登录界面
         */
        // 1,获取请求的uri地址
        String requestURI = request.getRequestURI();
        log.info("拦截到请求:{}",requestURI);

        //定义不需要处理的路径
        String[] urls = new String[]{
          "/employee/login",
          "/employee/logout",
          "/backend/**",//放行这个路径下的所有静态资源  与@WebFilter注解上的/*不一样
          "/front/**"
        };
        ///backend/** 相当于  /backend/ss/

        // 2,判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        // 3,如果不需要处理,则直接放行
        if (check) {
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request ,response);
            return;
        }

        // 4,判断登录状态,如果已经登录直接放行
        if (!Objects.isNull(request.getSession().getAttribute("employee"))) {
            log.info("用户已登陆,用户id: {}" , request.getSession().getId());

            long id = Thread.currentThread().getId();
            log.info("线程id: {}",id);
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));

            filterChain.doFilter(request , response);
            return;
        }

        log.info("用户未登录");
        // 5,如果未登录返回登录界面,通过输出流方式向客户端响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    /**
     * 路径匹配判断
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI) {
        //对我们所有放行的地址进行匹配
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            //只要有一条路径匹配,就放行,如果直接返回match相对每条路径都需要匹配
            if (match) {
                return true;
            }
        }
        return false;
    }
}
