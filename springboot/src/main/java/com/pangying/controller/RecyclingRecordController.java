package com.pangying.controller;

import com.pangying.entity.R;
import com.pangying.entity.RecyclingRecord;
import com.pangying.service.IRecyclingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 * 回收记录表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-02-24
 */
@CrossOrigin
@RestController
@RequestMapping("/recyclingRecord")
public class RecyclingRecordController {
    
    @Autowired
    private IRecyclingRecordService recyclingRecordService;

    /**
     * 获取所有回收记录列表
     */
    @GetMapping("/list")
    public R list() {
        return R.success(recyclingRecordService.list());
    }

    /**
     * 删除回收记录
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return recyclingRecordService.removeById(id) ? R.success() : R.fail("删除回收记录失败");
    }

    /**
     * 添加回收记录
     */
    @PostMapping
    public R add(@RequestBody RecyclingRecord recyclingRecord) {
        // 设置创建时间
        recyclingRecord.setCreatedAt(LocalDateTime.now());
        recyclingRecord.setUpdatedAt(LocalDateTime.now());
        
        // 计算金额
        recyclingRecord.setAmount(
            recyclingRecord.getWeight().multiply(recyclingRecord.getUnitPrice())
        );
        
        return recyclingRecordService.save(recyclingRecord) ? R.success() : R.fail("添加回收记录失败");
    }

    /**
     * 更新回收记录
     */
    @PostMapping("/update")
    public R update(@RequestBody RecyclingRecord recyclingRecord) {
        // 更新修改时间
        recyclingRecord.setUpdatedAt(LocalDateTime.now());
        
        // 重新计算金额
        recyclingRecord.setAmount(
            recyclingRecord.getWeight().multiply(recyclingRecord.getUnitPrice())
        );
        
        return recyclingRecordService.updateById(recyclingRecord) ? R.success() : R.fail("更新回收记录失败");
    }
}
