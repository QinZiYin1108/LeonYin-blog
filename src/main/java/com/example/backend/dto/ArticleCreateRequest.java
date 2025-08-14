package com.example.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "发布文章请求", description = "用于Swagger显示精简字段，仅包含前端需要填写的属性，其他由后端自动生成")
public class ArticleCreateRequest {

    @ApiModelProperty(value = "标题", required = true, example = "我的第一篇文章")
    private String title;

    @ApiModelProperty(value = "摘要", example = "这是摘要")
    private String summary;

    @ApiModelProperty(value = "正文内容", required = true, example = "这是文章内容……")
    private String content;

    @ApiModelProperty(value = "封面图片ID（可选）", example = "IMG114051964087011")
    private String coverImageId;

    @ApiModelProperty(value = "分类ID", required = true, example = "C114051964087011")
    private String categoryId;

    @ApiModelProperty(value = "标签（逗号分隔或JSON数组字符串）", example = "java,spring")
    private String tags;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCoverImageId() { return coverImageId; }
    public void setCoverImageId(String coverImageId) { this.coverImageId = coverImageId; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
}


