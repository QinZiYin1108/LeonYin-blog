package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.util.IdGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 用户账号实体类（登录认证相关信息）
 */
@TableName("user_account")
public class UserAccount extends BaseEntity {
    
    private String email;
    
    @JsonIgnore
    private String password;
    
    private Integer userType; // 0-普通用户，1-管理员
    private Integer status; // 0-禁用，1-正常
    private Integer registerType; // 0-邮箱注册
    private Integer emailVerified; // 0-未验证，1-已验证
    private Long lastLoginTime; // 最后登录时间
    
    public UserAccount() {
        super();
        // 生成U前缀的ID
        this.setId(IdGenerator.generateUserId());
    }
    
    /**
     * 获取用户角色字符串（用于JWT）
     */
    public String getRole() {
        return userType == 1 ? "ADMIN" : "USER";
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Integer getUserType() {
        return userType;
    }
    
    public void setUserType(Integer userType) {
        this.userType = userType;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getRegisterType() {
        return registerType;
    }
    
    public void setRegisterType(Integer registerType) {
        this.registerType = registerType;
    }
    
    public Integer getEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(Integer emailVerified) {
        this.emailVerified = emailVerified;
    }
    

    
    public Long getLastLoginTime() {
        return lastLoginTime;
    }
    
    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}



