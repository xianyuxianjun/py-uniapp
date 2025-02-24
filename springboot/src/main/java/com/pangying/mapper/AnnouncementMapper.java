package com.pangying.mapper;

import com.pangying.entity.Announcement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 新闻/公告表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2025-02-24
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {

}

