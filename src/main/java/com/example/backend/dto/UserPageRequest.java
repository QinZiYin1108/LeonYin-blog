package com.example.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 用户分页查询请求DTO
 */
@ApiModel("用户分页查询请求")
public class UserPageRequest {
    
    @ApiModelProperty(value = "页码", required = true, example = "1")
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum;
    
    @ApiModelProperty(value = "每页大小", required = true, example = "10")
    @NotNull(message = "每页大小不能为空")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer pageSize;
    
    @ApiModelProperty(value = "搜索关键词（邮箱或昵称）", example = "张三")
    private String keyword;
    
    @ApiModelProperty(value = "邮箱筛选", example = "user@example.com")
    private String email;
    
    @ApiModelProperty(value = "用户类型（0-普通用户，1-管理员）", example = "0")
    private Integer userType;
    
    @ApiModelProperty(value = "账号状态（0-禁用，1-正常）", example = "1")
    private Integer status;
    
    @ApiModelProperty(value = "邮箱验证状态（0-未验证，1-已验证）", example = "1")
    private Integer emailVerified;
    

    
    // Getters and Setters
    public Integer getPageNum() {
        return pageNum;
    }
    
    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
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
    
    public Integer getEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(Integer emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    // 兼容性方法
    public Integer getCurrent() {
        return pageNum;
    }
    
    public Integer getSize() {
        return pageSize;
    }
}
 
 
 