package com.pangying.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pangying.entity.File;
import com.pangying.mapper.FileMapper;
import com.pangying.service.IFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-02-23
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {

    @Value("${file.upload.path}")
    private String uploadPath;

    private String BASE_URL = "http://localhost:8080/file/view/";

    @Override
    public File uploadFile(MultipartFile file, String relatedType, Long relatedId) throws Exception {
        // 获取项目根路径
        String projectRootPath = System.getProperty("user.dir");
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 生成文件保存路径
        String filePath = projectRootPath + java.io.File.separator + uploadPath + java.io.File.separator + UUID.randomUUID() + fileName;

        // 创建文件
        java.io.File dest = new java.io.File(filePath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        // 保存文件
        file.transferTo(dest);

        // 保存文件信息
        File files = new File();
        files.setFileName(fileName);
        files.setFilePath(filePath);
        files.setRelatedType(relatedType);
        files.setRelatedId(relatedId);
        files.setCreateAt(LocalDateTime.now());
        save(files);

        return files;
    }

    @Override
    public String getFilePathById(Long relatedId, String relatedType) {
        if (relatedId == null || relatedType == null) {
            return null;
        }
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(File::getRelatedId, relatedId);
        queryWrapper.eq(File::getRelatedType, relatedType);
        //查询最新的记录
        queryWrapper.orderByDesc(File::getCreateAt);
        queryWrapper.last("limit 1");
        File file = getOne(queryWrapper);
        return file == null ? null : BASE_URL + file.getId();
    }
}
