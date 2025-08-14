package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.entity.ArticleHeat;

public interface ArticleHeatService extends IService<ArticleHeat> {

    void incrView(String articleId, long delta);

    void incrLike(String articleId, long delta);

    void incrCollect(String articleId, long delta);

    /** 从Redis聚合并刷新数据库热度表（可被定时任务调用） */
    void flushToDatabase();

    /**
     * 获取文章的实时热度：把数据库持久化值和Redis增量合并得到快照
     */
    ArticleHeat getRealtimeHeat(String articleId);
}



