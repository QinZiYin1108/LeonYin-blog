package com.example.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("修改用户状态请求")
public class ChangeUserStatusRequest {
    @ApiModelProperty(value = "状态：0-禁用 1-正常", required = true, example = "1")
    private Integer status;

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}






