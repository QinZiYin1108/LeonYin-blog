package com.example.backend.controller;

import com.example.backend.common.PageResult;
import com.example.backend.common.Result;
import com.example.backend.entity.UserLog;
import com.example.backend.service.UserLogService;
import com.example.backend.util.RateLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员-用户操作日志")
@RestController
@RequestMapping("/admin/log")
@CrossOrigin
public class AdminLogController {

    @Autowired
    private UserLogService userLogService;

    @ApiOperation("分页查询用户操作日志")
    @GetMapping("/page")
    @RateLimit(capacity = 80, ratePerSecond = 15.0, key = "#{ip}:/admin/log/page")
    public Result<PageResult<UserLog>> page(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("用户ID(可选)") @RequestParam(required = false) String userId,
            @ApiParam("操作名(可选)") @RequestParam(required = false) String operation
    ) {
        try {
            PageResult<UserLog> pr = userLogService.pageUserLogs(pageNum, pageSize, userId, operation);
            return Result.success(pr);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}






