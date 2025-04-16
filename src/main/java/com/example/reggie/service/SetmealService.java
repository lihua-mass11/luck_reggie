package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.dto.SetmealDto;

import java.util.List;

/**
 * 套餐service接口
 */
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐,同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐,同时需要删除套餐和菜品的关联数据
     * @param Ids
     */
    void removeWithDish(List<Long> Ids);

    /**
     * 根据id批量操作菜品状态
     * @param status
     * @param Ids
     */
    void statusWithDish(Integer status,List<Long> Ids);
}
