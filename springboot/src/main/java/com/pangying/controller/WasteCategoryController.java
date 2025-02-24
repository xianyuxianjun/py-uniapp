package com.pangying.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pangying.entity.R;
import com.pangying.entity.WasteCategory;
import com.pangying.service.IFileService;
import com.pangying.service.IWasteCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 废品分类表，用于存储废品分类的名称、价格、图片等信息 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-02-24
 */
@CrossOrigin
@RestController
@RequestMapping("/wasteCategory")
public class WasteCategoryController {
    
    @Autowired
    private IWasteCategoryService wasteCategoryService;
    
    @Autowired
    private IFileService fileService;

    /**
     * 获取所有废品分类列表
     */
    @GetMapping("/list")
    public R list() {
        return R.success(wasteCategoryService.list());
    }

    /**
     * 删除指定ID的废品分类
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Integer id) {
        return wasteCategoryService.removeById(id) ? R.success() : R.fail("删除失败");
    }

    /**
     * 添加新的废品分类
     */
    @PostMapping
    public R add(@RequestBody WasteCategory wasteCategory) {
        // 处理分类图片
        String imageUrl = fileService.getFilePathById(wasteCategory.getId(), "category");
        if (imageUrl != null) {
            wasteCategory.setImageUrl(imageUrl);
        }
        return wasteCategoryService.save(wasteCategory) ? R.success() : R.fail("添加失败");
    }

    /**
     * 更新废品分类信息
     */
    @PostMapping("/update")
    public R update(@RequestBody WasteCategory wasteCategory) {
        // 处理分类图片
        String imageUrl = fileService.getFilePathById(wasteCategory.getId(), "category");
        if (imageUrl != null) {
            wasteCategory.setImageUrl(imageUrl);
        }
        return wasteCategoryService.updateById(wasteCategory) ? R.success() : R.fail("更新失败");
    }
}
