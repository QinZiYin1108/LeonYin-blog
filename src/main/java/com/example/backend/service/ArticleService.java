package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.PageResult;
import com.example.backend.entity.Article;

/**
 * 文章服务接口
 */
public interface ArticleService extends IService<Article> {
    
    /**
     * 发布文章
     */
    Article publishArticle(Article article);
    
    /**
     * 更新文章
     */
    boolean updateArticle(String articleId, Article article);
    
    /**
     * 删除文章
     */
    boolean deleteArticle(String articleId);
    
    /**
     * 获取文章详情（会增加浏览量）
     */
    Article getArticleDetail(String articleId, String userId);
    
    /**
     * 分页查询文章（按热度排序）
     */
    PageResult<Article> pageArticles(Integer current, Integer size, String categoryId, String keyword);
    
    /**
     * 点赞文章
     */
    boolean likeArticle(String articleId, String userId);
    
    /**
     * 取消点赞文章
     */
    boolean unlikeArticle(String articleId, String userId);
    
    /**
     * 收藏文章
     */
    boolean collectArticle(String articleId, String userId);
    
    /**
     * 取消收藏文章
     */
    boolean uncollectArticle(String articleId, String userId);
    
    /**
     * 更新文章热度分数
     */
    void updateHotScore(String articleId);
    
    /**
     * 获取用户收藏的文章
     */
    PageResult<Article> getUserCollectedArticles(String userId, Integer current, Integer size);
    
    /**
     * 置顶/取消置顶文章
     */
    boolean toggleTopArticle(String articleId);

    /** 是否已点赞 */
    boolean hasLiked(String articleId, String userId);

    /** 是否已收藏 */
    boolean hasCollected(String articleId, String userId);
} 