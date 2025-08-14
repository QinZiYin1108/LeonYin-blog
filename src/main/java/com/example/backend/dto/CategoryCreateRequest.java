package com.example.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("创建分类请求")
public class CategoryCreateRequest {

	@ApiModelProperty(value = "分类名称", required = true, example = "技术分享")
	private String name;

	@ApiModelProperty(value = "分类描述", example = "关于后端/前端/架构的技术文章")
	private String description;

	@ApiModelProperty(value = "图标图片ID(可选)", example = "IMG114051964087011")
	private String iconImageId;

	@ApiModelProperty(value = "排序权重，越小越靠前", example = "0")
	private Integer sortOrder;

	@ApiModelProperty(value = "状态：0-禁用，1-启用", example = "1")
	private Integer status;

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public String getIconImageId() { return iconImageId; }
	public void setIconImageId(String iconImageId) { this.iconImageId = iconImageId; }
	public Integer getSortOrder() { return sortOrder; }
	public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
	public Integer getStatus() { return status; }
	public void setStatus(Integer status) { this.status = status; }
}






