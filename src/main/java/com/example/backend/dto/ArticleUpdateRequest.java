package com.example.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "更新文章请求", description = "只包含允许更新的字段，未填写的字段保持不变")
public class ArticleUpdateRequest {

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "摘要")
    private String summary;

    @ApiModelProperty(value = "正文内容")
    private String content;

    @ApiModelProperty(value = "封面图片ID")
    private String coverImageId;

    @ApiModelProperty(value = "分类ID")
    private String categoryId;

    @ApiModelProperty(value = "标签（逗号分隔或JSON数组字符串）")
    private String tags;

    @ApiModelProperty(value = "文章状态：0-下架 1-正常")
    private Integer status;

    @ApiModelProperty(value = "是否置顶：0-否 1-是")
    private Integer isTop;

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
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getIsTop() { return isTop; }
    public void setIsTop(Integer isTop) { this.isTop = isTop; }
}






