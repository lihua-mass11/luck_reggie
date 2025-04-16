package com.example.reggie.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.dto.DishDto;
import com.example.reggie.exceptions.BusinessException;
import com.example.reggie.mapper.DishMapper;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import com.example.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author  唐三
 * description: 菜品业务逻辑处理层
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
                        implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品，同时保存对应的口味
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息dish
        this.save(dishDto);

        //获取当前菜品的id信息,最终是自动生成的id
        Long id = dishDto.getId();

        List<DishFlavor> dishFlavors = dishDto.getFlavors();
        dishFlavors = dishFlavors.stream().map(item -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        System.out.println(dishFlavors);
        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(dishFlavors);

    }

    /**
     * 菜品信息修改,返回当前菜品信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        /**
         * 思路:
         *    分析业务需求:最终也面回显,口味信息也要回显,返回DishDto
         *    1,查询当前的菜品id,进行数据拷贝到DishDto,
         *    2,查询菜品口味信息
         *    3,返回DishDto
         */
        //菜品查询
        Dish dish = dishService.getById(id);
        //信息拷贝
        DishDto dishDto =  BeanUtil.copyProperties(dish,DishDto.class);

        List<DishFlavor> dishFlavors = dishFlavorService.list(
                //口味查询
                new LambdaQueryWrapper<DishFlavor>().
                        eq(DishFlavor::getDishId,id)
        );
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }

    /**
     * 菜品修改
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        /**
         * 后台接收到了前端的修改信息
         * 思路处理:
         *   共要调用两张表,dish_flavor,dish
         *    1,数据Dish拷贝,对数据进行修改
         *    2,获取口味集合,数据修改
         */
        //Dish数据拷贝
        dishService.updateById(BeanUtil.copyProperties(dishDto,Dish.class));
        //菜品口味数据拷贝
        //获取要修改的口味所有数据
        dishFlavorService.remove(
                new LambdaQueryWrapper<DishFlavor>().
                        eq(DishFlavor::getDishId,dishDto.getId())
        );

        //进行数据重新插入,但是它不会自己添加当前菜品的Id
        dishFlavorService.saveBatch(
                dishDto.getFlavors().stream().
                        map(item -> {
                            item.setDishId(dishDto.getId());
                            return item;
                        }).collect(Collectors.toList())
        );

        //这条语句会导致只能修改但是无法删除
        //dishFlavorService.updateBatchById(dishDto.getFlavors());

    }

    /**
     * 菜品禁售或起售处理
     * @param status
     * @param dishIds
     */
    @Override
    public void prohibitWithDish(Integer status, List<Long> dishIds) {
        /**
         * 处理思路:
         *   需要调用的数据表dish,setmeal
         *    当我们的数据状态为0停售,1起售
         *    当我们的菜品id自前端传过来
         *    1,判断当前菜品是否在套餐,如果在，响应无法修改信息
         *    2,进行菜品状态修改1,修改为0
         *    我们根据id查询到当前所要批量的菜品
         */
        /**
         * 获取当前菜品的分类id,
         * 根据分类id判断当前菜品是否在套餐中,
         * 在的话抛出异常,不在进行数据修改
         */
        //通过当前dish_id获得菜品,进行状态修改
        List<Dish> dishs = dishService.listByIds(dishIds).
                stream().
                map(item -> {
                    item.setStatus(status);
                    return item;
                }).collect(Collectors.toList());

        System.out.println("集合:" + dishs);

        //进行菜品分类信息id抽取
        List<Long> categoryIds =  dishs.stream().
                distinct().//进行菜品分类去重，他们可能是同一种菜品
                map(Dish::getCategoryId).
                collect(Collectors.toList());

        //判断当前菜品是否在套餐
        List<Long> setmeals = setmealService.list(
                new LambdaQueryWrapper<Setmeal>().
                            eq(Setmeal::getCategoryId,categoryIds)
                ).stream().map(Setmeal::getCategoryId).collect(Collectors.toList());

        //套餐集合是以存在套餐内的菜品分类,直接进行排除
        //进行批量操作,如果id和菜品分类
        if (!setmeals.isEmpty()) {
            throw new BusinessException("当前菜品可能在套餐内");
        }

        dishService.updateBatchById(dishs);

    }

    /**
     * 批量数据删除
     * @param dishIds
     */
    @Override
    public void deleteWithDish(List<Long> dishIds) {
        /**
         * 删除思路:
         *    判断是否在套餐:如果套餐存在这个菜品,
         *    就抛出异常,否则就删除,如果套餐停售无法删除
         *    1,获取当前id的菜品表
         *    2,根据菜品表获取当前的菜品分类
         *    3,对菜品分类的表id进行抽取
         *    4,获取当前套餐表中的数据
         *    5,如果套餐集合为空,删除
         *    6,不为空,抛异常
         */
        //获取当前菜品表
        List<Dish> dishes = dishService.listByIds(dishIds);
        //分类表id抽取
        List<Long> categoryIds = dishes.stream().
                            distinct().
                            map(Dish::getCategoryId).collect(Collectors.toList());
        //获取当前套餐表中的数据
        List<Setmeal> setmeals = setmealService.list(
                new LambdaQueryWrapper<Setmeal>().
                        eq(Setmeal::getCategoryId,categoryIds)
        );

        dishes.forEach(dish -> {
            //判断当前状态是否为停售
            if (Objects.equals(dish.getStatus(),0)) {
                throw new BusinessException("抱歉您的菜品已停售😴");
            }
        });

        //如果套餐集合为空,删除
        if (!setmeals.isEmpty()) {
            throw new BusinessException("你删除的菜品已存在套餐中!!");
        }
        //批量删除
        dishService.removeByIds(dishIds);
    }

}
