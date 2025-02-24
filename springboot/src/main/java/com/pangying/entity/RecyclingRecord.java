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
/**
 * <p>
 * 回收记录表
 * </p>
 *
 * @author baomidou
 * @since 2025-02-24
 */
@Getter
@Setter
@ToString
@TableName("recycling_record")
public class RecyclingRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID，主键，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的预约订单ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 回收员ID
     */
    @TableField("recycler_id")
    private Long recyclerId;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 废品分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 重量/数量
     */
    @TableField("weight")
    private BigDecimal weight;

    /**
     * 单价
     */
    @TableField("unit_price")
    private BigDecimal unitPrice;

    /**
     * 金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 上门回收时间
     */
    @TableField("pickup_time")
    private LocalDateTime pickupTime;

    /**
     * 回收地址
     */
    @TableField("pickup_address")
    private String pickupAddress;

    /**
     * 备注信息
     */
    @TableField("notes")
    private String notes;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
