package com.example.backend.vo;

import com.example.backend.entity.Category;

/**
 * 分类响应对象：在实体基础上添加前端展示字段
 */
public class CategoryVO extends Category {

    private Integer articleCount;
    private String iconUrl;

    public Integer getArticleCount() { return articleCount; }
    public void setArticleCount(Integer articleCount) { this.articleCount = articleCount; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
}


