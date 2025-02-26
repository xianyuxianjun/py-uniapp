package com.pangying.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product_order")
public class ProductOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;
    private Integer status;
    private String address;
    private String contactName;
    private String contactPhone;
    private String notes;
    private LocalDateTime paymentTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime completionTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}