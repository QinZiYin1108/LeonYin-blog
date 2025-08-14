package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.dto.UserProfileRequest;
import com.example.backend.entity.UserProfile;

/**
 * 用户信息服务接口
 */
public interface UserProfileService extends IService<UserProfile> {
    
    /**
     * 创建用户信息（注册时调用）
     */
    UserProfile createProfile(String userAccountId, String nickname);
    
    /**
     * 创建用户信息记录（包含头像设置）
     */
    UserProfile createProfileWithAvatar(String userAccountId, String nickname, String avatarImageId);
    
    /**
     * 根据用户账号ID获取用户信息
     */
    UserProfile getByUserAccountId(String userAccountId);
    
    /**
     * 更新用户个人资料
     */
    boolean updateUserProfile(String userAccountId, UserProfileRequest request);
    
    
    /**
     * 更新用户头像
     */
    boolean updateAvatar(String userAccountId, String avatarImageId);
    
    /**
     * 删除用户账户（删除个人信息）
     */
    boolean deleteAccount(String userAccountId);
    
    /**
     * 获取完整的用户信息（包含账号和资料）
     */
    Object getFullUserInfo(String userAccountId);

    /**
     * 管理员分页或列表（这里简单返回全部用户资料，可扩展成分页）
     */
    java.util.List<UserProfile> listAllProfiles();
}



