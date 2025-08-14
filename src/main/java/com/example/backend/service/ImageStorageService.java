package com.example.backend.service;

import com.example.backend.entity.ImageStorage;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片存储服务接口
 */
public interface ImageStorageService {

    /**
     * 上传图片并保存数据库记录
     */
    ImageStorage uploadImageAndSave(MultipartFile file, String folder, Integer usageType, String uploadUserId, String altText);

    /**
     * 根据图片ID获取访问URL
     */
    String getImageUrl(String imageId, String style);

    /**
     * 根据图片ID删除图片（包含OSS文件与两张表记录）
     */
    boolean deleteImageById(String imageId);
}



