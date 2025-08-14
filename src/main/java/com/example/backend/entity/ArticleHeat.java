package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.util.IdGenerator;

import java.math.BigDecimal;

@TableName("article_heat")
public class ArticleHeat extends BaseEntity {

    private String articleId;
    private Long viewCount;
    private Long likeCount;
    private Long collectCount;
    private BigDecimal hotScore;
    private Long publishTime;

    public ArticleHeat() {
        super();
        this.setId(IdGenerator.generateId("H"));
        this.viewCount = 0L;
        this.likeCount = 0L;
        this.collectCount = 0L;
        this.hotScore = BigDecimal.ZERO;
    }

    public String getArticleId() { return articleId; }
    public void setArticleId(String articleId) { this.articleId = articleId; }
    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
    public Long getLikeCount() { return likeCount; }
    public void setLikeCount(Long likeCount) { this.likeCount = likeCount; }
    public Long getCollectCount() { return collectCount; }
    public void setCollectCount(Long collectCount) { this.collectCount = collectCount; }
    public BigDecimal getHotScore() { return hotScore; }
    public void setHotScore(BigDecimal hotScore) { this.hotScore = hotScore; }
    public Long getPublishTime() { return publishTime; }
    public void setPublishTime(Long publishTime) { this.publishTime = publishTime; }
}






