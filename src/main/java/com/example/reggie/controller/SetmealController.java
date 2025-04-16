package com.example.reggie.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.Steam;
import com.example.reggie.common.R;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import com.example.reggie.entity.dto.SetmealDto;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.mockito.internal.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
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
}
