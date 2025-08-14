package com.example.backend.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * ID生成器工具类
 * 简化版雪花算法
 */
@Component
public class IdGenerator {
    
    private static final AtomicLong sequence = new AtomicLong(0);
    private static final long startTime = 1640995200000L; // 2022-01-01 00:00:00
    
    /**
     * 生成带前缀的ID
     */
    public static String generateId(String prefix) {
        long timestamp = System.currentTimeMillis() - startTime;
        long seq = sequence.incrementAndGet() % 1000;
        return prefix + timestamp + String.format("%03d", seq);
    }
    
    /**
     * 生成用户ID
     */
    public static String generateUserId() {
        return generateId("U");
    }
    
    /**
     * 生成文章ID
     */
    public static String generateArticleId() {
        return generateId("A");
    }
    
    /**
     * 生成分类ID
     */
    public static String generateCategoryId() {
        return generateId("C");
    }
    
    /**
     * 生成评论ID
     */
    public static String generateCommentId() {
        return generateId("CM");
    }
    
    /**
     * 生成点赞ID
     */
    public static String generateLikeId() {
        return generateId("L");
    }
    
    /**
     * 生成收藏ID
     */
    public static String generateCollectId() {
        return generateId("CO");
    }
    
    /**
     * 生成日志ID
     */
    public static String generateLogId() {
        return generateId("LOG");
    }
    
    /**
     * 生成配置ID
     */
    public static String generateConfigId() {
        return generateId("SC");
    }
    
    /**
     * 生成验证码ID
     */
    public static String generateVerificationId() {
        return generateId("EV");
    }
} 
 