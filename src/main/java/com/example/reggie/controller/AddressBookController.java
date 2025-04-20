package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.reggie.common.R;
import com.example.reggie.entity.AddressBook;
import com.example.reggie.service.AddressBookService;
import com.example.reggie.utils.BaseContext;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


/**
 * @author 唐三
 * descrpition: 移动端用户地址表示层
 */
@Slf4j
@RestController
@RequestMapping("/addressBooks")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        //封装用户id
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("地址 :{} ,addressBook: {}","/addressBook/",addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        /**
         * 当前这个用户可以填写多个地址：1 表示使用当前地址  0 表示禁用
         */
        log.info("地址 :{} ,addressBook: {}","/addressBook/default",addressBook);
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        updateWrapper.set(AddressBook::getIsDefault,0);
        //SQL: update address_book set is_default=0 where user_id=?
        addressBookService.update(updateWrapper);

        addressBook.setIsDefault(1);
        //SQL: update address_book set is_default=1 where user_id=?
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id) {
        /**
         * 执行流程
         *   查询到地址,可能用户所查询的地址不存在
         */

        AddressBook addressBook = addressBookService.getById(id);
        if (Objects.isNull(addressBook)) {
            return R.error("抱歉！您所查询的地址不存在😒😒");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        log.info("请求类型: {},地址: {}","GET","addressBook/default");
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault,1);

        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (Objects.isNull(addressBook)) {
            return R.error("没有该地址对象!!");
        } else {
            return R.success(addressBook);
        }
    }

    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        log.info("请求类型: {},地址: {}","GET","addressBook/list");

        //条件装配
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId() != null,AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id=? order by update_time desc;
        return R.success(addressBookService.list(queryWrapper));
    }
}
