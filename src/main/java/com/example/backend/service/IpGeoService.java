package com.example.backend.service;

/**
 * IP 地理位置解析服务
 */
public interface IpGeoService {

    /**
     * 根据 IP 获取归属地描述（如：国家-省-市），失败返回 null
     */
    String resolveLocation(String ipAddress);
}


