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
 * 订单评价表
 * </p>
 *
 * @author baomidou
 * @since 2025-02-24
 */
@Getter
@Setter
@ToString
@TableName("order_review")
public class OrderReview implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评价ID，主键，自增
     */
    @TableId(value = "review_id", type = IdType.AUTO)
    private Long reviewId;

    /**
     * 订单ID，关联订单表
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 客户ID，关联用户表
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 评分（例如1-5分）
     */
    @TableField("rating")
    private Byte rating;

    /**
     * 评价文本内容
     */
    @TableField("comment")
    private String comment;

    /**
     * 评价创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 评价更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
