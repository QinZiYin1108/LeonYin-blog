package com.example.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@ApiModel(value = "找回密码请求", description = "通过邮箱验证码验证后设置新密码")
public class ForgotPasswordRequest {

    @ApiModelProperty(value = "邮箱", required = true, example = "user@example.com")
    @NotBlank
    @Email
    private String email;

    @ApiModelProperty(value = "邮箱验证码", required = true, example = "123456")
    @NotBlank
    private String verificationCode;

    @ApiModelProperty(value = "新密码", required = true, example = "Abc123456!")
    @NotBlank
    private String newPassword;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}






