package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.common.PageResult;
import com.example.backend.entity.Article;
import com.example.backend.entity.UserCollect;
import com.example.backend.entity.UserLike;
import com.example.backend.mapper.ArticleMapper;
import com.example.backend.mapper.UserCollectMapper;
import com.example.backend.mapper.UserLikeMapper;
import com.example.backend.mapper.ArticleHeatMapper;
import com.example.backend.service.ArticleService;
import com.example.backend.service.ArticleHeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 文章服务实现类
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    
    @Autowired
    private ArticleMapper articleMapper;
    
    @Autowired
    private UserLikeMapper userLikeMapper;
    
    @Autowired
    private UserCollectMapper userCollectMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ArticleHeatService articleHeatService;

    @Autowired
    private ArticleHeatMapper articleHeatMapper;
    
    @Value("${blog.hot-score.view-weight:1.0}")
    private Double viewWeight;
    
    @Value("${blog.hot-score.like-weight:2.0}")
    private Double likeWeight;
    
    @Value("${blog.hot-score.comment-weight:0.0}")
    private Double commentWeight;
    
    @Value("${blog.hot-score.collect-weight:4.0}")
    private Double collectWeight;
    
    @Value("${blog.hot-score.time-decay-factor:0.1}")
    private Double timeDecayFactor;
    
    @Override
    @org.springframework.transaction.annotation.Transactional
    public Article publishArticle(Article article) {
        // 规范化 tags：允许逗号分隔字符串或JSON数组字符串，统一存JSON数组
        article.setTags(normalizeTags(article.getTags()));
        article.setStatus(1); // 正常状态
        article.setPublishTime(System.currentTimeMillis());
        articleMapper.insert(article);

        // 初始化热度表记录
        com.example.backend.entity.ArticleHeat heat = new com.example.backend.entity.ArticleHeat();
        heat.setArticleId(article.getId());
        heat.setPublishTime(article.getPublishTime());
        articleHeatService.save(heat);

        // 触发热度计算（定时任务合并）
        updateHotScore(article.getId());
        
        return article;
    }
    
    @Override
    public boolean updateArticle(String articleId, Article article) {
        Article existArticle = articleMapper.selectById(articleId);
        if (existArticle == null) {
            return false;
        }
        
        article.setId(articleId);
        if (article.getTags() != null) {
            article.setTags(normalizeTags(article.getTags()));
        }
        article.updateTime();
        return articleMapper.updateById(article) > 0;
    }
    
    @Override
    public boolean deleteArticle(String articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            return false;
        }
        
        article.setStatus(0); // 下架
        article.updateTime();
        return articleMapper.updateById(article) > 0;
    }
    
    @Override
    public Article getArticleDetail(String articleId, String userId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null || article.getStatus() == 0) {
            return null;
        }
        
        // 只有登录用户的浏览才增加浏览量
        if (StringUtils.hasText(userId)) {
            // 使用Redis防止重复计数（同一用户1小时内只计算一次浏览）
            String viewKey = "article:view:" + articleId + ":" + userId;
            if (!redisTemplate.hasKey(viewKey)) {
                // 增加浏览量（增量写入Redis）
                articleHeatService.incrView(articleId, 1);
                
                // 设置Redis标记，1小时过期
                redisTemplate.opsForValue().set(viewKey, "1", 1, TimeUnit.HOURS);
                
                // 异步更新热度分数
                updateHotScore(articleId);
            }
        }
        
        return article;
    }
    
    @Override
    public PageResult<Article> pageArticles(Integer current, Integer size, String categoryId, String keyword) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 只查询正常状态的文章
        
        if (StringUtils.hasText(categoryId)) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("title", keyword)
                       .or()
                       .like("summary", keyword);
        }
        
        // 若带筛选条件，先用文章表筛选；否则走热度表排序
        long offset = (long) (current - 1) * size;
        java.util.List<Article> list = articleMapper.selectByFiltersOrderByHeat(categoryId, keyword, offset, size);
        long total = articleMapper.countByFilters(categoryId, keyword);
        return PageResult.of(list, total, (long) current, (long) size);
    }
    
    @Override
    public boolean likeArticle(String articleId, String userId) {
        // 检查是否已点赞
        QueryWrapper<UserLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("target_id", articleId)
                   .eq("target_type", 0); // 0表示文章
        
        UserLike existLike = userLikeMapper.selectOne(queryWrapper);
        if (existLike != null) {
            return false; // 已经点赞过
        }
        
        // 添加点赞记录
        UserLike userLike = new UserLike();
        userLike.setUserId(userId);
        userLike.setTargetId(articleId);
        userLike.setTargetType(0);
        userLikeMapper.insert(userLike);
        
        // 增加点赞（Redis）
        articleHeatService.incrLike(articleId, 1);
        updateHotScore(articleId);
        
        return true;
    }
    
    @Override
    public boolean unlikeArticle(String articleId, String userId) {
        // 删除点赞记录
        QueryWrapper<UserLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("target_id", articleId)
                   .eq("target_type", 0);
        
        int deleteCount = userLikeMapper.delete(queryWrapper);
        if (deleteCount == 0) {
            return false; // 没有点赞记录
        }
        
        // 减少点赞（Redis）
        articleHeatService.incrLike(articleId, -1);
        updateHotScore(articleId);
        
        return true;
    }
    
    @Override
    public boolean collectArticle(String articleId, String userId) {
        // 检查是否已收藏
        QueryWrapper<UserCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("article_id", articleId);
        
        UserCollect existCollect = userCollectMapper.selectOne(queryWrapper);
        if (existCollect != null) {
            return false; // 已经收藏过
        }
        
        // 添加收藏记录
        UserCollect userCollect = new UserCollect();
        userCollect.setUserId(userId);
        userCollect.setArticleId(articleId);
        userCollectMapper.insert(userCollect);
        
        // 增加收藏（Redis）
        articleHeatService.incrCollect(articleId, 1);
        updateHotScore(articleId);
        
        return true;
    }
    
    @Override
    public boolean uncollectArticle(String articleId, String userId) {
        // 删除收藏记录
        QueryWrapper<UserCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("article_id", articleId);
        
        int deleteCount = userCollectMapper.delete(queryWrapper);
        if (deleteCount == 0) {
            return false; // 没有收藏记录
        }
        
        // 减少收藏（Redis）
        articleHeatService.incrCollect(articleId, -1);
        updateHotScore(articleId);
        
        return true;
    }
    
    @Override
    public void updateHotScore(String articleId) {
        // No-op：实际计算在定时任务合并Redis增量时执行
    }

    @Override
    public boolean hasLiked(String articleId, String userId) {
        if (!StringUtils.hasText(userId)) return false;
        QueryWrapper<UserLike> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).eq("target_id", articleId).eq("target_type", 0);
        return userLikeMapper.selectCount(qw) > 0;
    }

    @Override
    public boolean hasCollected(String articleId, String userId) {
        if (!StringUtils.hasText(userId)) return false;
        QueryWrapper<UserCollect> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).eq("article_id", articleId);
        return userCollectMapper.selectCount(qw) > 0;
    }

    private String normalizeTags(String raw) {
        try {
            if (!org.springframework.util.StringUtils.hasText(raw)) {
                return null;
            }
            String trimmed = raw.trim();
            // 已经是JSON数组，直接返回
            if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                return trimmed;
            }
            // 逗号分隔转JSON数组
            String[] parts = trimmed.split(",");
            List<String> list = new ArrayList<>();
            for (String p : parts) {
                String v = p.trim();
                if (v.length() > 0) {
                    list.add(v);
                }
            }
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return list.isEmpty() ? null : mapper.writeValueAsString(list);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public PageResult<Article> getUserCollectedArticles(String userId, Integer current, Integer size) {
        // 查询用户收藏记录
        QueryWrapper<UserCollect> collectQueryWrapper = new QueryWrapper<>();
        collectQueryWrapper.eq("user_id", userId);
        collectQueryWrapper.orderByDesc("create_time");

        IPage<UserCollect> collectPage = new Page<>(current, size);
        IPage<UserCollect> collectResult = userCollectMapper.selectPage(collectPage, collectQueryWrapper);

        if (collectResult.getRecords() == null || collectResult.getRecords().isEmpty()) {
            return PageResult.of(new java.util.ArrayList<>(), 0L, (long) current, (long) size);
        }

        java.util.List<String> articleIds = new java.util.ArrayList<>();
        for (UserCollect uc : collectResult.getRecords()) {
            if (uc != null && uc.getArticleId() != null) {
                articleIds.add(uc.getArticleId());
            }
        }
        if (articleIds.isEmpty()) {
            return PageResult.of(new java.util.ArrayList<>(), collectResult.getTotal(), collectResult.getCurrent(), collectResult.getSize());
        }

        // 批量获取文章并过滤下架
        java.util.List<Article> articles = articleMapper.selectBatchIds(articleIds);
        java.util.Map<String, Article> idToArticle = new java.util.HashMap<>();
        if (articles != null) {
            for (Article a : articles) {
                if (a != null && a.getStatus() != null && a.getStatus() == 1) {
                    idToArticle.put(a.getId(), a);
                }
            }
        }

        // 按收藏顺序组织结果
        java.util.List<Article> ordered = new java.util.ArrayList<>();
        for (UserCollect uc : collectResult.getRecords()) {
            Article a = idToArticle.get(uc.getArticleId());
            if (a != null) ordered.add(a);
        }

        return PageResult.of(ordered, collectResult.getTotal(), collectResult.getCurrent(), collectResult.getSize());
    }
    
    @Override
    public boolean toggleTopArticle(String articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            return false;
        }
        
        article.setIsTop(article.getIsTop() == 1 ? 0 : 1);
        article.updateTime();
        return articleMapper.updateById(article) > 0;
    }
} 
 
 
 