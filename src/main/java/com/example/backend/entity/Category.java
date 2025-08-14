package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.util.IdGenerator;

/**
 * 文章分类实体类
 */
@TableName("category")
public class Category extends BaseEntity {
    
    private String name;
    private String description;
    private String iconImageId; // 分类图标ID（关联image_storage表）
    private Integer sortOrder;
    private Integer status; // 0-禁用，1-启用

    public Category() {
        super();
        this.setId(IdGenerator.generateId("C"));
        this.sortOrder = 0;
        this.status = 1;
    }
    
    public Category(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getIconImageId() {
        return iconImageId;
    }
    
    public void setIconImageId(String iconImageId) {
        this.iconImageId = iconImageId;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }

}