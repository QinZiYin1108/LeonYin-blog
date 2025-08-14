package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.util.IdGenerator;

/**
 * 用户收藏实体类
 */
@TableName("user_collect")
public class UserCollect extends BaseEntity {
    
    private String userId;
    private String articleId;
    
    public UserCollect() {
        super();
        // 生成CO前缀的ID
        this.setId(IdGenerator.generateCollectId());
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getArticleId() {
        return articleId;
    }
    
    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
} 