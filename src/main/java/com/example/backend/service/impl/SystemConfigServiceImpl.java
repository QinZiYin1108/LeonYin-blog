package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.entity.SystemConfig;
import com.example.backend.mapper.SystemConfigMapper;
import com.example.backend.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Override
    public boolean isAutoAssignAvatar() {
        SystemConfig cfg = systemConfigMapper.selectOne(new QueryWrapper<SystemConfig>().eq("config_key", "auto_assign_avatar"));
        return cfg != null && "1".equals(cfg.getConfigValue());
    }

    @Override
    public String getDefaultAvatarImageId() {
        SystemConfig cfg = systemConfigMapper.selectOne(new QueryWrapper<SystemConfig>().eq("config_key", "default_avatar_image_id"));
        return cfg == null ? null : cfg.getConfigValue();
    }

    @Override
    public List<SystemConfig> listAll() {
        return systemConfigMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public SystemConfig getByKey(String key) {
        return systemConfigMapper.selectOne(new QueryWrapper<SystemConfig>().eq("config_key", key));
    }

    @Override
    public boolean upsert(String key, String value) {
        SystemConfig exist = getByKey(key);
        if (exist == null) {
            SystemConfig cfg = new SystemConfig();
            cfg.setConfigKey(key);
            cfg.setConfigValue(value);
            return systemConfigMapper.insert(cfg) > 0;
        } else {
            exist.setConfigValue(value);
            exist.updateTime();
            return systemConfigMapper.updateById(exist) > 0;
        }
    }

    @Override
    public boolean deleteByKey(String key) {
        SystemConfig exist = getByKey(key);
        if (exist == null) {
            return false;
        }
        return systemConfigMapper.deleteById(exist.getId()) > 0;
    }
}

