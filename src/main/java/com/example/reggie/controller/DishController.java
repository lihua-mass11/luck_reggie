package com.example.reggie.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.entity.dto.DishDto;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import com.example.reggie.service.impl.DishFlavorServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 唐三
 * description: 菜品信息表示层
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 菜品分页
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page, Integer pageSize,String name) {
        /**
         * 思路分析:
         *    获取分页信息
         *    进行分页,当name为null时不进行条件构造处理处理
         *    输出数据
         */
        log.info("dish [page: {}, pageSize: {}, name: {}]", page, pageSize, name);
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //分页条件构造
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName, name);
        //根据菜品id进行排序
        dishLambdaQueryWrapper.orderByDesc(Dish::getId);

        //分页
        dishService.page(dishPage, dishLambdaQueryWrapper);

        //对象数据拷贝,但是不能拷贝records这个数据集合封装的是我们查找到的信息
        BeanUtil.copyProperties(dishPage, dishDtoPage,"records");//或略records这个属性
        List<Dish> records = dishPage.getRecords();
        List<DishDto> dtoRecords = records.stream().map(item -> {
            DishDto dto = new DishDto();

            BeanUtil.copyProperties(item,dto);

            Long id = item.getCategoryId();// 分类id
            // 根据id查询分类对象
            Category category = categoryService.getById(id);

            if(category != null) {
                String categoryName = category.getName();
                dto.setCategoryName(categoryName);
            }
            return dto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dtoRecords);
        //返回结果
        return R.success(dishDtoPage);
    }

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    //@RequestBody 一定要有,因为我们的数据式json不是参数类型
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("save dishDto {}", dishDto);
        //表数据封装
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功😁😁");
    }

    /**
     * 菜品信息修改,返回当前菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable("id") Long id) {
        log.info("dish [id: {}]", id);
        return R.success(dishService.getByIdWithFlavor(id));
    }

    /**
     * 数据修改
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("update dishDto {}", dishDto);
        dishService.updateWithFlavor(dishDto);
        return R.success("成功修改❤️❤️");
    }

    /**
     * 菜品状态信息
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(
            @PathVariable("status") Integer status,
            @RequestParam("ids") List<Long> ids
    ) {
        log.info("update dish status [status: {}]", status);
        dishService.prohibitWithDish(status,ids);
        return R.success("成功批量😊😊");
    }

    /**
     * 菜品删除或批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        log.info("delete ids: {}", ids);
        dishService.deleteWithDish(ids);
        return R.success("成功");
    }

    /**
     * 根据条件查询菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(dish),Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        return R.success(dishService.list(queryWrapper));
    }
}
