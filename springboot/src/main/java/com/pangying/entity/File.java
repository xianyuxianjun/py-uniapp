package com.pangying.entity;

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
 *
 * </p>
 *
 * @author baomidou
 * @since 2025-02-23
 */
@Getter
@Setter
@ToString
@TableName("file")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;

    /**
     * 文件名称
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件类型
     */
    @TableField("related_type")
    private String relatedType;

    /**
     * 关联者id
     */
    @TableField("related_id")
    private Long relatedId;

    /**
     * 上传时间
     */
    @TableField("create_at")
    private LocalDateTime createAt;
}
