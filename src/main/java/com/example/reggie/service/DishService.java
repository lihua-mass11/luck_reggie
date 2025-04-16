package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.dto.DishDto;

import java.util.List;

/**
 * 菜品service接口
 */
public interface DishService extends IService<Dish> {

    //新增菜品,同时插入菜品对应的口味数据,需要操作两张表: dish,dish_flavor
    void saveWithFlavor(DishDto dishDto);

    //菜品信息修改,返回当前菜品信息
    DishDto getByIdWithFlavor(Long id);

    //菜品信息修改
    void updateWithFlavor(DishDto dishDto);

    //菜品批量禁售或起售
    void prohibitWithDish(Integer status, List<Long> dishIds);

    //批量删除
    void deleteWithDish(List<Long> dishIds);
}
