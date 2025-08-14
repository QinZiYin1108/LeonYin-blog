package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.dto.UserProfileRequest;
import com.example.backend.entity.UserAccount;
import com.example.backend.entity.UserProfile;
import com.example.backend.mapper.UserAccountMapper;
import com.example.backend.mapper.UserProfileMapper;
import com.example.backend.service.ImageStorageService;
import com.example.backend.service.UserProfileService;
import com.example.backend.service.SystemConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息服务实现类
 */
@Service
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {
    
    private static final Logger log = LoggerFactory.getLogger(UserProfileServiceImpl.class);
    
    @Autowired
    private UserProfileMapper userProfileMapper;
    
    @Autowired
    private UserAccountMapper userAccountMapper;
    
    @Autowired
    private ImageStorageService imageStorageService;
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public UserProfile createProfile(String userAccountId, String nickname) {
        return createProfileWithAvatar(userAccountId, nickname, null);
    }
    
    @Override
    public UserProfile createProfileWithAvatar(String userAccountId, String nickname, String avatarImageId) {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(userAccountId);
        userProfile.setNickname(nickname);
        userProfile.setAvatarImageId(avatarImageId);
        
        userProfileMapper.insert(userProfile);
        return userProfile;
    }
    
    @Override
    public UserProfile getByUserAccountId(String userAccountId) {
        QueryWrapper<UserProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userAccountId);
        return userProfileMapper.selectOne(queryWrapper);
    }
    
    @Override
    public boolean updateUserProfile(String userAccountId, UserProfileRequest request) {
        UserProfile userProfile = getByUserAccountId(userAccountId);
        if (userProfile == null) {
            return false;
        }
        
        // 更新允许修改的字段
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            userProfile.setNickname(request.getNickname());
        }
        if (request.getBio() != null) {
            userProfile.setBio(request.getBio());
        }
        if (request.getPhone() != null) {
            userProfile.setPhone(request.getPhone());
        }
        
        userProfile.updateTime();
        return userProfileMapper.updateById(userProfile) > 0;
    }
    
    
    @Override
    public boolean updateAvatar(String userAccountId, String avatarImageId) {
        UserProfile userProfile = getByUserAccountId(userAccountId);
        if (userProfile == null) {
            return false;
        }
        String oldAvatar = userProfile.getAvatarImageId();

        // 设置新头像
        userProfile.setAvatarImageId(avatarImageId);
        userProfile.updateTime();
        
        boolean updated = userProfileMapper.updateById(userProfile) > 0;
        if (updated) {
            // 删除旧头像（如果不是系统默认头像）
            try {
                if (oldAvatar != null && !oldAvatar.trim().isEmpty() && !isSystemDefaultAvatar(oldAvatar)) {
                    imageStorageService.deleteImageById(oldAvatar);
                }
            } catch (Exception ignore) {}
        }
        return updated;
    }

    /**
     * 判断是否为系统默认头像
     * 这里先简单按空/特定标识判断，后续可接入SystemConfigService默认头像ID进行校验
     */
    private boolean isSystemDefaultAvatar(String imageId) {
        try {
            com.example.backend.entity.SystemConfig cfg = systemConfigService.getByKey("default_avatar_image_id");
            String defId = cfg == null ? null : cfg.getConfigValue();
            return defId != null && defId.equals(imageId);
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean deleteAccount(String userAccountId) {
        // 删除用户信息（账号表会通过级联删除）
        QueryWrapper<UserProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userAccountId);
        
        UserProfile userProfile = userProfileMapper.selectOne(queryWrapper);
        if (userProfile != null) {
            // 如果有头像，减少引用计数
            if (userProfile.getAvatarImageId() != null) {
                // TODO: 减少头像的引用计数
            }
            
            return userProfileMapper.deleteById(userProfile.getId()) > 0;
        }
        
        return false;
    }
    
    @Override
    public Object getFullUserInfo(String userAccountId) {
        try {
            // 获取账号信息
            UserAccount userAccount = userAccountMapper.selectById(userAccountId);
            if (userAccount == null) {
                return null;
            }
            
            // 获取用户信息
            UserProfile userProfile = getByUserAccountId(userAccountId);
            
            // 组合完整信息
            Map<String, Object> fullInfo = new HashMap<>();
            
            // 账号信息（隐藏密码）
            Map<String, Object> accountInfo = new HashMap<>();
            accountInfo.put("id", userAccount.getId());
            accountInfo.put("email", userAccount.getEmail());
            accountInfo.put("userType", userAccount.getUserType());
            accountInfo.put("status", userAccount.getStatus());
            accountInfo.put("registerType", userAccount.getRegisterType());
            accountInfo.put("emailVerified", userAccount.getEmailVerified());
            accountInfo.put("lastLoginTime", userAccount.getLastLoginTime());
            accountInfo.put("role", userAccount.getRole());
            
            fullInfo.put("account", accountInfo);
            
            // 用户信息
            if (userProfile != null) {
                Map<String, Object> profileInfo = new HashMap<>();
                profileInfo.put("nickname", userProfile.getNickname());
                profileInfo.put("bio", userProfile.getBio());
                profileInfo.put("phone", userProfile.getPhone());
                
                // 获取头像URL
                if (userProfile.getAvatarImageId() != null) {
                    String avatarUrl = imageStorageService.getImageUrl(userProfile.getAvatarImageId(), "original");
                    profileInfo.put("avatarUrl", avatarUrl);
                }
                
                fullInfo.put("profile", profileInfo);
            } else {
                fullInfo.put("profile", null);
            }
            
            return fullInfo;
            
        } catch (Exception e) {
            log.error("获取完整用户信息失败", e);
            return null;
        }
    }

    @Override
    public java.util.List<UserProfile> listAllProfiles() {
        return userProfileMapper.selectList(new QueryWrapper<>());
    }
}



