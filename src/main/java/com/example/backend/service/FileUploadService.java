package com.example.backend.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {
    
    /**
     * 上传文件到阿里云OSS
     * @param file 要上传的文件
     * @param folder 文件夹名称（如: avatar, article, etc.）
     * @return 文件的访问URL
     */
    String uploadFile(MultipartFile file, String folder);
    
    /**
     * 删除文件
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean deleteFile(String fileUrl);
    
    /**
     * 验证文件类型
     * @param file 文件
     * @param allowedTypes 允许的文件类型
     * @return 是否为允许的类型
     */
    boolean isValidFileType(MultipartFile file, String[] allowedTypes);
    
    /**
     * 验证文件大小
     * @param file 文件
     * @param maxSize 最大大小（字节）
     * @return 是否符合大小限制
     */
    boolean isValidFileSize(MultipartFile file, long maxSize);
}
 
 
 