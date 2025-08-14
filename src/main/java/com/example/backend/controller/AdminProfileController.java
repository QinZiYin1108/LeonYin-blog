package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.UserProfileRequest;
import com.example.backend.entity.UserProfile;
import com.example.backend.service.UserProfileService;
import com.example.backend.service.ImageStorageService;
import com.example.backend.service.SystemConfigService;
import com.example.backend.service.UserAccountService;
import com.example.backend.service.EmailService;
import com.example.backend.util.RateLimit;
import com.example.backend.vo.UserProfileVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "管理员-用户资料管理")
@RestController
@RequestMapping("/admin/profile")
@CrossOrigin
public class AdminProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private EmailService emailService;

    @ApiOperation("管理员更新用户资料")
    @PutMapping("/{userId}")
    @RateLimit(capacity = 30, ratePerSecond = 3.0, key = "#{ip}:/admin/profile/update")
    public Result<Void> updateProfile(@ApiParam("用户ID") @PathVariable String userId,
                                      @ApiParam("资料更新") @Validated @RequestBody UserProfileRequest request) {
        try {
            boolean success = userProfileService.updateUserProfile(userId, request);
            return success ? Result.success() : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("管理员查看所有用户资料")
    @GetMapping
    @RateLimit(capacity = 60, ratePerSecond = 10.0, key = "#{ip}:/admin/profile/list")
    public Result<List<UserProfileVO>> listAll() {
        try {
            List<UserProfile> list = userProfileService.listAllProfiles();
            List<UserProfileVO> vos = new ArrayList<>();
            for (UserProfile up : list) {
                UserProfileVO vo = new UserProfileVO();
                vo.setId(up.getId());
                vo.setUserId(up.getUserId());
                vo.setNickname(up.getNickname());
                vo.setAvatarImageId(up.getAvatarImageId());
                vo.setBio(up.getBio());
                vo.setPhone(up.getPhone());
                vo.setCreateTime(up.getCreateTime());
                vo.setUpdateTime(up.getUpdateTime());
                if (up.getAvatarImageId() != null) {
                    String url = imageStorageService.getImageUrl(up.getAvatarImageId(), null);
                    vo.setAvatarUrl(url);
                } else {
                    // 回退到系统默认头像
                    com.example.backend.entity.SystemConfig cfg = systemConfigService.getByKey("default_avatar_image_id");
                    if (cfg != null && cfg.getConfigValue() != null && !cfg.getConfigValue().trim().isEmpty()) {
                        String url = imageStorageService.getImageUrl(cfg.getConfigValue(), null);
                        vo.setAvatarUrl(url);
                    }
                }
                vos.add(vo);
            }
            return Result.success(vos);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("重置用户头像为系统默认头像")
    @PutMapping("/{userId}/reset-avatar")
    @RateLimit(capacity = 20, ratePerSecond = 2.0, key = "#{ip}:/admin/profile/reset-avatar")
    public Result<Void> resetAvatar(@ApiParam("用户ID") @PathVariable String userId) {
        try {
            com.example.backend.entity.SystemConfig cfg = systemConfigService.getByKey("default_avatar_image_id");
            String defaultAvatarId = cfg == null ? null : cfg.getConfigValue();
            boolean ok = userProfileService.updateAvatar(userId, defaultAvatarId);
            if (ok) {
                try {
                    com.example.backend.entity.UserAccount target = userAccountService.getById(userId);
                    if (target != null) {
                        emailService.sendSimpleMail(target.getEmail(), "头像重置通知", "您的头像已被管理员重置为系统默认头像。");
                    }
                } catch (Exception ignore) {}
                return Result.success();
            }
            return Result.error("操作失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

