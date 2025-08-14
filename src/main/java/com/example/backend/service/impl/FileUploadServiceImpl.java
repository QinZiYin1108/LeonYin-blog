package com.example.backend.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.example.backend.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * 文件上传服务实现类
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {
    
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    
    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;
    
    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;
    
    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;
    
    @Value("${aliyun.oss.url-prefix}")
    private String urlPrefix;
    
    private OSS ossClient;
    
    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
    
    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
    
    @Override
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        
        try {
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + extension;
            
            // 生成文件路径：folder/yyyy/MM/dd/fileName
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String datePath = dateFormat.format(new Date());
            String objectKey = folder + "/" + datePath + "/" + fileName;
            
            // 设置文件元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            
            // 上传文件
            ossClient.putObject(bucketName, objectKey, file.getInputStream(), metadata);
            
            // 返回文件访问URL
            return urlPrefix + "/" + objectKey;
            
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(urlPrefix)) {
            return false;
        }
        
        try {
            // 提取对象key
            String objectKey = fileUrl.substring(urlPrefix.length() + 1);
            ossClient.deleteObject(bucketName, objectKey);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isValidFileType(MultipartFile file, String[] allowedTypes) {
        if (file == null || allowedTypes == null) {
            return false;
        }
        
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        
        return Arrays.asList(allowedTypes).contains(contentType);
    }
    
    @Override
    public boolean isValidFileSize(MultipartFile file, long maxSize) {
        if (file == null) {
            return false;
        }
        
        return file.getSize() <= maxSize;
    }
}
 
 
 