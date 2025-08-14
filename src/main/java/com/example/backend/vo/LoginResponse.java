package com.example.backend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("登录响应数据")
public class LoginResponse {

    @ApiModelProperty("JWT访问令牌")
    private String token;

    @ApiModelProperty("用户信息")
    private Object user;

    @ApiModelProperty("是否需要验证码：true-需要验证码，false-不需要")
    private Boolean needVerification;

    @ApiModelProperty("提示信息")
    private String message;

    public LoginResponse() {}

    public LoginResponse(String token, Object user) {
        this.token = token;
        this.user = user;
        this.needVerification = false;
    }

    public LoginResponse(String token, Object user, Boolean needVerification, String message) {
        this.token = token;
        this.user = user;
        this.needVerification = needVerification;
        this.message = message;
    }

    public LoginResponse(Boolean needVerification, String message) {
        this.needVerification = needVerification;
        this.message = message;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Object getUser() { return user; }
    public void setUser(Object user) { this.user = user; }
    public Boolean getNeedVerification() { return needVerification; }
    public void setNeedVerification(Boolean needVerification) { this.needVerification = needVerification; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}






