package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.reggie.common.R;
import com.example.reggie.entity.AddressBook;
import com.example.reggie.service.AddressBookService;
import com.example.reggie.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookViewController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 思路分析,我们都要感谢什么事情
     *   1,默认地址显示
     *        用户登录: 是可以添加多个订餐地址,但是用户只可以启用一个订餐地址
     *   2,地址添加
     *
     *   3,获取所有地址
     *        获取所有该用户的地址
     *   4,地址修改
     *
     *   5,地址删除
     *
     *   6,根据id数据回显
     *   7,用户修改地址信息
     */

    /**
     * 默认地址显示
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getAddressBookDefault() {
        log.info("请求类型: {}, 请求地址: {}","GET","/addressBook/default");
        //SQL:SELECT * FROM address_book WHERE default_id=1 AND user_id=?;
        AddressBook addressBook = addressBookService.getOne(
                new LambdaQueryWrapper<AddressBook>().
                        eq(AddressBook::getIsDefault,1).
                        eq(AddressBook::getUserId, BaseContext.getCurrentId())
        );

       if (Objects.isNull(addressBook)) {
           return R.error("抱歉您的地址不存在😖😖");
       }else {
            return R.success(addressBook);
       }
    }

    /**
     * 添加用信息
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> saveAddressBook(@RequestBody AddressBook addressBook) {
        log.info("请求类型: {}, 请求地址: {}, addressBook: {}","POST","/addressBook/",addressBook);
        System.out.println("地址:" + addressBook);
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success("恭喜您成功添加😁😁");
    }

    /**
     * 显示当前用户的所有地址
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> allAndAddressBook() {
        log.info("请求类型: {}, 请求地址: {}","GET","/addressBook/list");
        System.out.println("用户id:" + BaseContext.getCurrentId());
        return R.success(addressBookService.list(
                new LambdaQueryWrapper<AddressBook>().
                            eq(AddressBook::getUserId,BaseContext.getCurrentId()).
                            orderByDesc(AddressBook::getId)
        ));
    }

    /**
     * 修改当前默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<String> updateAndAddressBook(@RequestBody AddressBook addressBook) {
        //前端认为我们必须有一个地址
        log.info("请求类型: {}, 请求地址: {}, addressBool: {}","GET","/addressBook/",addressBook);
        //数据重置
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        //只允许有一个用户地址
        //关闭所有的用户地址
        updateWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(updateWrapper);

        //设置当前用户的默认地址
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("成共修改");
    }

    /**
     * 地址修改表单数据回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> awareAndAddressBook(@PathVariable Long id) {
        log.info("请求类型: {}, 请求地址: {}, id: {}","GET","/addressBook/{id}",id);
        System.out.println("地方:" + addressBookService.getById(id).getLabel());
        return R.success(addressBookService.getById(id));
    }

    /**
     * 地址信息修改
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> updateByAddressBook(@RequestBody AddressBook addressBook) {
        log.info("请求类型: {}, 请求地址: {}, update addressBool: {}","PUT","/addressBook/",addressBook);
        addressBookService.updateById(addressBook);
        return R.success("成功修改😁😁");
    }

    /**
     * 地址删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long ids) {
        log.info("请求类型: {}, 请求地址: {}, delete id: {}","DELETE","/addressBook/",ids);
        addressBookService.removeById(ids);
        return R.success("成功删除😁😁");
    }
}
