package com.pangying.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pangying.entity.Product;
import com.pangying.entity.ProductOrder;
import com.pangying.mapper.ProductOrderMapper;
import com.pangying.service.IProductOrderService;
import com.pangying.service.IProductService;
import com.pangying.vo.ProductOrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder> implements IProductOrderService {

    @Autowired
    private IProductService productService;

    @Override
    @Transactional
    public ProductOrderVO createOrder(Long userId, Long productId, Integer quantity, 
                                     String address, String contactName, String contactPhone, String notes) {
        // 检查商品是否存在
        Product product = productService.getById(productId);
        if (product == null || product.getStatus() != 1) {
            return null; // 商品不存在或已下架
        }
        
        // 检查库存
        if (product.getStockQuantity() < quantity) {
            return null; // 库存不足
        }
        
        // 创建订单
        ProductOrder order = new ProductOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setProductId(productId);
        order.setProductName(product.getProductName());
        order.setProductImage(product.getImageUrl());
        order.setPrice(product.getPrice());
        order.setQuantity(quantity);
        order.setTotalAmount(product.getPrice().multiply(new BigDecimal(quantity)));
        order.setStatus(1); // 待支付
        order.setAddress(address);
        order.setContactName(contactName);
        order.setContactPhone(contactPhone);
        order.setNotes(notes);
        
        // 保存订单
        save(order);
        
        // 减少库存
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productService.updateById(product);
        
        // 返回订单信息
        ProductOrderVO orderVO = new ProductOrderVO();
        BeanUtils.copyProperties(order, orderVO);
        return orderVO;
    }

    @Override
    @Transactional
    public boolean payOrder(String orderNo) {
        LambdaQueryWrapper<ProductOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductOrder::getOrderNo, orderNo);
        ProductOrder order = getOne(wrapper);
        
        if (order == null || order.getStatus() != 1) {
            return false;
        }
        
        order.setStatus(2); // 已支付
        order.setPaymentTime(LocalDateTime.now());
        return updateById(order);
    }

    @Override
    @Transactional
    public boolean cancelOrder(String orderNo) {
        LambdaQueryWrapper<ProductOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductOrder::getOrderNo, orderNo);
        ProductOrder order = getOne(wrapper);
        
        if (order == null || order.getStatus() != 1) {
            return false;
        }
        
        // 恢复库存
        Product product = productService.getById(order.getProductId());
        if (product != null) {
            product.setStockQuantity(product.getStockQuantity() + order.getQuantity());
            productService.updateById(product);
        }
        
        order.setStatus(5); // 已取消
        return updateById(order);
    }

    @Override
    public List<ProductOrderVO> getUserOrderList(Long userId) {
        LambdaQueryWrapper<ProductOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductOrder::getUserId, userId).orderByDesc(ProductOrder::getCreatedAt);
        List<ProductOrder> orderList = list(wrapper);
        
        List<ProductOrderVO> orderVOList = new ArrayList<>();
        for (ProductOrder order : orderList) {
            ProductOrderVO orderVO = new ProductOrderVO();
            BeanUtils.copyProperties(order, orderVO);
            orderVOList.add(orderVO);
        }
        
        return orderVOList;
    }

    @Override
    public ProductOrderVO getOrderDetail(String orderNo) {
        LambdaQueryWrapper<ProductOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductOrder::getOrderNo, orderNo);
        ProductOrder order = getOne(wrapper);
        
        if (order == null) {
            return null;
        }
        
        ProductOrderVO orderVO = new ProductOrderVO();
        BeanUtils.copyProperties(order, orderVO);
        return orderVO;
    }
    
    // 生成订单编号
    private String generateOrderNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String dateTime = LocalDateTime.now().format(formatter);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
        return "P" + dateTime + uuid;
    }
}