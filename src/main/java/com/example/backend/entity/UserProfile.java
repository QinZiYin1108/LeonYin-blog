package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.util.IdGenerator;

/**
 * 用户信息实体类（个人资料信息）
 */
@TableName("user_profile")
public class UserProfile extends BaseEntity {
    
    private String userId; // 关联的用户账号ID
    private String nickname;
    private String avatarImageId; // 头像图片ID（关联image_storage表）
    private String bio; // 个人简介
    private String phone; // varchar(11)
    
    public UserProfile() {
        super();
        // 生成UP前缀的ID
        this.setId(IdGenerator.generateId("UP"));
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getAvatarImageId() {
        return avatarImageId;
    }
    
    public void setAvatarImageId(String avatarImageId) {
        this.avatarImageId = avatarImageId;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
}



