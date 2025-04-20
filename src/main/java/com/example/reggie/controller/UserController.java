package com.example.reggie.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.common.R;
import com.example.reggie.entity.User;
import com.example.reggie.service.UserService;
import com.example.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;

/**
 * @author 唐三
 * description: 移动端用户登录表示层
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map ,HttpSession session) {
        log.info("map: {}",map);

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String validate = map.get("code").toString();
        //从Session中获取保存的验证码
        String req_validate = session.getAttribute(phone).toString();
        //进行验证的比对(页面提交的验证码和session中保存的验证码进行比对)
        if (Objects.equals(validate,req_validate)) {
            //如果能够比对成功,说明登录成功
            //判断当前手机号对应的用户是否为新用户,如果是新用户,自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (Objects.isNull(user)) {
                user = new User();
                //添加用户手机号
                user.setPhone(phone);
                //用户状态设置
                user.setStatus(1);
                userService.save(user);
            }
            //保存用户session
            session.setAttribute("user",userService.getOne(queryWrapper).getId());
            return R.success(user);
        }

        return R.error("登录失败😒😒");
    }

    /**
     * 手机验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user,HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        log.info("user: {}",user);

        if (!StringUtils.isEmpty(phone)) {
            //生成随机验证码4位
            String validate = ValidateCodeUtils.generateValidateCode4String(4).toString();

            //保存需要生成的验证码
            session.setAttribute(phone,validate);
            //return R.success("手机短信发送成功😁😁");
            return R.success(validate);
        }
        return R.error("手机短信发送失败😋");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession session) {
        //销毁session
        session.removeAttribute("user");
        return R.success("成功退出😁😁");
    }
}
