package com.example.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Size;

/**
 * 用户个人信息更新请求DTO
 */
@ApiModel("用户个人信息更新请求")
public class UserProfileRequest {
    
    @ApiModelProperty(value = "用户昵称", example = "张三")
    @Size(min = 2, max = 20, message = "昵称长度必须在2-20位之间")
    private String nickname;
    
    @ApiModelProperty(value = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    @ApiModelProperty(value = "个人简介", example = "这是我的个人简介")
    @Size(max = 200, message = "个人简介不能超过200字符")
    private String bio;
    
    
    @ApiModelProperty(value = "手机号", example = "13888888888")
    @Size(min = 11, max = 11, message = "手机号格式不正确")
    private String phone;
    
    // Getters and Setters
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
 
 
 