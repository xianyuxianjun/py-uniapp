package com.pangying.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pangying.entity.AppointmentOrder;
import com.pangying.entity.R;
import com.pangying.entity.RecyclingRecord;
import com.pangying.service.IAppointmentOrderService;
import com.pangying.service.IRecyclingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    private IRecyclingRecordService recyclingRecordService;
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

    /**
     * 回收员获取接单列表
     */
    @GetMapping("/recycler/list/{recyclerId}")
    public R getRecyclerOrders(@PathVariable Long recyclerId) {

        // 创建查询条件
        LambdaQueryWrapper<RecyclingRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RecyclingRecord::getRecyclerId, recyclerId);
        // 查询回收员接单的接单记录
        List<RecyclingRecord> recyclingRecords = recyclingRecordService.list(queryWrapper);
        List<AppointmentOrder> orders = new ArrayList<>();
        //查询预约订单
        recyclingRecords.forEach(recyclingRecord -> {
            LambdaQueryWrapper<AppointmentOrder> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(AppointmentOrder::getId, recyclingRecord.getOrderId());
            orders.add(appointmentOrderService.getOne(queryWrapper1));
        });
        return R.success(orders);
    }

    @PostMapping("/accept/{orderId}")
    public R acceptOrder(@PathVariable Long orderId, @RequestParam Long recyclerId) {
        try {
            // 1. 更新订单状态为已接单
            AppointmentOrder order = appointmentOrderService.getById(orderId);
            if (order == null) {
                return R.fail("订单不存在");
            }
            
            if (order.getStatus() != 1) {
                return R.fail("订单状态不正确");
            }
            
            order.setStatus((byte) 2); // 设置为进行中状态
            appointmentOrderService.updateById(order);
            
            // 2. 创建回收记录
            RecyclingRecord record = new RecyclingRecord();
            record.setOrderId(orderId);
            record.setRecyclerId(recyclerId);
            record.setCustomerId(order.getUserId());
            record.setCategoryId(1L); // 设置一个默认分类ID
            record.setPickupTime(order.getAppointmentTime());
            record.setPickupAddress(order.getAddress());
            record.setNotes(order.getNotes());
            record.setWeight(BigDecimal.ZERO); // 设置默认重量
            record.setUnitPrice(BigDecimal.ZERO); // 设置默认单价
            record.setAmount(BigDecimal.ZERO); // 设置默认金额
            record.setCreatedAt(LocalDateTime.now());
            record.setUpdatedAt(LocalDateTime.now());
            
            recyclingRecordService.save(record);
            
            return R.success();
        } catch (Exception e) {
            log.error("接单失败：", e);
            return R.fail("接单失败");
        }
    }

    /**
     * 回收员完成订单
     */
    @PostMapping("/complete/{orderId}")
    public R completeOrder(@PathVariable Long orderId, @RequestBody RecyclingRecord recyclingRecord) {
        try {
            // 1. 检查订单状态
            AppointmentOrder order = appointmentOrderService.getById(orderId);
            if (order == null) {
                return R.fail("订单不存在");
            }
            
            if (order.getStatus() != 2) {
                return R.fail("订单状态不正确，只能完成进行中的订单");
            }
            
            // 2. 更新订单状态为已完成
            order.setStatus((byte) 3);
            appointmentOrderService.updateById(order);
            
            // 3. 更新回收记录
            LambdaQueryWrapper<RecyclingRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RecyclingRecord::getOrderId, orderId);
            RecyclingRecord record = recyclingRecordService.getOne(queryWrapper);
            
            if (record != null) {
                // 更新回收记录的信息
                record.setCategoryId(recyclingRecord.getCategoryId());
                record.setWeight(recyclingRecord.getWeight());
                record.setUnitPrice(recyclingRecord.getUnitPrice());
                // 计算总金额
                BigDecimal amount = recyclingRecord.getWeight().multiply(recyclingRecord.getUnitPrice());
                record.setAmount(amount);
                record.setUpdatedAt(LocalDateTime.now());
                
                recyclingRecordService.updateById(record);
            }
            
            return R.success();
        } catch (Exception e) {
            log.error("完成订单失败：", e);
            return R.fail("完成订单失败");
        }
    }
}
    

