package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.exceptions.BusinessException;
import com.example.reggie.mapper.CategoryMapper;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishService;
import com.example.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.stereotype.Service;

/**
 * @author 唐三
 * description: 菜品分类业务逻辑层
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
                    implements CategoryService {

    //菜品
    @Autowired
    private DishService dishService;

    //套餐
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类,删除之前要进行判断
     * 当前的分类,可能关联了许多信息
     * 它可能在某个菜品,也可能在某个套餐
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //查询当前分类是否关联了菜品,如果已经关联我们抛出业务逻辑异常
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        System.out.println("id:" + id);
        int count_dish = dishService.count(dishLambdaQueryWrapper);
        //关联了10个菜品
        if (count_dish > 0) {
            //抛出业务异常
            throw new BusinessException("当前分类下关联了菜品😋😋");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //查询分类是否关联了套餐,如果以关联,抛出一个业务异常
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count_setmeal = setmealService.count(setmealLambdaQueryWrapper);
        if (count_setmeal > 0) {
            //抛出业务逻辑异常
            throw new BusinessException("当前分类下关联了套餐😋😋");
        }

        //正常删除分类
        baseMapper.deleteById(id);
    }
}
