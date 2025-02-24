package com.pangying.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 新闻/公告表
 * </p>
 *
 * @author baomidou
 * @since 2025-02-24
 */
@Getter
@Setter
@ToString
@TableName("announcement")
public class Announcement implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识ID，主键，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 新闻或公告标题
     */
    @TableField("title")
    private String title;

    /**
     * 新闻或公告内容
     */
    @TableField("content")
    private String content;

    /**
     * 状态（1：草稿，2：发布，3：下线）
     */
    @TableField("status")
    private Byte status;

    /**
     * 发布日期
     */
    @TableField("publish_date")
    private LocalDateTime publishDate;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
