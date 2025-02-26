package com.pangying.service.impl;

import com.pangying.entity.Product;
import com.pangying.mapper.ProductMapper;
import com.pangying.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品信息表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-02-26
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

}
