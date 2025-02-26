package com.pangying.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pangying.entity.ProductOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductOrderMapper extends BaseMapper<ProductOrder> {
}