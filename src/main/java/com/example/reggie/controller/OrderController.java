package com.example.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.entity.Orders;
import com.example.reggie.entity.User;
import com.example.reggie.service.OrdersService;
import com.example.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

/**
 * @author  唐三
 * description: 订单次序信息表示层
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private UserService userService;

    /**
     * 用户下单
     * 最终可以根据用户id获取用户购物车信息
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单数据: {}",orders);
        ordersService.submit(orders);
        return R.success("下单成功😋😋");
    }

    /**
     * 根据分页获取用户信息
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<User>> userPage(Integer page,Integer pageSize) {
        Page<User> userPage = new Page<>(page,pageSize);
        return R.success(userService.page(userPage));
    }
}