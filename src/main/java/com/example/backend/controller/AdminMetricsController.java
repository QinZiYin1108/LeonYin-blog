package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.UserLog;
import com.example.backend.service.UserLogService;
import com.example.backend.util.RateLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(tags = "管理员-数据统计")
@RestController
@RequestMapping("/admin/metrics")
@CrossOrigin
public class AdminMetricsController {

    @Autowired
    private UserLogService userLogService;

    @ApiOperation("获取概览数据")
    @GetMapping("/overview")
    @RateLimit(capacity = 60, ratePerSecond = 10.0, key = "#{ip}:/admin/metrics/overview")
    public Result<Map<String, Long>> getOverview() {
        try {
            Map<String, Long> data = userLogService.getTodayVisitCounts();
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("获取访问趋势")
    @GetMapping("/visits-trend")
    @RateLimit(capacity = 60, ratePerSecond = 10.0, key = "#{ip}:/admin/metrics/visits-trend")
    public Result<Map<String, Object>> getVisitsTrend(
            @ApiParam("天数") @RequestParam(defaultValue = "7") Integer days
    ) {
        try {
            Map<String, Object> data = userLogService.getVisitsTrend(days);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("获取最新用户日志")
    @GetMapping("/latest-logs")
    @RateLimit(capacity = 60, ratePerSecond = 10.0, key = "#{ip}:/admin/metrics/latest-logs")
    public Result<List<UserLog>> getLatestLogs(
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit
    ) {
        try {
            List<UserLog> logs = userLogService.getLatestUserLogs(limit);
            return Result.success(logs);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}