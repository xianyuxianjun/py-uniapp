package com.pangying.controller;

import com.pangying.entity.R;
import com.pangying.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-02-23
 */
@CrossOrigin
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private IFileService fileService;


    @GetMapping("/view/{fileId}")
    public ResponseEntity<Resource> viewImage(@PathVariable Long fileId) throws IOException {
        // 根据文件ID获取文件信息
        com.pangying.entity.File fileEntity = fileService.getById(fileId);
        if (fileEntity == null) {
            return ResponseEntity.notFound().build();
        }

        // 获取文件路径
        String filePath = fileEntity.getFilePath();
        File file = new File(filePath);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 创建Resource对象
        Resource resource = new FileSystemResource(file);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .body(resource);
    }

    @PostMapping("/upload")
    public R upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("relatedId") Long relatedId,
            @RequestParam("relatedType") String relatedType
    ) throws Exception {
        fileService.uploadFile(file,relatedType,relatedId);
        return R.success();
    }
}
