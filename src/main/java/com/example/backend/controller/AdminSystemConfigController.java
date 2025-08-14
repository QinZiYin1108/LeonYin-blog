package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.vo.SystemConfigVO;
import com.example.backend.entity.SystemConfig;
import com.example.backend.mapper.SystemConfigMapper;
import com.example.backend.service.SystemConfigService;
import com.example.backend.util.RateLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员-系统配置")
@RestController
@RequestMapping("/admin/config")
@CrossOrigin
public class AdminSystemConfigController {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Autowired
    private SystemConfigService systemConfigService;

    @ApiOperation("设置是否自动分配默认头像")
    @PostMapping("/auto-avatar")
    @RateLimit(capacity = 20, ratePerSecond = 2.0, key = "#{ip}:/admin/config/auto-avatar")
    public Result<Void> setAutoAvatar(@ApiParam("0或1") @RequestParam String value) {
        try {
            boolean ok = systemConfigService.upsert("auto_assign_avatar", value);
            return ok ? Result.success() : Result.error("保存失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("设置默认头像图片ID")
    @PostMapping("/default-avatar")
    @RateLimit(capacity = 20, ratePerSecond = 2.0, key = "#{ip}:/admin/config/default-avatar")
    public Result<Void> setDefaultAvatar(@ApiParam("图片ID") @RequestParam String imageId) {
        try {
            boolean ok = systemConfigService.upsert("default_avatar_image_id", imageId);
            return ok ? Result.success() : Result.error("保存失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("设置默认分类图标图片ID")
    @PostMapping("/default-category-icon")
    @RateLimit(capacity = 20, ratePerSecond = 2.0, key = "#{ip}:/admin/config/default-category-icon")
    public Result<Void> setDefaultCategoryIcon(@ApiParam("图片ID") @RequestParam String imageId) {
        try {
            boolean ok = systemConfigService.upsert("default_category_icon_image_id", imageId);
            return ok ? Result.success() : Result.error("保存失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("获取默认分类图标图片ID")
    @GetMapping("/default-category-icon")
    @RateLimit(capacity = 60, ratePerSecond = 10.0, key = "#{ip}:/admin/config/default-category-icon:get")
    public Result<String> getDefaultCategoryIcon() {
        try {
            com.example.backend.entity.SystemConfig cfg = systemConfigService.getByKey("default_category_icon_image_id");
            return cfg == null ? Result.success("") : Result.success(cfg.getConfigValue());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("列出全部配置")
    @GetMapping
    @RateLimit(capacity = 80, ratePerSecond = 15.0, key = "#{ip}:/admin/config/list")
    public Result<java.util.List<SystemConfigVO>> listAll() {
        try {
            java.util.List<SystemConfig> list = systemConfigService.listAll();
            java.util.List<SystemConfigVO> vos = new java.util.ArrayList<>();
            for (SystemConfig c : list) {
                vos.add(new SystemConfigVO(c.getConfigKey(), c.getConfigValue()));
            }
            return Result.success(vos);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("按key查询配置")
    @GetMapping("/{key}")
    @RateLimit(capacity = 80, ratePerSecond = 15.0, key = "#{ip}:/admin/config/get")
    public Result<SystemConfigVO> getByKey(@PathVariable String key) {
        try {
            SystemConfig cfg = systemConfigService.getByKey(key);
            return cfg == null ? Result.error("不存在") : Result.success(new SystemConfigVO(cfg.getConfigKey(), cfg.getConfigValue()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("删除配置")
    @DeleteMapping("/{key}")
    @RateLimit(capacity = 20, ratePerSecond = 2.0, key = "#{ip}:/admin/config/delete")
    public Result<Void> delete(@PathVariable String key) {
        try {
            boolean ok = systemConfigService.deleteByKey(key);
            return ok ? Result.success() : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

