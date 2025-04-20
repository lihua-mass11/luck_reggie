package com.example.reggie.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import com.example.reggie.entity.dto.SetmealDishVO;
import com.example.reggie.entity.dto.SetmealDto;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishService;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import com.example.reggie.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    /**
     * 套餐分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(Integer page,Integer pageSize,String name) {
        log.info("page: {}, pageSize: {},name: {}", page, pageSize,name);

        /**
         * 思路分析:
         *    1,先分页,条件构造判断条件,名称不为空
         */
        Page<Setmeal> info = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //条件构造
        setmealService.page(
                info,
                new LambdaQueryWrapper<Setmeal>().
                        like(StringUtils.isNotEmpty(name), Setmeal::getName, name).
                        orderByDesc(Setmeal::getUpdateTime)
        );

        //数据对象拷贝
        BeanUtil.copyProperties(info, setmealDtoPage,"records");

        List<SetmealDto> setmealDtos = info.getRecords().stream().
                        map(item -> {
                            SetmealDto setmealDto = new SetmealDto();
                            //分类Id
                            Long categoryId = item.getCategoryId();
                            //根据分类id查询分类对象
                            Category category = categoryService.getById(categoryId);
                            if (!Objects.isNull(category)) {
                                String categoryName = category.getName();
                                setmealDto.setCategoryName(categoryName);
                            }
                            BeanUtil.copyProperties(item, setmealDto);
                            return setmealDto;
                        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtos);

        //如果数据为空,抛出异常
        if (info.getTotal() <= 0) {
            R.error("抱歉您的数据为空😭😭");
        }
        return R.success(setmealDtoPage);
    }


    /**
     * 添加套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息: {}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功😊😊");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("接口/setmeal/ => ids: {}", ids);
        setmealService.removeWithDish(ids);
        return R.success("成功");
    }

    @PostMapping("/status/{status}")
    public R<String> status(
            @PathVariable("status") Integer id,
            @RequestParam List<Long> ids
            ) {
        log.info("接口:/status/{}",id);
        log.info("status: {}, ids: {}", id, ids);

        setmealService.statusWithDish(id, ids);
        return R.success("批量成功😊😊");
    }

    /**
     * 套餐修改,根据id进行数据回调
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> awareSetmeal(@PathVariable Long id) {
        //根据分类Id查询套餐
        Setmeal setmeal = setmealService.getById(id);

        //根据套餐Id获取菜品信息
        List<SetmealDish> setmealDishs = setmealDishService.list(
                new LambdaQueryWrapper<SetmealDish>().
                            eq(SetmealDish::getSetmealId,setmeal.getId())
        );

        //Dto封装
        SetmealDto setmealDto = BeanUtil.copyProperties(setmeal,SetmealDto.class);
        setmealDto.setSetmealDishes(setmealDishs);
        return R.success(setmealDto);
    }

    @GetMapping("/list")
    public R<List<SetmealDto>> list(
            @RequestParam("categoryId") Long id,
            @RequestParam("status") Integer status
    ) {
        log.info("菜品分类Id: {}, 状态: {}",id,status);
        /**
         * 获取当前分类的菜品
         */
        List<Setmeal> setmeals = setmealService.list(
                new LambdaQueryWrapper<Setmeal>().
                            eq(Setmeal::getCategoryId,id).
                            eq(Setmeal::getStatus,status)
        );

        List<SetmealDto> setmealDtos = BeanUtil.copyToList(setmeals,SetmealDto.class).
                    stream().map(item -> {
                        //对套餐的菜品进新货发你如果装
                        List<SetmealDish> setmealDishes = setmealDishService.list(
                                new LambdaQueryWrapper<SetmealDish>().
                                            eq(SetmealDish::getSetmealId,item.getId())
                        );
                        item.setSetmealDishes(setmealDishes);

                        return item;
                    }).collect(Collectors.toList());
        System.out.println("set:" + setmealDtos);
        return R.success(setmealDtos);
    }

    @GetMapping("/dish/{id}")
    public R<SetmealDto> withSetmeal(@PathVariable Long id) {
        //获取当前套餐
        Setmeal setmeal = setmealService.getById(id);
        //获取当前分类名称
        Category category = categoryService.getById(setmeal.getCategoryId());

        //获取当前套餐的菜品
        List<SetmealDish> setmealDishs = setmealDishService.list(
                new LambdaQueryWrapper<SetmealDish>().
                        eq(SetmealDish::getSetmealId,id)
        );

        //用户返回vo
        List<SetmealDishVO> setmealDishVOS = setmealDishs.stream().map(item -> {
            SetmealDishVO setmealDishVO = BeanUtil.copyProperties(item, SetmealDishVO.class);
            //获取该菜品图片
            String image = dishService.getById(item.getDishId()).getImage();
            setmealDishVO.setImage(image);
            return setmealDishVO;
        }).collect(Collectors.toList());


        SetmealDto setmealDto = BeanUtil.copyProperties(setmeal,SetmealDto.class);

        setmealDto.setSetmealDisheVOS(setmealDishVOS);
        setmealDto.setCategoryName(category.getName());
        System.out.println("对象参数:" + setmealDto);

        return R.success(setmealDto);
    }
}
