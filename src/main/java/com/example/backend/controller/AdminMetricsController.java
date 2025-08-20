package com.example.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.common.Result;
import com.example.backend.entity.UserAccount;
import com.example.backend.entity.UserLog;
import com.example.backend.service.UserLogService;
import com.example.backend.mapper.UserAccountMapper;
import com.example.backend.util.RateLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Api(tags = "管理员-指标概览")
@RestController
@RequestMapping("/admin/metrics")
@CrossOrigin
public class AdminMetricsController {

    @Autowired
    private UserLogService userLogService;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @ApiOperation("今日访问情况（去除管理员）")
    @GetMapping("/overview")
    @RateLimit(capacity = 60, ratePerSecond = 10.0, key = "#{ip}:/admin/metrics/overview")
    public Result<Map<String, Object>> overview() {
        try {
            long startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long now = System.currentTimeMillis();

            // 查询今日所有日志
            java.util.List<UserLog> logs = userLogService.list(new QueryWrapper<UserLog>()
                    .ge("create_time", startOfDay)
                    .le("create_time", now)
                    .orderByDesc("create_time"));

            // 过滤管理员
            Set<String> adminIds = new HashSet<>();
            if (logs != null) {
                Set<String> uidSet = new HashSet<>();
                for (UserLog l : logs) { if (l.getUserId() != null) uidSet.add(l.getUserId()); }
                if (!uidSet.isEmpty()) {
                    for (String uid : uidSet) {
                        UserAccount ua = userAccountMapper.selectById(uid);
                        if (ua != null && ua.getUserType() != null && ua.getUserType() == 1) {
                            adminIds.add(uid);
                        }
                    }
                }
            }

            // 今日访问用户数（非管理员的唯一用户ID）
            Set<String> uniqUsers = new HashSet<>();
            // 今日访问IP数（所有日志的唯一IP）
            Set<String> uniqIps = new HashSet<>();

            if (logs != null) {
                for (UserLog l : logs) {
                    if (l.getUserId() != null && !adminIds.contains(l.getUserId())) uniqUsers.add(l.getUserId());
                    if (l.getIpAddress() != null && !l.getIpAddress().isEmpty()) uniqIps.add(l.getIpAddress());
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("todayVisitUserCount", uniqUsers.size());
            data.put("todayVisitIpCount", uniqIps.size());
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("近N天访问趋势（非管理员，按天去重用户数）")
    @GetMapping("/visits-trend")
    @RateLimit(capacity = 60, ratePerSecond = 10.0, key = "#{ip}:/admin/metrics/visits-trend")
    public Result<Map<String, Object>> visitsTrend(@ApiParam("天数，默认7") @RequestParam(defaultValue = "7") int days) {
        try {
            if (days <= 0) days = 7;
            ZoneId zone = ZoneId.systemDefault();
            LocalDate today = LocalDate.now();
            long startTs = today.minusDays(days - 1L).atStartOfDay(zone).toInstant().toEpochMilli();
            long endTs = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1;

            List<UserLog> logs = userLogService.list(new QueryWrapper<UserLog>()
                    .ge("create_time", startTs)
                    .le("create_time", endTs));

            // 收集管理员ID
            Set<String> adminIds = new HashSet<>();
            if (logs != null) {
                Set<String> uidSet = new HashSet<>();
                for (UserLog l : logs) { if (l.getUserId() != null) uidSet.add(l.getUserId()); }
                if (!uidSet.isEmpty()) {
                    for (String uid : uidSet) {
                        UserAccount ua = userAccountMapper.selectById(uid);
                        if (ua != null && ua.getUserType() != null && ua.getUserType() == 1) adminIds.add(uid);
                    }
                }
            }

            Map<String, Set<String>> dayToUsers = new LinkedHashMap<>();
            for (int i = days - 1; i >= 0; i--) {
                LocalDate d = today.minusDays(i);
                dayToUsers.put(d.toString(), new HashSet<>());
            }

            if (logs != null) {
                for (UserLog l : logs) {
                    if (l.getUserId() == null || adminIds.contains(l.getUserId())) continue;
                    LocalDate d = Instant.ofEpochMilli(l.getCreateTime()).atZone(zone).toLocalDate();
                    String key = d.toString();
                    Set<String> s = dayToUsers.get(key);
                    if (s != null) s.add(l.getUserId());
                }
            }

            List<String> categories = new ArrayList<>(dayToUsers.keySet());
            List<Integer> series = new ArrayList<>();
            for (String k : categories) series.add(dayToUsers.get(k).size());

            Map<String, Object> data = new HashMap<>();
            data.put("categories", categories);
            data.put("series", series);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("最新非管理员用户日志")
    @GetMapping("/latest-logs")
    @RateLimit(capacity = 60, ratePerSecond = 10.0, key = "#{ip}:/admin/metrics/latest-logs")
    public Result<List<UserLog>> latestLogs(@ApiParam("条数，默认10") @RequestParam(defaultValue = "10") int limit) {
        try {
            if (limit <= 0) limit = 10;
            List<UserLog> list = userLogService.list(new QueryWrapper<UserLog>()
                    .orderByDesc("create_time")
                    .last("limit " + limit * 3)); // 多取点再过滤管理员

            if (list == null || list.isEmpty()) return Result.success(Collections.emptyList());

            List<UserLog> result = new ArrayList<>();
            for (UserLog l : list) {
                if (l.getUserId() == null) { result.add(l); }
                else {
                    UserAccount ua = userAccountMapper.selectById(l.getUserId());
                    if (ua == null || ua.getUserType() == null || ua.getUserType() != 1) result.add(l);
                }
                if (result.size() >= limit) break;
            }
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}


