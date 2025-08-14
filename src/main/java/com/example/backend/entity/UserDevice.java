package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.util.IdGenerator;

/**
 * 用户设备记录实体类
 */
@TableName("user_device")
public class UserDevice extends BaseEntity {
    
    private String userId;
    private String deviceId;
    private String deviceInfo;
    private String ipAddress;
    private String location;
    private Long lastLoginTime;
    private Integer status; // 0-禁用，1-正常
    
    public UserDevice() {
        super();
        this.setId(IdGenerator.generateId("UD"));
    }
    
    public UserDevice(String userId, String deviceId, String deviceInfo, String ipAddress) {
        this();
        this.userId = userId;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
        this.lastLoginTime = System.currentTimeMillis();
        this.status = 1;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getDeviceInfo() {
        return deviceInfo;
    }
    
    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Long getLastLoginTime() {
        return lastLoginTime;
    }
    
    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
} 
 
 
 