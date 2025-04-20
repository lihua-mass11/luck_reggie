package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.entity.*;
import com.example.reggie.exceptions.BusinessException;
import com.example.reggie.mapper.OrdersMapper;
import com.example.reggie.service.*;
import com.example.reggie.utils.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 唐三
 * description: 订单次序展示业务逻辑层
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
                        implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户订单提交
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //获得当前用户Id
        Long userId = BaseContext.getCurrentId();
        //查询当前用户购物车数据
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(
                new LambdaQueryWrapper<ShoppingCart>().
                        eq(ShoppingCart::getUserId,userId)
        );

        if (shoppingCarts.isEmpty()) {
            throw new BusinessException("抱歉您的购物车为空😖😖");
        }
        //查询用户数据
        User user = userService.getById(userId);
        //查询地址数据
        System.out.println("地址:" + orders.getAddressBookId());
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) {
            throw new BusinessException("用户地址信息为空😋");
        }

        Long orderId = IdWorker.getId();//订单号
        //下单,向订单表orders
        //orders.setNumber(String.valueOf(orderId));

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        //向订单插入一条数据
        this.save(orders);
        //向订单明细表插入数据order_detail,多条数据
        orderDetailService.saveBatch(orderDetails);
        //清空购物车
        shoppingCartService.remove(
                new LambdaUpdateWrapper<ShoppingCart>().
                        eq(ShoppingCart::getUserId,userId)
        );
    }
}
