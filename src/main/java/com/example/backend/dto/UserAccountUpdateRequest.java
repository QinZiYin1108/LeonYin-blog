package com.example.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "管理员更新用户信息请求", description = "仅包含可变更的用户字段")
public class UserAccountUpdateRequest {

	@ApiModelProperty(value = "用户类型：0-普通用户 1-管理员", example = "0")
	private Integer userType;

	@ApiModelProperty(value = "状态：0-禁用 1-正常", example = "1")
	private Integer status;

	@ApiModelProperty(value = "邮箱是否已验证：0-未验证 1-已验证", example = "1")
	private Integer emailVerified;

	public Integer getUserType() { return userType; }
	public void setUserType(Integer userType) { this.userType = userType; }
	public Integer getStatus() { return status; }
	public void setStatus(Integer status) { this.status = status; }
	public Integer getEmailVerified() { return emailVerified; }
	public void setEmailVerified(Integer emailVerified) { this.emailVerified = emailVerified; }
}






