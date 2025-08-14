package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 评论实体类
 */
@TableName("comment")
public class Comment extends BaseEntity {
    
    private String articleId;
    private String userId;
    private String parentId;
    private String content;
    private Integer likeCount;
    private Integer status; // 0-删除，1-正常，2-待审核
    
    public Comment() {
        super();
        // 生成CM前缀的ID
        this.setId("CM" + System.currentTimeMillis());
        this.likeCount = 0;
        this.status = 1;
    }
    
    // Getters and Setters
    public String getArticleId() {
        return articleId;
    }
    
    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getParentId() {
        return parentId;
    }
    
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
} 