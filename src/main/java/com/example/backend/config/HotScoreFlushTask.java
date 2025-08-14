package com.example.backend.config;

import com.example.backend.service.ArticleHeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HotScoreFlushTask {

    @Autowired
    private ArticleHeatService articleHeatService;

    // 每5分钟合并一次增量
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void flush() {
        articleHeatService.flushToDatabase();
    }
}






