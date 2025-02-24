package com.pangying.controller;

import com.pangying.entity.OrderReview;
import com.pangying.entity.R;
import com.pangying.service.IOrderReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 * 订单评价表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-02-24
 */
@CrossOrigin
@RestController
@RequestMapping("/orderReview")
public class OrderReviewController {
    
    @Autowired
    private IOrderReviewService orderReviewService;

    /**
     * 获取所有评价列表
     */
    @GetMapping("/list")
    public R list() {
        return R.success(orderReviewService.list());
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return orderReviewService.removeById(id) ? R.success() : R.fail("删除评价失败");
    }

    /**
     * 添加评价
     */
    @PostMapping
    public R add(@RequestBody OrderReview orderReview) {
        // 设置创建时间
        orderReview.setCreatedAt(LocalDateTime.now());
        orderReview.setUpdatedAt(LocalDateTime.now());
        return orderReviewService.save(orderReview) ? R.success() : R.fail("添加评价失败");
    }

    /**
     * 更新评价
     */
    @PostMapping("/update")
    public R update(@RequestBody OrderReview orderReview) {
        // 更新修改时间
        orderReview.setUpdatedAt(LocalDateTime.now());
        return orderReviewService.updateById(orderReview) ? R.success() : R.fail("更新评价失败");
    }
}
