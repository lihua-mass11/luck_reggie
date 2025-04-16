package com.example.reggie.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import com.example.reggie.entity.dto.SetmealDto;
import com.example.reggie.exceptions.BusinessException;
import com.example.reggie.mapper.SetmealMapper;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author  唐三
 * descripyion: 套餐业务逻辑处理层
 */
@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
                            implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐,同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息,操作setmeal,执行insert操作
        this.save(setmealDto);

        //保存套餐和菜品的关联信息,操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(
                setmealDto.getSetmealDishes().stream().
                        map(item -> {
                            item.setSetmealId(setmealDto.getId());
                            return item;
                        }).collect(Collectors.toList())
        );
    }

    /**
     * 删除套餐,同时需要删除套餐和菜品的关联数据
     * @param Ids
     */
    @Override
    public void removeWithDish(List<Long> Ids) {
        //sql:SELECT COUNT(*) FROM setmeal WHERE id IN (1,...) AND status = 1;
        //查询套餐状态,确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,Ids).
                eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);
        if (count > 0) {
            //如果不能删除,抛出一个业务逻辑异常
            throw new BusinessException("套餐正在售卖中,不能删除😖");
        }

        //如果可以删除,先删除套餐表中的数据
        this.removeByIds(Ids);

        //删除关系表中的数据----setmeal_dish
        //根据当前套餐关联菜品的id进行菜品关联删除
        LambdaQueryWrapper<SetmealDish> queryWrapperDish = new LambdaQueryWrapper<>();
        queryWrapperDish.in(SetmealDish::getSetmealId,Ids);

        setmealDishService.remove(queryWrapperDish);

    }

    /**
     * 根据id批量操作菜品状态
     * @param status
     * @param Ids
     */
    @Override
    public void statusWithDish(Integer status, List<Long> Ids) {
        /**
         * 判断思路:
         *     套餐停售与菜品与其他菜品无关
         *      需要操作setmeal
         *      1,将状态改变
         */
        //获取所有当前ID的数据,并对其状态进行修改
        List<Setmeal> setmeals = this.listByIds(Ids).stream().
                                map(item -> {
                                    item.setStatus(status);
                                    return item;
                                }).collect(Collectors.toList());

        //批量更新
        this.updateBatchById(setmeals);
    }

}
