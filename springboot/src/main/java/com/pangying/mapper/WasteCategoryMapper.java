package com.pangying.mapper;

import com.pangying.entity.WasteCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 废品分类表，用于存储废品分类的名称、价格、图片等信息 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2025-02-24
 */
@Mapper
public interface WasteCategoryMapper extends BaseMapper<WasteCategory> {

}

