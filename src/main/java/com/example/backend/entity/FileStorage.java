package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.util.IdGenerator;

/**
 * 文件存储实体
 */
@TableName("file_storage")
public class FileStorage extends BaseEntity {

    private String fileUrl;
    private String folder;
    private Integer usageType;
    private String uploadUserId;
    private String fileName;
    private String contentType;
    private Long size;

    public FileStorage() {
        super();
        this.setId(IdGenerator.generateId("FS"));
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public Integer getUsageType() {
        return usageType;
    }

    public void setUsageType(Integer usageType) {
        this.usageType = usageType;
    }

    public String getUploadUserId() {
        return uploadUserId;
    }

    public void setUploadUserId(String uploadUserId) {
        this.uploadUserId = uploadUserId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}


