package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.entity.Employee;
import com.example.reggie.mapper.EmployeeMapper;
import com.example.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author 唐三
 * discription: 员工表,业务逻辑层
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
                            implements EmployeeService {


}
