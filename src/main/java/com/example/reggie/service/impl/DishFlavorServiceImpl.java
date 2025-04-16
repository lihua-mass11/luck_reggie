package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.entity.dto.DishDto;
import com.example.reggie.mapper.DishFlavorMapper;
import com.example.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @author  唐三
 * descri[tion: 菜品口味业务逻辑层
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
                        implements DishFlavorService {

}
