package com.example.backend.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 文章返回给前端的视图对象
 */
public class ArticleVO {

    private String id;
    private String title;
    private String summary;
    private String content;
    private String coverImageId;
    private String authorId;
    private String categoryId;
    private String tags;
    private Integer viewCount;
    private Integer likeCount;
    private Integer collectCount;
    private BigDecimal hotScore;
    private Integer status;
    private Integer isTop;
    private Long publishTime;

    // 展示字段
    private String authorName;
    private String categoryName;
    private List<String> tagList;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCoverImageId() { return coverImageId; }
    public void setCoverImageId(String coverImageId) { this.coverImageId = coverImageId; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public Integer getCollectCount() { return collectCount; }
    public void setCollectCount(Integer collectCount) { this.collectCount = collectCount; }
    public BigDecimal getHotScore() { return hotScore; }
    public void setHotScore(BigDecimal hotScore) { this.hotScore = hotScore; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getIsTop() { return isTop; }
    public void setIsTop(Integer isTop) { this.isTop = isTop; }
    public Long getPublishTime() { return publishTime; }
    public void setPublishTime(Long publishTime) { this.publishTime = publishTime; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public List<String> getTagList() { return tagList; }
    public void setTagList(List<String> tagList) { this.tagList = tagList; }
}


