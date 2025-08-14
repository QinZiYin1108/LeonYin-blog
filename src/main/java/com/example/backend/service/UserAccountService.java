package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.PageResult;
import com.example.backend.dto.*;
import com.example.backend.vo.LoginResponse;
import com.example.backend.entity.UserAccount;

/**
 * 用户账号服务接口
 */
public interface UserAccountService extends IService<UserAccount> {
    
    /**
     * 用户注册（使用验证码）
     */
    UserAccount registerWithCode(String email, String password, Integer registerType, String verificationCode, String nickname);
    
    /**
     * 用户登录（支持多种登录方式）
     */
    LoginResponse login(LoginRequest request, String clientIp);
    
    /**
     * 根据邮箱获取用户
     */
    UserAccount getUserByEmail(String email);
    
    /**
     * 修改用户状态（管理员功能）
     */
    boolean changeUserStatus(String userId, Integer status);
    
    /**
     * 分页查询用户（管理员功能）
     */
    PageResult<UserAccount> pageUsers(UserPageRequest request);
    
    /**
     * 管理员更新用户信息
     */
    boolean adminUpdateUser(String userId, UserAccount userAccount);
    
    /**
     * 记录用户操作日志
     */
    void recordUserLog(String userId, String operation, String details, String ip);
    
    /**
     * 分页查询用户操作日志（管理员功能）
     */
    PageResult<Object> pageUserLogs(String userId, Integer page, Integer size);
    
    /**
     * 重置用户密码（管理员功能）
     */
    boolean resetPassword(String userId, String newPassword);

    /**
     * 用户自助修改密码（需提供旧密码）
     */
    boolean changePassword(String userId, String oldPassword, String newPassword);
}



