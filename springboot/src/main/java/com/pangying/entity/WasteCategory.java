package com.pangying.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 废品分类表，用于存储废品分类的名称、价格、图片等信息
 * </p>
 *
 * @author baomidou
 * @since 2025-02-24
 */
@Getter
@Setter
@ToString
@TableName("waste_category")
public class WasteCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，唯一标识每个分类
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分类名称（如“纸类”或“报纸”）
     */
    @TableField("category_name")
    private String categoryName;

    /**
     * 回收价格（单位：元/公斤或其他）
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 图片URL（用于展示分类图片）
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 分类描述（可选，用于补充说明分类信息）
     */
    @TableField("description")
    private String description;

    /**
     * 计量单位（如“公斤”、“吨”等）
     */
    @TableField("unit")
    private String unit;
    /**
     * 记录创建时间，自动生成
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 记录更新时间，自动更新
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
