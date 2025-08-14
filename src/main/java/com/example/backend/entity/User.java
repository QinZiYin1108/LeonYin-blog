package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 用户实体类
 */
@TableName("user")
public class User extends BaseEntity {
    
    private String email;
    
    @JsonIgnore
    private String password;
    
    private String nickname;
    private String avatar;
    private String realName;
    private String idCard;
    private String phone;
    private Integer userType; // 0-普通用户，1-管理员
    private Integer status; // 0-禁用，1-正常
    private Integer registerType; // 0-邮箱注册，1-微信注册
    private String wechatOpenid;
    private Integer emailVerified; // 0-未验证，1-已验证
    private Integer realNameVerified; // 0-未认证，1-已认证
    
    public User() {
        super();
        // 生成U前缀的ID
        this.setId("U" + System.currentTimeMillis());
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
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getIdCard() {
        return idCard;
    }
    
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
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
    
    public String getWechatOpenid() {
        return wechatOpenid;
    }
    
    public void setWechatOpenid(String wechatOpenid) {
        this.wechatOpenid = wechatOpenid;
    }
    
    public Integer getEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(Integer emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    public Integer getRealNameVerified() {
        return realNameVerified;
    }
    
    public void setRealNameVerified(Integer realNameVerified) {
        this.realNameVerified = realNameVerified;
    }
} 