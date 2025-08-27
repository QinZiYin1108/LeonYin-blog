package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.PageResult;
import com.example.backend.entity.UserLog;

import java.util.List;
import java.util.Map;

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
    
    /**
     * 获取今日访问统计数据
     * @return 包含今日访问用户数和IP数的Map
     */
    Map<String, Long> getTodayVisitCounts();
    
    /**
     * 获取指定天数的访问趋势数据
     * @param days 天数
     * @return 访问趋势数据，包含日期分类和访问数据
     */
    Map<String, Object> getVisitsTrend(int days);
    
    /**
     * 获取最新的用户日志
     * @param limit 限制数量
     * @return 最新的用户日志列表
     */
    List<UserLog> getLatestUserLogs(int limit);
}
 
 
 