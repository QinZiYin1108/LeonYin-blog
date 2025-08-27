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

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    
    @Override
    public Map<String, Long> getTodayVisitCounts() {
        Map<String, Long> result = new HashMap<>();
        
        // 获取今天的开始时间戳（当天0点）
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long todayStart = calendar.getTimeInMillis();
        
        // 查询今日访问日志
        QueryWrapper<UserLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("create_time", todayStart);
        
        // 排除管理员操作
        queryWrapper.isNull("user_id").or().notLike("user_id", "U%");
        
        List<UserLog> todayLogs = list(queryWrapper);
        
        // 统计不同用户数量
        long userCount = todayLogs.stream()
                .filter(log -> log.getUserId() != null)
                .map(UserLog::getUserId)
                .distinct()
                .count();
        
        // 统计不同IP数量
        long ipCount = todayLogs.stream()
                .filter(log -> log.getIpAddress() != null)
                .map(UserLog::getIpAddress)
                .distinct()
                .count();
        
        result.put("todayVisitUserCount", userCount);
        result.put("todayVisitIpCount", ipCount);
        
        return result;
    }
    
    @Override
    public Map<String, Object> getVisitsTrend(int days) {
        Map<String, Object> result = new HashMap<>();
        List<String> categories = new ArrayList<>();
        List<Long> series = new ArrayList<>();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        
        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        // 计算每天的访问量
        for (int i = days - 1; i >= 0; i--) {
            // 设置为当天的开始时间
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            long dayStart = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            long dayEnd = calendar.getTimeInMillis();
            
            // 查询当天的访问日志
            QueryWrapper<UserLog> queryWrapper = new QueryWrapper<>();
            queryWrapper.ge("create_time", dayStart)
                      .lt("create_time", dayEnd);
            
            // 排除管理员操作
            queryWrapper.isNull("user_id").or().notLike("user_id", "U%");
            
            // 统计不同用户数量
            List<UserLog> dayLogs = list(queryWrapper);
            long userCount = dayLogs.stream()
                    .filter(log -> log.getUserId() != null)
                    .map(UserLog::getUserId)
                    .distinct()
                    .count();
            
            // 添加到结果集
            categories.add(dateFormat.format(new Date(dayStart)));
            series.add(userCount);
        }
        
        // 反转列表，使日期按照从早到晚排序
        Collections.reverse(categories);
        Collections.reverse(series);
        
        result.put("categories", categories);
        result.put("series", series);
        
        return result;
    }
    
    @Override
    public List<UserLog> getLatestUserLogs(int limit) {
        QueryWrapper<UserLog> queryWrapper = new QueryWrapper<>();
        
        // 排除管理员操作
        queryWrapper.isNull("user_id").or().notLike("user_id", "U%");
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc("create_time");
        
        // 限制返回数量
        Page<UserLog> page = new Page<>(1, limit);
        IPage<UserLog> logPage = page(page, queryWrapper);
        
        return logPage.getRecords();
    }
}
 
 
 