package com.example.backend.service.impl;

import com.example.backend.entity.FileStorage;
import com.example.backend.mapper.FileStorageMapper;
import com.example.backend.service.FileStorageService;
import com.example.backend.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务实现
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private FileStorageMapper fileStorageMapper;

    @Override
    public FileStorage uploadAndSave(MultipartFile file, String folder, Integer usageType, String uploadUserId) {
        String url = fileUploadService.uploadFile(file, folder);

        FileStorage record = new FileStorage();
        record.setFileUrl(url);
        record.setFolder(folder);
        record.setUsageType(usageType == null ? 0 : usageType);
        record.setUploadUserId(uploadUserId);
        record.setFileName(file.getOriginalFilename());
        record.setContentType(file.getContentType());
        record.setSize(file.getSize());

        fileStorageMapper.insert(record);
        return record;
    }

    @Override
    public String getFileUrl(String fileId) {
        FileStorage fs = fileStorageMapper.selectById(fileId);
        return fs == null ? null : fs.getFileUrl();
    }

    @Override
    public boolean deleteFile(String fileId) {
        FileStorage fs = fileStorageMapper.selectById(fileId);
        if (fs == null) {
            return false;
        }
        boolean storageDeleted = fileUploadService.deleteFile(fs.getFileUrl());
        int rows = fileStorageMapper.deleteById(fileId);
        return storageDeleted && rows > 0;
    }
}





