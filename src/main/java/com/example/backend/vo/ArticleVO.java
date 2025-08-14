package com.example.backend.vo;

import com.example.backend.entity.Article;

import java.util.List;

/**
 * 文章响应对象：在实体基础上添加前端展示字段
 */
public class ArticleVO extends Article {

    private String authorName;
    private String categoryName;
    private List<String> tagList;
    private Boolean liked;
    private Boolean collected;

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public List<String> getTagList() { return tagList; }
    public void setTagList(List<String> tagList) { this.tagList = tagList; }

    public Boolean getLiked() { return liked; }
    public void setLiked(Boolean liked) { this.liked = liked; }
    public Boolean getCollected() { return collected; }
    public void setCollected(Boolean collected) { this.collected = collected; }
}



