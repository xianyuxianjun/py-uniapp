package com.pangying.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * <p>
 * 上门预约订单表
 * </p>
 *
 * @author baomidou
 * @since 2025-02-24
 */
@Getter
@Setter
@ToString
@TableName("appointment_order")
public class AppointmentOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID，主键，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，关联用户表
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 预约时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("appointment_time")
    private LocalDateTime appointmentTime;

    /**
     * 上门地址
     */
    @TableField("address")
    private String address;

    /**
     * 联系人姓名
     */
    @TableField("contact_name")
    private String contactName;

    /**
     * 联系人电话
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 订单状态（1：待确认，2：已接单，3：已完成，4：已取消）
     */
    @TableField("status")
    private Byte status;

    /**
     * 备注信息
     */
    @TableField("notes")
    private String notes;

    /**
     * 订单创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 订单更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 上门地址经度，用于地图展示
     */
    @TableField("longitude")
    private BigDecimal longitude;

    /**
     * 上门地址纬度，用于地图展示
     */
    @TableField("latitude")
    private BigDecimal latitude;

    /**
     * 地图上的标准地址
     */
    @TableField("map_address")
    private String mapAddress;
}
