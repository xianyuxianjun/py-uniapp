package com.pangying.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductOrderVO {
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
    private String statusText; // 状态文本
    private String address;
    private String contactName;
    private String contactPhone;
    private String notes;
    private LocalDateTime paymentTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime completionTime;
    private LocalDateTime createdAt;
    
    // 获取状态文本
    public String getStatusText() {
        switch (status) {
            case 1: return "待支付";
            case 2: return "已支付";
            case 3: return "已发货";
            case 4: return "已完成";
            case 5: return "已取消";
            default: return "未知状态";
        }
    }
}