package com.pangying.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 *
 * </p>
 *
 * @author baomidou
 * @since 2025-02-23
 */
@Getter
@Setter
@ToString
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，主键，无符号整数，自动递增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名，唯一且不能为空
     */
    @TableField("username")
    private String username;

    /**
     * 密码哈希值，存储加密后的密码，不能为空
     */
    @TableField("password_hash")
    private String passwordHash;

    /**
     * 电子邮件地址，唯一且不能为空
     */
    @TableField("email")
    private String email;

    /**
     * 电话号码，唯一，允许为空
     */
    @TableField("phone")
    private String phone;

    /**
     * 用户头像的URL或路径，允许为空
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 用户状态（如：active, inactive, banned等）
     */
    @TableField("status")
    private String status;

    /**
     * 用户角色（如：admin, user, recycler）
     */
    @TableField("role")
    private String role;

    /**
     * 记录创建时间，默认为当前时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 记录更新时间，自动更新为当前时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 用户性别（如：男, 女, 未知）
     */
    @TableField("gender")
    private String gender;
}
