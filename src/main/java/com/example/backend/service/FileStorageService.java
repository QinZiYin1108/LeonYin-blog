package com.example.backend.service;

import com.example.backend.entity.FileStorage;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务接口
 */
public interface FileStorageService {

    /**
     * 上传文件并保存数据库记录
     */
    FileStorage uploadAndSave(MultipartFile file, String folder, Integer usageType, String uploadUserId);

    /**
     * 根据文件ID获取访问URL
     */
    String getFileUrl(String fileId);

    /**
     * 根据文件ID删除文件（含数据库记录）
     */
    boolean deleteFile(String fileId);
}






