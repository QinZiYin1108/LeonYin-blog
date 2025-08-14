package com.example.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 注册请求DTO
 */
@ApiModel("注册请求参数")
public class RegisterRequest {
    
    @ApiModelProperty(value = "邮箱地址", required = true, example = "user@example.com")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @ApiModelProperty(value = "密码", required = true, example = "123456")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;
    
    @ApiModelProperty(value = "昵称（可选，不填则使用邮箱前缀）", required = false, example = "张三")
    @Size(min = 2, max = 20, message = "昵称长度必须在2-20位之间")
    private String nickname;
    
    @ApiModelProperty(value = "注册类型：0-邮箱注册", required = true, example = "0")
    @NotNull(message = "注册类型不能为空")
    private Integer registerType; // 0-邮箱注册
    
    @ApiModelProperty(value = "邮箱验证码", required = true, example = "123456")
    @NotBlank(message = "验证码不能为空")
    private String verificationCode;
    
    public RegisterRequest() {}
    
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
    
    public Integer getRegisterType() {
        return registerType;
    }
    
    public void setRegisterType(Integer registerType) {
        this.registerType = registerType;
    }
    
    public String getVerificationCode() {
        return verificationCode;
    }
    
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
} 
 
 
 