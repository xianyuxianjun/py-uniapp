package com.pangying.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pangying.entity.AppointmentOrder;
import com.pangying.entity.R;
import com.pangying.service.IAppointmentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <p>
 * 上门预约订单表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-02-24
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/appointmentOrder")
public class AppointmentOrderController {
    
    @Autowired
    private IAppointmentOrderService appointmentOrderService;

    /**
     * 获取所有预约订单列表
     */
    @GetMapping("/list")
    public R list() {
        return R.success(appointmentOrderService.list());
    }

    /**
     * 删除预约订单
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return appointmentOrderService.removeById(id) ? R.success() : R.fail("删除订单失败");
    }

    /**
     * 添加预约订单
     */
    @PostMapping
    public R add(@RequestBody AppointmentOrder appointmentOrder) {
        return appointmentOrderService.save(appointmentOrder) ? R.success() : R.fail("添加订单失败");
    }

    /**
     * 更新预约订单
     */
    @PostMapping("/update")
    public R update(@RequestBody AppointmentOrder appointmentOrder) {
        return appointmentOrderService.updateById(appointmentOrder) ? R.success() : R.fail("更新订单失败");
    }

    /**
     * 获取用户的预约订单列表
     */
    @GetMapping("/user/{userId}")
    public R getUserOrders(@PathVariable Long userId) {
        try {
            // 创建查询条件
            QueryWrapper<AppointmentOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .orderByDesc("created_at"); // 按创建时间倒序排列
            
            List<AppointmentOrder> orders = appointmentOrderService.list(queryWrapper);
            return R.success(orders);
        } catch (Exception e) {
            log.error("获取用户订单失败：", e);
            return R.fail("获取订单失败");
        }
    }

    /**
     * 取消预约订单
     */
    @PostMapping("/cancel/{orderId}")
    public R cancelOrder(@PathVariable Long orderId) {
        try {
            AppointmentOrder order = appointmentOrderService.getById(orderId);
            if (order == null) {
                return R.fail("订单不存在");
            }
            
            // 只有待确认状态的订单可以取消
            if (order.getStatus() != 1) {
                return R.fail("当前订单状态不可取消");
            }
            
            order.setStatus((byte) 4); // 设置为已取消状态
            boolean success = appointmentOrderService.updateById(order);
            
            return success ? R.success() : R.fail("取消订单失败");
        } catch (Exception e) {
            log.error("取消订单失败：", e);
            return R.fail("取消订单失败");
        }
    }
}
