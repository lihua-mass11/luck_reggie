package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.entity.OrderDetail;
import com.example.reggie.entity.Orders;
import com.example.reggie.mapper.OrderDetailMapper;
import com.example.reggie.mapper.OrdersMapper;
import com.example.reggie.service.OrderDetailService;
import com.example.reggie.service.OrdersService;
import org.springframework.stereotype.Service;

/**
 * @author 唐三
 * description: 详细订单次序展示业务逻辑层
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
                        implements OrderDetailService {
}
