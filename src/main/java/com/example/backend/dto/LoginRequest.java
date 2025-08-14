package com.example.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 登录请求DTO
 */
@ApiModel("登录请求参数")
public class LoginRequest {
    
    @ApiModelProperty(value = "登录类型：1-邮箱验证码登录，2-邮箱密码登录", required = true, example = "2")
    @NotNull(message = "登录类型不能为空")
    private Integer loginType;
    
    @ApiModelProperty(value = "邮箱地址", required = true, example = "user@example.com")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @ApiModelProperty(value = "密码（邮箱密码登录时必填）", example = "123456")
    private String password;
    
    @ApiModelProperty(value = "验证码（邮箱验证码登录或首次密码登录时必填）", example = "123456")
    private String verificationCode;
    
    @ApiModelProperty(value = "设备标识（用于判断是否首次登录）", example = "device_12345")
    private String deviceId;
    
    @ApiModelProperty(value = "设备信息", example = "Chrome 120.0.0.0 Windows 10")
    private String deviceInfo;
    
    public LoginRequest() {}
    
    // Getters and Setters
    public Integer getLoginType() {
        return loginType;
    }
    
    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }
    
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
    
    public String getVerificationCode() {
        return verificationCode;
    }
    
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getDeviceInfo() {
        return deviceInfo;
    }
    
    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
} 
 