package com.pangying.service.impl;

import com.pangying.entity.WasteCategory;
import com.pangying.mapper.WasteCategoryMapper;
import com.pangying.service.IWasteCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 废品分类表，用于存储废品分类的名称、价格、图片等信息 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-02-24
 */
@Service
public class WasteCategoryServiceImpl extends ServiceImpl<WasteCategoryMapper, WasteCategory> implements IWasteCategoryService {

}
