package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.util.IdGenerator;

/**
 * 图片存储实体
 */
@TableName("image_storage")
public class ImageStorage extends BaseEntity {

    private String fileId; // 对应file_storage记录ID
    private Integer width;
    private Integer height;
    private Integer usageType; // 0-通用
    private String altText;

    public ImageStorage() {
        super();
        this.setId(IdGenerator.generateId("IMG"));
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getUsageType() {
        return usageType;
    }

    public void setUsageType(Integer usageType) {
        this.usageType = usageType;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }
}


