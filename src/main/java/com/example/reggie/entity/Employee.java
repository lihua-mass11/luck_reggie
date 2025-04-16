package com.example.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工
 */
@Data
@TableName("employee")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime updateTime;

    //fill进行填充  FieldFill.INSERT填充策略,
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    //插入和更新时填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
