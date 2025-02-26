package com.pangying.controller;

import com.pangying.dto.CreateOrderDTO;
import com.pangying.dto.OrderNoDTO;
import com.pangying.entity.R;
import com.pangying.service.IProductOrderService;
import com.pangying.vo.ProductOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/product/order")
public class ProductOrderController {

    @Autowired
    private IProductOrderService productOrderService;

    @PostMapping("/create")
    public R createOrder(@RequestBody CreateOrderDTO orderDTO) {
        ProductOrderVO orderVO = productOrderService.createOrder(
            orderDTO.getUserId(),
            orderDTO.getProductId(),
            orderDTO.getQuantity(),
            orderDTO.getAddress(),
            orderDTO.getContactName(),
            orderDTO.getContactPhone(),
            orderDTO.getNotes()
        );
        return orderVO != null ? R.success(orderVO) : R.fail("创建订单失败");
    }

    @PostMapping("/pay")
    public R payOrder(@RequestParam("orderNo") String orderNo) {
        boolean result = productOrderService.payOrder(orderNo);
        return result ? R.success() : R.fail("支付订单失败");
    }

    @PostMapping("/cancel")
    public R cancelOrder(@RequestParam("orderNo") String orderNo) {
        boolean result = productOrderService.cancelOrder(orderNo);
        return result ? R.success() : R.fail("取消订单失败");
    }

    @GetMapping("/list")
    public R getUserOrderList(@RequestParam("userId") Long userId) {
        List<ProductOrderVO> orderList = productOrderService.getUserOrderList(userId);
        return R.success(orderList);
    }

    @GetMapping("/detail")
    public R getOrderDetail(@RequestParam("orderNo") String orderNo) {
        ProductOrderVO orderVO = productOrderService.getOrderDetail(orderNo);
        return orderVO != null ? R.success(orderVO) : R.fail("订单不存在");
    }
}
