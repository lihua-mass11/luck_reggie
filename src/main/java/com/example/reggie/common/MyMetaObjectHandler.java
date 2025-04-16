package com.example.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.example.reggie.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

//MetaObjectHandler这个接口,是元数据处理器
/**
 * 公共自动自动填充,是MybatisPlu提供的这个接口MetaObjectHandler
 *  @TableField(fill = FieldFill.INSERT)
 *     private Long createUser;
 */
//自定义元数据对象处理器
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Autowired
    private HttpSession session;

    /**
     * 插入操作自动填充
     * @param metaObject
     */
    //插入时执行
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共自动自动填充[insert]...");
        log.info(metaObject.toString());
        //employee.setCreateTime(LocalDateTime.now());//创建时间
        //employee.setUpdateTime(LocalDateTime.now());//更新时间

        //employee.setCreateUser(empId);//创建人
        //employee.setUpdateUser(empId);//更新人

        //这个对象封装了我们的对象数据
        //metaObject.getOriginalObject();
        //metaObject.setValue("createTime", LocalDateTime.now());//两个方法是等价的
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        //metaObject.setValue("createUser", ((Employee)metaObject.getOriginalObject()).getCreateUser());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 更新操作进行自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共自动自动填充[update]...");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());

        long id = Thread.currentThread().getId();
        log.info("线程id: {}",id);
        System.out.println("session对象:" + session.getId());
    }
}
