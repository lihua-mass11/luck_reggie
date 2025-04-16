package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.entity.Category;

/**
 * 菜品分类service接口
 */
public interface CategoryService extends IService<Category> {

    void remove(Long id);
}
