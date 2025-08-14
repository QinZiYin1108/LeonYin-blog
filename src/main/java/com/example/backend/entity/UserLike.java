package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.util.IdGenerator;

/**
 * 用户点赞实体类
 */
@TableName("user_like")
public class UserLike extends BaseEntity {
    
    private String userId;
    private String targetId;
    private Integer targetType; // 0-文章，1-评论
    
    public UserLike() {
        super();
        // 生成L前缀的ID
        this.setId(IdGenerator.generateLikeId());
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getTargetId() {
        return targetId;
    }
    
    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
    
    public Integer getTargetType() {
        return targetType;
    }
    
    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }
} 