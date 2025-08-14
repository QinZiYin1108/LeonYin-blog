package com.example.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 邮箱验证响应DTO
 */
@ApiModel("邮箱验证响应")
public class EmailVerificationResponse {
    
    @ApiModelProperty(value = "邮箱验证token", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String verificationToken;
    
    @ApiModelProperty(value = "邮箱地址", example = "user@example.com")
    private String email;
    
    @ApiModelProperty(value = "验证用途", example = "0")
    private Integer purpose;
    
    @ApiModelProperty(value = "token有效期（分钟）", example = "30")
    private Integer expiresInMinutes;
    
    public EmailVerificationResponse() {}
    
    public EmailVerificationResponse(String verificationToken, String email, Integer purpose, Integer expiresInMinutes) {
        this.verificationToken = verificationToken;
        this.email = email;
        this.purpose = purpose;
        this.expiresInMinutes = expiresInMinutes;
    }
    
    // Getters and Setters
    public String getVerificationToken() {
        return verificationToken;
    }
    
    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Integer getPurpose() {
        return purpose;
    }
    
    public void setPurpose(Integer purpose) {
        this.purpose = purpose;
    }
    
    public Integer getExpiresInMinutes() {
        return expiresInMinutes;
    }
    
    public void setExpiresInMinutes(Integer expiresInMinutes) {
        this.expiresInMinutes = expiresInMinutes;
    }
}
 
 
 