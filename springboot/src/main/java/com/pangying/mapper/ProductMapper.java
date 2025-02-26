package com.pangying.mapper;

import com.pangying.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 商品信息表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2025-02-26
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

}

