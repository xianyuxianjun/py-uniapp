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
 * 商品信息表
 * </p>
 *
 * @author baomidou
 * @since 2025-02-26
 */
@Getter
@Setter
@ToString
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    @TableField("product_name")
    private String productName;

    /**
     * 商品描述
     */
    @TableField("description")
    private String description;

    /**
     * 商品销售价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 商品库存数量
     */
    @TableField("stock_quantity")
    private Integer stockQuantity;

    /**
     * 商品图片URL
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 商品状态（1：在售，2：下架）
     */
    @TableField("status")
    private Byte status;

    /**
     * 商品创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 商品更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
