package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.common.PageResult;
import com.example.backend.entity.UserLog;
import com.example.backend.mapper.UserLogMapper;
import com.example.backend.service.UserLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户操作日志服务实现类
 */
@Service
public class UserLogServiceImpl extends ServiceImpl<UserLogMapper, UserLog> implements UserLogService {
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public void recordLog(String userId, String operation, String details, String ip) {
        UserLog userLog = new UserLog();
        userLog.setUserId(userId);
        userLog.setAction(operation);
        
        // 将详细信息转换为JSON字符串
        if (details != null) {
            try {
                java.util.Map<String, Object> detailsMap = java.util.Collections.singletonMap("description", details);
                userLog.setDetails(objectMapper.writeValueAsString(detailsMap));
            } catch (Exception e) {
                // 如果JSON序列化失败，直接存储字符串
                userLog.setDetails(details);
            }
        }
        
        userLog.setIpAddress(ip);
        userLog.setCreateTime(System.currentTimeMillis());
        userLog.setUpdateTime(System.currentTimeMillis());
        
        save(userLog);
    }
    
    @Override
    public PageResult<UserLog> pageUserLogs(Integer pageNum, Integer pageSize, String userId, String operation) {
        Page<UserLog> page = new Page<>(pageNum, pageSize);
        
        QueryWrapper<UserLog> queryWrapper = new QueryWrapper<>();
        
        // 按用户ID筛选
        if (StringUtils.hasText(userId)) {
            queryWrapper.eq("user_id", userId);
        }
        
        // 按操作类型筛选
        if (StringUtils.hasText(operation)) {
            queryWrapper.like("action", operation);
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc("create_time");
        
        IPage<UserLog> userLogPage = page(page, queryWrapper);
        
        return PageResult.success(userLogPage.getRecords(), userLogPage.getTotal(), pageNum, pageSize);
    }
}
 
 
 