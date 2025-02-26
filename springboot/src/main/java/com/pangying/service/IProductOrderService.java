package com.pangying.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pangying.entity.ProductOrder;
import com.pangying.vo.ProductOrderVO;

import java.util.List;

public interface IProductOrderService extends IService<ProductOrder> {
    // 创建订单
    ProductOrderVO createOrder(Long userId, Long productId, Integer quantity, 
                              String address, String contactName, String contactPhone, String notes);
    
    // 支付订单
    boolean payOrder(String orderNo);
    
    // 取消订单
    boolean cancelOrder(String orderNo);
    
    // 获取用户订单列表
    List<ProductOrderVO> getUserOrderList(Long userId);
    
    // 获取订单详情
    ProductOrderVO getOrderDetail(String orderNo);
}