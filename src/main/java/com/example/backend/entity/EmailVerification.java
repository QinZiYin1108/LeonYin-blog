package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 邮箱验证码实体类
 */
@TableName("email_verification")
public class EmailVerification extends BaseEntity {
    
    private String email;
    private String code;
    private Integer purpose; // 0-注册，1-登录，2-重置密码
    private Integer used; // 0-未使用，1-已使用
    private Long expireTime;
    
    public EmailVerification() {
        super();
        // 生成EV前缀的ID
        this.setId("EV" + System.currentTimeMillis());
        this.used = 0;
        // 默认5分钟过期
        this.expireTime = System.currentTimeMillis() + 5 * 60 * 1000;
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public Integer getPurpose() {
        return purpose;
    }
    
    public void setPurpose(Integer purpose) {
        this.purpose = purpose;
    }
    
    public Integer getUsed() {
        return used;
    }
    
    public void setUsed(Integer used) {
        this.used = used;
    }
    
    public Long getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
} 