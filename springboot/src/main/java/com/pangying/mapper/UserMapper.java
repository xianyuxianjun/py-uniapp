package com.pangying.mapper;

import com.pangying.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2025-02-23
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}

