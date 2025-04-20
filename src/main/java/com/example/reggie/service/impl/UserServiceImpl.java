package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.entity.User;
import com.example.reggie.mapper.UserMapper;
import com.example.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author  唐三
 * description: 移动端用户登录业务逻辑处理层
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
                        implements UserService {
}
