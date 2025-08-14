package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 测试控制器 - 用于调试Redis连接
 */
@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    /**
     * 测试Redis连接
     */
    @GetMapping("/redis")
    public String testRedis() {
        try {
            // 测试写入
            redisTemplate.opsForValue().set("test_key", "test_value", 60, TimeUnit.SECONDS);
            
            // 测试读取
            String value = redisTemplate.opsForValue().get("test_key");
            
            return "Redis连接正常! 写入: test_value, 读取: " + value;
        } catch (Exception e) {
            return "Redis连接失败: " + e.getMessage();
        }
    }
    
    /**
     * 查看验证码
     */
    @GetMapping("/verification-code")
    public String getVerificationCode(@RequestParam String email) {
        try {
            String key = "verification_code:" + email;
            String code = redisTemplate.opsForValue().get(key);
            
            if (code != null) {
                return "邮箱 " + email + " 的验证码是: " + code;
            } else {
                return "未找到邮箱 " + email + " 的验证码";
            }
        } catch (Exception e) {
            return "查询失败: " + e.getMessage();
        }
    }
    
    /**
     * 手动设置验证码 - 用于测试
     */
    @GetMapping("/set-code")
    public String setVerificationCode(@RequestParam String email, @RequestParam String code) {
        try {
            String key = "verification_code:" + email;
            redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
            
            return "已为邮箱 " + email + " 设置验证码: " + code;
        } catch (Exception e) {
            return "设置失败: " + e.getMessage();
        }
    }
    
    /**
     * 查看Redis中的所有key
     */
    @GetMapping("/redis-keys")
    public String getRedisKeys() {
        try {
            java.util.Set<String> keys = redisTemplate.keys("*");
            return "Redis中的所有key: " + keys;
        } catch (Exception e) {
            return "查询Redis keys失败: " + e.getMessage();
        }
    }
}
 