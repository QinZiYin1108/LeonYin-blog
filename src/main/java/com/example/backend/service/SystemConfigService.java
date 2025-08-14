package com.example.backend.service;

import com.example.backend.entity.SystemConfig;

import java.util.List;

/**
 * 系统配置服务接口
 */
public interface SystemConfigService {

    /** 是否自动分配默认头像 */
    boolean isAutoAssignAvatar();

    /** 获取默认头像图片ID */
    String getDefaultAvatarImageId();

    /** 列出所有配置 */
    List<SystemConfig> listAll();

    /** 按 key 获取配置 */
    SystemConfig getByKey(String key);

    /** 新增或更新配置 */
    boolean upsert(String key, String value);

    /** 按 key 删除配置 */
    boolean deleteByKey(String key);
}


