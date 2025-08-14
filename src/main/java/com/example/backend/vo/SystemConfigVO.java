package com.example.backend.vo;

import com.example.backend.entity.SystemConfig;

/**
 * 系统配置响应对象：按需暴露字段
 */
public class SystemConfigVO extends SystemConfig {
    public SystemConfigVO() {}
    public SystemConfigVO(String key, String value) {
        this.setConfigKey(key);
        this.setConfigValue(value);
    }
}


