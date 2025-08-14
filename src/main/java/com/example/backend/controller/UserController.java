package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.UserProfileRequest;
import com.example.backend.vo.UserAccountVO;
import com.example.backend.dto.ChangePasswordRequest;
import com.example.backend.vo.UserProfileVO;
import com.example.backend.vo.ArticleVO;
import com.example.backend.common.PageResult;
import com.example.backend.service.FileUploadService;
import com.example.backend.service.ArticleService;
import com.example.backend.service.ArticleHeatService;
import com.example.backend.service.UserAccountService;
import com.example.backend.service.UserProfileService;
import com.example.backend.util.JwtUtil;
import com.example.backend.util.RateLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户个人账号管理控制器
 */
@Api(tags = "用户个人账号管理")
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    
    @Autowired
    private UserAccountService userAccountService;
    
    @Autowired
    private UserProfileService userProfileService;
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleHeatService articleHeatService;
    
    /**
     * 获取当前用户信息
     */
    @ApiOperation(value = "获取当前用户信息")
    @GetMapping("/profile")
    @RateLimit(capacity = 80, ratePerSecond = 15.0, key = "#{ip}:/user/profile:get")
    public Result<Object> getUserProfile(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            String userId = jwtUtil.getUserIdFromToken(token);
            Object full = userProfileService.getFullUserInfo(userId);
            if (full == null) {
                return Result.error("用户不存在");
            }
            // 适配为 VO（保持原结构 account/profile）
            java.util.Map<?,?> m = (java.util.Map<?,?>) full;
            java.util.Map<?,?> acc = (java.util.Map<?,?>) m.get("account");
            java.util.Map<?,?> prof = (java.util.Map<?,?>) m.get("profile");
            java.util.Map<String,Object> vo = new java.util.HashMap<>();
            if (acc != null) {
                UserAccountVO a = new UserAccountVO();
                a.setId((String) acc.get("id"));
                a.setEmail((String) acc.get("email"));
                a.setUserType((Integer) acc.get("userType"));
                a.setStatus((Integer) acc.get("status"));
                a.setRegisterType((Integer) acc.get("registerType"));
                a.setEmailVerified((Integer) acc.get("emailVerified"));
                a.setLastLoginTime((Long) acc.get("lastLoginTime"));
                a.setRole((String) acc.get("role"));
                vo.put("account", a);
            } else {
                vo.put("account", null);
            }
            if (prof != null) {
                UserProfileVO p = new UserProfileVO();
                p.setNickname((String) prof.get("nickname"));
                p.setBio((String) prof.get("bio"));
                p.setPhone((String) prof.get("phone"));
                p.setAvatarUrl((String) prof.get("avatarUrl"));
                vo.put("profile", p);
            } else {
                vo.put("profile", null);
            }
            return Result.success(vo);
        } catch (Exception e) {
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新用户个人信息
     */
    @ApiOperation(value = "更新用户个人信息")
    @PutMapping("/profile")
    @RateLimit(capacity = 30, ratePerSecond = 3.0, key = "#{ip}:/user/profile:update")
    public Result<Void> updateUserProfile(@ApiParam("用户信息更新请求") @Validated @RequestBody UserProfileRequest request,
                                          HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            String userId = jwtUtil.getUserIdFromToken(token);
            boolean success = userProfileService.updateUserProfile(userId, request);
            
            if (success) {
                // 记录操作日志
                userAccountService.recordUserLog(userId, "更新个人信息", "用户更新了个人信息", getClientIpAddress(httpRequest));
                return Result.success();
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新用户信息失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "更新头像ID")
    @PutMapping("/avatar")
    @RateLimit(capacity = 30, ratePerSecond = 3.0, key = "#{ip}:/user/avatar")
    public Result<Void> updateAvatar(@ApiParam("头像图片ID") @RequestParam String imageId,
                                     HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            String userId = jwtUtil.getUserIdFromToken(token);
            boolean success = userProfileService.updateAvatar(userId, imageId);
            if (success) {
                userAccountService.recordUserLog(userId, "更新头像", "用户更新了头像", getClientIpAddress(httpRequest));
                return Result.success();
            }
            return Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新头像失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "修改密码")
    @PutMapping("/password")
    @RateLimit(capacity = 20, ratePerSecond = 1.0, key = "#{ip}:/user/password")
    public Result<Void> changePassword(@ApiParam("修改密码请求") @Validated @RequestBody ChangePasswordRequest request,
                                       HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            String userId = jwtUtil.getUserIdFromToken(token);
            boolean success = userAccountService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
            if (success) {
                userAccountService.recordUserLog(userId, "修改密码", "用户修改了登录密码", getClientIpAddress(httpRequest));
                return Result.success();
            }
            return Result.error("修改失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 注销账号
     */
    @ApiOperation(value = "注销账号")
    @DeleteMapping("/account")
    @RateLimit(capacity = 10, ratePerSecond = 1.0, key = "#{ip}:/user/account:delete")
    public Result<Void> deleteAccount(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            String userId = jwtUtil.getUserIdFromToken(token);
            boolean success = userProfileService.deleteAccount(userId);
            
            if (success) {
                // 记录操作日志
                userAccountService.recordUserLog(userId, "注销账号", "用户主动注销了账号", getClientIpAddress(request));
                return Result.success();
            } else {
                return Result.error("注销失败");
            }
        } catch (Exception e) {
            return Result.error("注销账号失败：" + e.getMessage());
        }
    }

    /**
     * 我的收藏（分页）
     */
    @ApiOperation(value = "我的收藏分页")
    @GetMapping("/collections/page")
    @RateLimit(capacity = 60, ratePerSecond = 10.0, key = "#{ip}:/user/collections/page")
    public Result<PageResult<ArticleVO>> pageMyCollections(@ApiParam("页码") @RequestParam(defaultValue = "1") Integer current,
                                                           @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
                                                           HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
            String userId = jwtUtil.getUserIdFromToken(token);
            PageResult<com.example.backend.entity.Article> page = articleService.getUserCollectedArticles(userId, current, size);
            java.util.List<ArticleVO> vos = new java.util.ArrayList<>();
            if (page.getRecords() != null) {
                for (com.example.backend.entity.Article a : page.getRecords()) {
                    ArticleVO vo = toVO(a);
                    try {
                        com.example.backend.entity.ArticleHeat heat = articleHeatService.getRealtimeHeat(a.getId());
                        if (heat != null) {
                            vo.setViewCount(heat.getViewCount()==null?0:heat.getViewCount().intValue());
                            vo.setLikeCount(heat.getLikeCount()==null?0:heat.getLikeCount().intValue());
                            vo.setCollectCount(heat.getCollectCount()==null?0:heat.getCollectCount().intValue());
                            vo.setHotScore(heat.getHotScore());
                        }
                    } catch (Exception ignore) {}
                    vos.add(vo);
                }
            }
            return Result.success(new PageResult<>(vos, page.getTotal(), page.getCurrent(), page.getSize()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private ArticleVO toVO(com.example.backend.entity.Article a) {
        ArticleVO vo = new ArticleVO();
        vo.setId(a.getId());
        vo.setTitle(a.getTitle());
        vo.setSummary(a.getSummary());
        vo.setContent(a.getContent());
        vo.setCoverImageId(a.getCoverImageId());
        vo.setAuthorId(a.getAuthorId());
        vo.setCategoryId(a.getCategoryId());
        vo.setTags(a.getTags());
        vo.setViewCount(a.getViewCount());
        vo.setLikeCount(a.getLikeCount());
        vo.setCollectCount(a.getCollectCount());
        vo.setHotScore(a.getHotScore());
        vo.setStatus(a.getStatus());
        vo.setIsTop(a.getIsTop());
        vo.setPublishTime(a.getPublishTime());
        return vo;
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
 
 
 