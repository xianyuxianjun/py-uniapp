package com.pangying.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pangying.entity.Product;
import com.pangying.entity.R;
import com.pangying.service.IFileService;
import com.pangying.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 * 商品信息表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-02-26
 */
@CrossOrigin
@RestController
@RequestMapping("/product")
public class ProductController {
    
    @Autowired
    private IProductService productService;
    
    @Autowired
    private IFileService fileService;

    /**
     * 获取所有商品列表
     */
    @GetMapping("/list")
    public R list() {
        return R.success(productService.list());
    }

    /**
     * 删除指定ID的商品
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return productService.removeById(id) ? R.success() : R.fail("删除失败");
    }

    /**
     * 添加新商品
     */
    @PostMapping
    public R add(@RequestBody Product product) {
        // 处理商品图片
        String imageUrl = fileService.getFilePathById(product.getId(), "product");
        if (imageUrl != null) {
            product.setImageUrl(imageUrl);
        }
        return productService.save(product) ? R.success() : R.fail("添加失败");
    }

    /**
     * 更新商品信息
     */
    @PostMapping("/update")
    public R update(@RequestBody Product product) {
        // 处理商品图片
        String imageUrl = fileService.getFilePathById(product.getId(), "product");
        if (imageUrl != null) {
            product.setImageUrl(imageUrl);
        }
        return productService.updateById(product) ? R.success() : R.fail("更新失败");
    }
    
    /**
     * 根据ID获取商品详情
     */
    @GetMapping("/getById")
    public R getById(@RequestParam("id") Long id) {
        Product product = productService.getById(id);
        return product != null ? R.success(product) : R.fail("商品不存在");
    }
    
    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public R search(@RequestParam String keyword) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Product::getProductName, keyword)
               .or()
               .like(Product::getDescription, keyword);
        return R.success(productService.list(wrapper));
    }
    
    /**
     * 更新商品状态（上架/下架）
     */
    @PostMapping("/status/{id}")
    public R updateStatus(@PathVariable Long id, @RequestParam Byte status) {
        Product product = productService.getById(id);
        if (product == null) {
            return R.fail("商品不存在");
        }
        
        product.setStatus(status);
        
        return productService.updateById(product) ? R.success() : R.fail("更新商品状态失败");
    }
}
