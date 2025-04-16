package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Employee;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.mapper.SetmealMapper;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;

import java.util.List;

/**
 * @author 唐三
 * discription: 菜品分类表示层
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("category:{}", category);
        categoryService.save(category);
        return R.success("新增菜品成功😘😘");
    }

    @GetMapping("/page")
    public R<Page> page(
            @RequestParam(value = "page",required = false) int page,
            @RequestParam(value = "pageSize",required = false) int pageSize
    ) {
        /**
         * 思路分析:
         *    我们接口接收到分页信息
         *    判断集合数据是否为空
         *    不为空进行页面分页
         *    进行数据返回
         */
        //先分页
        Page<Category> pageInfo = new Page<>(page, pageSize);

        //进行排序(条件构造实现)
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Category::getSort);
        //获取集合数据
        categoryService.page(pageInfo);
        System.out.println("page值:" + pageInfo.getRecords());

        //如果数据为空,返回异常信息
        if (pageInfo.getTotal() <= 0) {
            return R.error("抱歉您的数据为空😴😴");
        }

        return R.success(pageInfo);
    }

    /**
     * 我们在想菜品与套餐都存在什么关联 菜品与套餐
     */
    /**
     * 菜品删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids) {
        log.info("删除分类,id为: {}",ids);
        categoryService.remove(ids);
        return R.success("恭喜您成功删除菜品😊");
    }

    /**
     * 根据id修改修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("category:{}", category);
        categoryService.updateById(category);
        return R.success("成功修改数据😋😋");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        log.info("category:{}", category);
        //条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        //添加条件
        wrapper.eq(category.getType() != null,Category::getType, category.getType());
        //添加排序条件
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(wrapper);

        return R.success(list);
    }

}
