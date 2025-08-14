package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.PageResult;
import com.example.backend.entity.UserLog;

/**
 * 用户操作日志服务接口
 */
public interface UserLogService extends IService<UserLog> {
    
    /**
     * 记录用户操作日志
     */
    void recordLog(String userId, String operation, String details, String ip);
    
    /**
     * 分页查询用户操作日志
     */
    PageResult<UserLog> pageUserLogs(Integer pageNum, Integer pageSize, String userId, String operation);
}
 
 
 