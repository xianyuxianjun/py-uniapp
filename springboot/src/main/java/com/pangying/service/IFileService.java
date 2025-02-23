package com.pangying.service;

import com.pangying.entity.File;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author baomidou
 * @since 2025-02-23
 */
public interface IFileService extends IService<File> {
    File uploadFile(MultipartFile file, String relatedType, Long relatedId) throws Exception;

    String getFilePathById(Long relatedId, String relatedType);
}
