package com.example.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("重置用户密码请求")
public class ResetPasswordRequest {
    @ApiModelProperty(value = "新密码", required = true, example = "Abc123456!")
    private String newPassword;

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}






