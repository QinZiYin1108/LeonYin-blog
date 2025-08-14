package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.util.IdGenerator;

/**
 * 系统配置实体
 */
@TableName("system_config")
public class SystemConfig extends BaseEntity {

    private String configKey;
    private String configValue;

    public SystemConfig() {
        super();
        this.setId(IdGenerator.generateConfigId());
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}





