package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.PageResult;
import com.example.backend.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     */
    User register(String email, String password, String nickname, Integer registerType);
    
    /**
     * 用户登录
     */
    String login(String email, String password);
    
    /**
     * 发送邮箱验证码
     */
    void sendEmailCode(String email, Integer purpose);
    
    /**
     * 验证邮箱验证码
     */
    boolean verifyEmailCode(String email, String code, Integer purpose);
    
    /**
     * 根据邮箱获取用户
     */
    User getUserByEmail(String email);
    
    /**
     * 更新用户信息
     */
    boolean updateUserInfo(String userId, User user);
    
    /**
     * 实名认证
     */
    boolean realNameVerification(String userId, String realName, String idCard);
    
    /**
     * 分页查询用户
     */
    PageResult<User> pageUsers(Integer current, Integer size, String keyword);
    
    /**
     * 禁用/启用用户
     */
    boolean changeUserStatus(String userId, Integer status);
} 