package com.example.backend.util;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    // 桶容量
    long capacity() default 50;
    // 每秒补充速率
    double ratePerSecond() default 5.0;
    // 每次请求消耗的令牌数
    long requestedTokens() default 1;
    // 桶键的维度：默认按IP+方法
    String key() default "#{ip}:#{method}:#{userId}";
}


