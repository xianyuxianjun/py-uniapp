package com.pangying.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pangying.entity.Announcement;
import com.pangying.entity.R;
import com.pangying.service.IAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 新闻/公告表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-02-24
 */
@CrossOrigin
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {
    
    @Autowired
    private IAnnouncementService announcementService;

    /**
     * 获取所有公告列表
     */
    @GetMapping("/list")
    public R list() {
        return R.success(announcementService.list());
    }

    /**
     * 删除指定公告
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return announcementService.removeById(id) ? R.success() : R.fail("删除失败");
    }

    /**
     * 添加新公告
     */
    @PostMapping
    public R add(@RequestBody Announcement announcement) {
        return announcementService.save(announcement) ? R.success() : R.fail("添加失败");
    }

    /**
     * 更新公告信息
     */
    @PostMapping("/update")
    public R update(@RequestBody Announcement announcement) {
        return announcementService.updateById(announcement) ? R.success() : R.fail("更新失败");
    }

    /**
     * 获取已发布的公告列表
     * 按发布时间倒序排列
     */
    @GetMapping("/published")
    public R getPublishedAnnouncements() {
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
            .eq(Announcement::getStatus, 2)  // 状态为已发布
            .orderByDesc(Announcement::getPublishDate);  // 按发布时间倒序排序
        
        return R.success(announcementService.list(queryWrapper));
    }
}
