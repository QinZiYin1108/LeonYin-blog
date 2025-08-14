package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.ArticleHeat;
import com.example.backend.mapper.ArticleHeatMapper;
import com.example.backend.service.ArticleHeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Set;

@Service
public class ArticleHeatServiceImpl extends ServiceImpl<ArticleHeatMapper, ArticleHeat> implements ArticleHeatService {

    private static final String KEY_VIEW = "article:heat:view:";      // +articleId -> long
    private static final String KEY_LIKE = "article:heat:like:";      // +articleId -> long
    private static final String KEY_COLLECT = "article:heat:collect:";// +articleId -> long
    private static final String KEY_SCORE = "article:heat:score:";    // +articleId -> double (缓存计算)

    @Autowired
    private StringRedisTemplate redis;

    @Value("${blog.hot-score.view-weight:1.0}")
    private double viewWeight;
    @Value("${blog.hot-score.like-weight:2.0}")
    private double likeWeight;
    @Value("${blog.hot-score.collect-weight:4.0}")
    private double collectWeight;
    @Value("${blog.hot-score.time-decay-factor:0.1}")
    private double timeDecayFactor;

    @Override
    public void incrView(String articleId, long delta) {
        redis.opsForValue().increment(KEY_VIEW + articleId, delta);
        redis.expire(KEY_VIEW + articleId, Duration.ofDays(2));
    }

    @Override
    public void incrLike(String articleId, long delta) {
        redis.opsForValue().increment(KEY_LIKE + articleId, delta);
        redis.expire(KEY_LIKE + articleId, Duration.ofDays(7));
    }

    @Override
    public void incrCollect(String articleId, long delta) {
        redis.opsForValue().increment(KEY_COLLECT + articleId, delta);
        redis.expire(KEY_COLLECT + articleId, Duration.ofDays(7));
    }

    /**
     * 周期性把Redis中的增量合并到数据库热度表，并计算热度分数
     */
    @Override
    public void flushToDatabase() {
        // 简化：扫描可能存在的key集合（实际生产建议结合文章列表或ZSET管理）
        Set<String> viewKeys = redis.keys(KEY_VIEW + "*");
        if (viewKeys == null) return;
        for (String key : viewKeys) {
            String articleId = key.substring(KEY_VIEW.length());
            long views = parseLong(redis.opsForValue().get(KEY_VIEW + articleId));
            long likes = parseLong(redis.opsForValue().get(KEY_LIKE + articleId));
            long collects = parseLong(redis.opsForValue().get(KEY_COLLECT + articleId));

            ArticleHeat heat = getOne(new QueryWrapper<ArticleHeat>().eq("article_id", articleId));
            if (heat == null) {
                heat = new ArticleHeat();
                heat.setArticleId(articleId);
                // publishTime 需要在文章发布时同步写入
                save(heat);
            }
            long newViews = Math.max(0, (heat.getViewCount() == null ? 0 : heat.getViewCount()) + views);
            long newLikes = Math.max(0, (heat.getLikeCount() == null ? 0 : heat.getLikeCount()) + likes);
            long newCollects = Math.max(0, (heat.getCollectCount() == null ? 0 : heat.getCollectCount()) + collects);
            heat.setViewCount(newViews);
            heat.setLikeCount(newLikes);
            heat.setCollectCount(newCollects);

            double score = newViews * viewWeight + newLikes * likeWeight + newCollects * collectWeight;
            if (heat.getPublishTime() != null) {
                long daysSincePublish = (System.currentTimeMillis() - heat.getPublishTime()) / (1000 * 60 * 60 * 24);
                double timeDecay = Math.exp(-timeDecayFactor * daysSincePublish);
                score *= timeDecay;
            }
            heat.setHotScore(BigDecimal.valueOf(score));
            updateById(heat);

            // 清理已合并的增量（也可改为减去对应增量保留过期策略）
            redis.delete(KEY_VIEW + articleId);
            redis.delete(KEY_LIKE + articleId);
            redis.delete(KEY_COLLECT + articleId);
        }
    }

    @Override
    public ArticleHeat getRealtimeHeat(String articleId) {
        ArticleHeat heat = getOne(new QueryWrapper<ArticleHeat>().eq("article_id", articleId));
        if (heat == null) {
            heat = new ArticleHeat();
            heat.setArticleId(articleId);
        }
        long addViews = parseLong(redis.opsForValue().get(KEY_VIEW + articleId));
        long addLikes = parseLong(redis.opsForValue().get(KEY_LIKE + articleId));
        long addCollects = parseLong(redis.opsForValue().get(KEY_COLLECT + articleId));
        long views = Math.max(0, (heat.getViewCount()==null?0:heat.getViewCount()) + addViews);
        long likes = Math.max(0, (heat.getLikeCount()==null?0:heat.getLikeCount()) + addLikes);
        long collects = Math.max(0, (heat.getCollectCount()==null?0:heat.getCollectCount()) + addCollects);
        heat.setViewCount(views);
        heat.setLikeCount(likes);
        heat.setCollectCount(collects);
        double score = views * viewWeight + likes * likeWeight + collects * collectWeight;
        if (heat.getPublishTime() != null) {
            long daysSincePublish = (System.currentTimeMillis() - heat.getPublishTime()) / (1000 * 60 * 60 * 24);
            double timeDecay = Math.exp(-timeDecayFactor * daysSincePublish);
            score *= timeDecay;
        }
        heat.setHotScore(java.math.BigDecimal.valueOf(score));
        return heat;
    }

    private long parseLong(String v) {
        if (v == null) return 0L;
        try { return Long.parseLong(v); } catch (Exception e) { return 0L; }
    }
}


