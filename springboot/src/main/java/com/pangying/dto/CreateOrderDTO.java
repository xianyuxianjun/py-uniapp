package com.pangying.dto;

import lombok.Data;

@Data
public class CreateOrderDTO {
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String address;
    private String contactName;
    private String contactPhone;
    private String notes;
} 