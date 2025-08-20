package com.example.backend.config;

import com.example.backend.service.ArticleHeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HotScoreFlushTask {

    @Autowired
    private ArticleHeatService articleHeatService;

    @Value("${blog.hot-score.flush-interval-ms:5000}")
    private long flushIntervalMs;

    // 使用可配置的固定延迟，默认5秒
    @Scheduled(fixedDelayString = "${blog.hot-score.flush-interval-ms:5000}")
    public void flush() {
        articleHeatService.flushToDatabase();
    }
}






