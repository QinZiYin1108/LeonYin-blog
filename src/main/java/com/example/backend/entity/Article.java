package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.util.IdGenerator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

/**
 * 博客文章实体类
 */
@TableName("article")
public class Article extends BaseEntity {
    
    private String title;
    private String summary;
    private String content;
    private String coverImageId; // 封面图片ID（关联image_storage表）
    private String authorId;
    private String categoryId;
    private String tags; // JSON字符串存储标签数组
    @TableField(exist = false)
    private Integer viewCount;
    @TableField(exist = false)
    private Integer likeCount;
    @TableField(exist = false)
    private Integer collectCount;
    @TableField(exist = false)
    private Integer commentCount;
    @TableField(exist = false)
    private BigDecimal hotScore;
    private Integer status; // 0-下架，1-正常
    private Integer isTop; // 0-否，1-是
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Long publishTime;
    
    public Article() {
        super();
        this.setId(IdGenerator.generateId("A"));
        this.viewCount = 0;
        this.likeCount = 0;
        this.collectCount = 0;
        this.commentCount = 0;
        this.hotScore = BigDecimal.ZERO;
        this.status = 1;
        this.isTop = 0;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getCoverImageId() {
        return coverImageId;
    }
    
    public void setCoverImageId(String coverImageId) {
        this.coverImageId = coverImageId;
    }
    
    public String getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
    
    public String getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    
    public Integer getCollectCount() {
        return collectCount;
    }
    
    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }
    
    public Integer getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
    
    public BigDecimal getHotScore() {
        return hotScore;
    }
    
    public void setHotScore(BigDecimal hotScore) {
        this.hotScore = hotScore;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getIsTop() {
        return isTop;
    }
    
    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }
    
    public Long getPublishTime() {
        return publishTime;
    }
    
    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }
    
}