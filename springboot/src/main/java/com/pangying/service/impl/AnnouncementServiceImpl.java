package com.pangying.service.impl;

import com.pangying.entity.Announcement;
import com.pangying.mapper.AnnouncementMapper;
import com.pangying.service.IAnnouncementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 新闻/公告表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-02-24
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements IAnnouncementService {

}
