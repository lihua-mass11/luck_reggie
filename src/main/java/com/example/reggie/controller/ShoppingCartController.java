package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.reggie.common.R;
import com.example.reggie.entity.ShoppingCart;
import com.example.reggie.service.ShoppingCartService;
import com.example.reggie.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author 唐三
 * description: 移动端购物车,业务逻辑展示层
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 遍历
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("购物车加载:{}","/shoppingCart/list");
        return R.success(shoppingCartService.list(
                new LambdaQueryWrapper<ShoppingCart>().
                        eq(ShoppingCart::getUserId,BaseContext.getCurrentId())
        ));
    }

    /**
     * 添加菜品
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("购物车数据:{}",shoppingCart);

        //设置用户Id,指定那个用户的购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //判断当前菜品或套餐,是否已存在购物车
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        if (dishId != null) {
            //添加到购物车的是菜品
            //queryWrapper.eq(shoppingCart.getDishFlavor() != null,ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart now_shoppingCart = shoppingCartService.getOne(queryWrapper);

        //如果存在,在原来数量上添加设置number数量+1
        if (!Objects.isNull(now_shoppingCart)) {
            now_shoppingCart.setNumber(now_shoppingCart.getNumber()+1);
            //最终个数修改
            shoppingCartService.updateById(now_shoppingCart);
        } else {
            //如果不存在,则添加到购物车
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            now_shoppingCart = shoppingCart;
        }

        return R.success(now_shoppingCart);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("clean")
    public R<String> cleanShopping() {
        log.info("清空所有的订单信息");
        shoppingCartService.remove(
                new LambdaQueryWrapper<ShoppingCart>().
                        eq(ShoppingCart::getUserId,BaseContext.getCurrentId())
        );
        return R.success("成功清空😋😋");
    }

    /**
     * 修改当前订单个数
     * @param shoppingCart
     * @return
     */
    //@PostMapping("/sub")
    public R<ShoppingCart> subShoppingCart01(@RequestBody ShoppingCart shoppingCart) {
        log.info("修改当前购物车菜品,shoppingCart: {}",shoppingCart);
        //获得当前菜品,当前菜品可能是套餐也可能是菜品
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        //修改
        LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        Long dishId = shoppingCart.getDishId();
        ShoppingCart now_shoppingCart = null;

        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            now_shoppingCart = shoppingCartService.getOne(queryWrapper);

            updateWrapper.eq(ShoppingCart::getDishId,dishId);
            updateWrapper.set(ShoppingCart::getNumber,now_shoppingCart.getNumber()-1);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            now_shoppingCart = shoppingCartService.getOne(queryWrapper);

            updateWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            updateWrapper.set(ShoppingCart::getNumber,now_shoppingCart.getNumber()-1);
        }


        if (now_shoppingCart.getNumber() > 0) {
            if (now_shoppingCart.getNumber() == 1) {
                System.out.println("删除了");
                shoppingCartService.removeById(now_shoppingCart.getId());
            }else {
                //修改
                shoppingCartService.update(updateWrapper);
            }
        }

        //在获得当前菜品
        if (now_shoppingCart.getNumber() == 1) {
            now_shoppingCart.setNumber(0);
            return R.success(now_shoppingCart);
        }
        now_shoppingCart = shoppingCartService.getById(now_shoppingCart.getId());
        System.out.println("当前:" + now_shoppingCart);
        return R.success(now_shoppingCart);
    }
    @PostMapping("/sub")
    public R<ShoppingCart> subShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        //先临时存储
        //先删除,当前菜品
        //如果num=0不进行添加
        //添加
        //返回当前菜品
        ShoppingCart now_shoppingCart;

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        Long dishId = shoppingCart.getDishId();
        //查找
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            now_shoppingCart = shoppingCartService.getOne(queryWrapper);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            now_shoppingCart = shoppingCartService.getOne(queryWrapper);
        }

        //删除,当前菜品Id绝对存在
        shoppingCartService.removeById(now_shoppingCart.getId());
        now_shoppingCart.setNumber(now_shoppingCart.getNumber() - 1);
        //添加
        if (now_shoppingCart.getNumber() != 0) {
            shoppingCartService.save(now_shoppingCart);
        }

        return R.success(now_shoppingCart);
    }
}
