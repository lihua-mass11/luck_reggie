package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.entity.Employee;
import com.example.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author 唐三
 * discription: 员工的业务表示层
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(
            HttpServletRequest request,
            @RequestBody Employee employee
    ) {
        //System.out.println(1/0);
        /**
         * 1,将页面提交的密码进行md5加密处理
         * 2,根据页面提交的用户名username查询数据库
         * 3,如果没有返回登录失败
         * 4,密码匹配,如果不一致返回失败
         * 5,查看员工状态是否已被禁用
         * 6,登录成功,将员工id存入Session并返回登录成功结果
         */
        // 1,将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        //进行md5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 数据库查询
        // .ne对吧不等于  .noTBetween不在这个范围之内  .listRight最左边 s最左边第一个为s
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());

        // 获取第一个数据 .getOne查询唯一数据
        Employee emp = employeeService.getOne(queryWrapper);

        // 3,如果没有返回登录失败
        if (emp == null) {
            return R.error("您登录失败了😒😒");
        }

        // 4,密码匹配,如果不一致返回失败
        if (!Objects.equals(password, emp.getPassword())) {
            return R.error("抱歉您的密码不一致😋😋");
        }

        // 5,查看员工状态是否已被禁用 0表示禁用
        if (Objects.equals(emp.getStatus(),0)) {
            return R.error("您已被拉入黑名单😭😭");
        }

        // 6,登录成功,将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());

        return R.success(employee);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        log.info("接口运行:{}", "/employee/logout");

        //清空当前登录的session的id
        request.getSession().removeAttribute("employee");

        return R.success("成功退出");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee) {
        log.info("新增员工,员工信息: {}", employee.toString());

        System.out.println("你好");
        // 设置初始密码123456,需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //使用了MybatisPlus提供的自动填充接口设置MetaObjectHandler没必要自己设置更新时间了
        //employee.setCreateTime(LocalDateTime.now());//创建时间
        //employee.setUpdateTime(LocalDateTime.now());//更新时间

        //获取当前登录用户的id
        Long empId = (Long) request.getSession().getAttribute("employee");

        //employee.setCreateUser(empId);//创建人
        //employee.setUpdateUser(empId);//更新人

        //在我们再次新增用户时,如果用户重复,服务器控制台会抛出异常
        //try ... catch ... 捕获,不太使用
        employeeService.save(employee);//插入数据
        return R.success("新增员工成功😁😁");
    }

    /**
     * 员工信息分页查询
     * @param page 页码
     * @param pageSize  每页数据
     * @param name 根据搜索进行查询
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page,int pageSize,String name) {
        //this.tableData = res.data.records 前端直接获取到了records对象集合
        log.info("page = {},pageSize = {},name = {}", page, pageSize, name);

        //{"code":1,"msg":null,"data":[1,2,3],"map":{}}无论集合还是数组最终返回的值都是数组
        //因为集合的底层原理也是数组
        /**
         * 开发思路
         *   1,先分页,
         *   2,在进行筛选
         *        如果name参数为null
         *        返回所有
         *   3,返回结果
         */
        /**
         * 查询搜索,上下分页都会发送请求
         */

        //分页构造器
        Page<Employee> infoPage = new Page<>(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //这个已经给我们添加了这个判断,当我们传过来的这个值不为空,才进行查询
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

        //添加一个排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);//根据更新事件进行排序
        //对这个page对象进行改造,返回的对象还是这个page
        employeeService.page(infoPage,queryWrapper);
        //进行筛选
        return R.success(infoPage);
    }

    /**
     * 启用和禁用分析:
     *    管理员admin登录系统可以对所有员工账号进行启用,禁用操作;
     *    如果某个员工账号状态为正常,则按钮显示为 "禁用" , 如果员工账号状态为禁用,则按钮显示为启用
     */
    /**
     * 根据id来修改员工信息,
     * 当数据修改保存后调用的还是这个方法
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee) {
        //如果id主键过长咋16位以后可能会导致json精度不准
        //1904514374677069800 前端传过来的
        //1904514374677069826
        //解决办法,统一将json转为String类型的字符串

        long id = Thread.currentThread().getId();
        log.info("线程id: {}",id);

        /**
         * 更新时要有更新人,还有更新时间
         */
        //根据对象获取当前用户id
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateTime(LocalDateTime.now());
        //当前那个用户修改
        //employee.setUpdateUser(empId);
        //修改其状态
        employeeService.updateById(employee);
        return R.success("员工信息修改成功😊😊");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    //当用户编辑时我们先展示当前的用户信息,老师的方法名称getById
    @GetMapping("/{id}")
    public R<Employee> userEdit(@PathVariable("id") Long id ) {
        log.info("根据id查询员工表信息");
        /**
         * 思路:
         *   根据我们当前发送的用户id进行查询,最终返回到编辑器
         */
        //一般情况下,不需要判断员工是否为null,只要有员工才会有编辑,编辑依赖员工
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
    }
}
