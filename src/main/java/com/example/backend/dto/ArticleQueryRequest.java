package com.example.backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "文章多条件查询请求", description = "条件可为null，未提供则不参与筛选；无论有无条件均按热度排序")
public class ArticleQueryRequest {

    @ApiModelProperty(value = "页码(从1开始)", required = true, example = "1")
    private Integer current;

    @ApiModelProperty(value = "每页数量", required = true, example = "10")
    private Integer size;

    @ApiModelProperty(value = "分类ID，可为空")
    private String categoryId;

    @ApiModelProperty(value = "关键词(匹配标题/摘要)，可为空")
    private String keyword;

    public Integer getCurrent() { return current; }
    public void setCurrent(Integer current) { this.current = current; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
}






