package com.pangying.controller;

import com.pangying.entity.AppointmentOrder;
import com.pangying.entity.R;
import com.pangying.service.IAppointmentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 上门预约订单表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-02-24
 */
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
}
