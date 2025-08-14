package com.example.backend.service.impl;

import com.example.backend.entity.FileStorage;
import com.example.backend.entity.ImageStorage;
import com.example.backend.mapper.FileStorageMapper;
import com.example.backend.mapper.ImageStorageMapper;
import com.example.backend.service.FileUploadService;
import com.example.backend.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片存储服务实现
 */
@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private FileStorageMapper fileStorageMapper;

    @Autowired
    private ImageStorageMapper imageStorageMapper;

    @Override
    public ImageStorage uploadImageAndSave(MultipartFile file, String folder, Integer usageType, String uploadUserId, String altText) {
        // 先上传原始文件，保存file_storage
        FileStorage fileStorage = new FileStorage();
        String url = fileUploadService.uploadFile(file, folder);
        fileStorage.setFileUrl(url);
        fileStorage.setFolder(folder);
        fileStorage.setUsageType(usageType == null ? 0 : usageType);
        fileStorage.setUploadUserId(uploadUserId);
        fileStorage.setFileName(file.getOriginalFilename());
        fileStorage.setContentType(file.getContentType());
        fileStorage.setSize(file.getSize());
        fileStorageMapper.insert(fileStorage);

        // 保存image_storage
        ImageStorage imageStorage = new ImageStorage();
        imageStorage.setFileId(fileStorage.getId());
        imageStorage.setUsageType(usageType == null ? 0 : usageType);
        imageStorage.setAltText(altText);
        imageStorageMapper.insert(imageStorage);
        return imageStorage;
    }

    @Override
    public String getImageUrl(String imageId, String style) {
        ImageStorage img = imageStorageMapper.selectById(imageId);
        if (img == null) {
            return null;
        }
        FileStorage fs = fileStorageMapper.selectById(img.getFileId());
        return fs == null ? null : fs.getFileUrl();
    }

    @Override
    public boolean deleteImageById(String imageId) {
        ImageStorage img = imageStorageMapper.selectById(imageId);
        if (img == null) {
            return false;
        }
        FileStorage fs = fileStorageMapper.selectById(img.getFileId());
        boolean ossDeleted = true;
        if (fs != null && fs.getFileUrl() != null) {
            // 删除OSS文件
            ossDeleted = fileUploadService.deleteFile(fs.getFileUrl());
        }
        // 删除数据库记录
        if (fs != null) {
            fileStorageMapper.deleteById(fs.getId());
        }
        imageStorageMapper.deleteById(imageId);
        return ossDeleted;
    }
}


