package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.entity.Orders;

/**
 * 订单次序业务逻辑接口
 */
public interface OrdersService extends IService<Orders> {

    //用户订单提交
    public void submit(Orders orders);
}
