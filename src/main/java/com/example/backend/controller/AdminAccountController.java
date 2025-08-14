package com.example.backend.controller;

import com.example.backend.common.PageResult;
import com.example.backend.common.Result;
import com.example.backend.dto.UserAccountUpdateRequest;
import com.example.backend.dto.ChangeUserStatusRequest;
import com.example.backend.dto.ResetPasswordRequest;
import com.example.backend.dto.UserPageRequest;
import com.example.backend.entity.UserAccount;
import com.example.backend.vo.UserAccountVO;
import com.example.backend.util.RateLimit;
import com.example.backend.service.EmailService;
import com.example.backend.service.UserAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员-用户账号管理")
@RestController
@RequestMapping("/admin/account")
@CrossOrigin
public class AdminAccountController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private EmailService emailService;

    @ApiOperation("分页查询用户")
    @PostMapping("/page")
    @RateLimit(capacity = 60, ratePerSecond = 10.0, key = "#{ip}:/admin/account/page")
    public Result<PageResult<UserAccountVO>> pageUsers(@ApiParam("分页查询请求") @Validated @RequestBody UserPageRequest request) {
        try {
            PageResult<UserAccount> page = userAccountService.pageUsers(request);
            java.util.List<UserAccountVO> vos = new java.util.ArrayList<>();
            if (page.getRecords() != null) {
                for (UserAccount ua : page.getRecords()) {
                    UserAccountVO vo = new UserAccountVO();
                    vo.setId(ua.getId());
                    vo.setEmail(ua.getEmail());
                    vo.setUserType(ua.getUserType());
                    vo.setStatus(ua.getStatus());
                    vo.setRegisterType(ua.getRegisterType());
                    vo.setEmailVerified(ua.getEmailVerified());
                    vo.setLastLoginTime(ua.getLastLoginTime());
                    // 继承entity包含role方法
                    vos.add(vo);
                }
            }
            return Result.success(new PageResult<>(vos, page.getTotal(), page.getCurrent(), page.getSize()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("管理员更新用户信息")
    @PutMapping("/{userId}")
    @RateLimit(capacity = 30, ratePerSecond = 3.0, key = "#{ip}:/admin/account/update")
    public Result<Void> adminUpdateUser(@ApiParam("用户ID") @PathVariable String userId,
                                        @ApiParam("用户信息") @RequestBody UserAccountUpdateRequest req) {
        try {
            UserAccount ua = new UserAccount();
            ua.setUserType(req.getUserType());
            ua.setStatus(req.getStatus());
            ua.setEmailVerified(req.getEmailVerified());
            boolean success = userAccountService.adminUpdateUser(userId, ua);
            if (success) {
                try {
                    com.example.backend.entity.UserAccount target = userAccountService.getById(userId);
                    if (target != null) {
                        emailService.sendSimpleMail(target.getEmail(), "账户信息变更通知", "您的账户信息已被管理员更新。如非本人操作请尽快联系支持。");
                    }
                } catch (Exception ignore) {}
            }
            return success ? Result.success() : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("修改用户状态")
    @PutMapping("/{userId}/status")
    @RateLimit(capacity = 30, ratePerSecond = 3.0, key = "#{ip}:/admin/account/status")
    public Result<Void> changeUserStatus(@ApiParam("用户ID") @PathVariable String userId,
                                         @ApiParam("修改状态请求") @RequestBody ChangeUserStatusRequest req) {
        try {
            boolean success = userAccountService.changeUserStatus(userId, req.getStatus());
            if (success) {
                try {
                    com.example.backend.entity.UserAccount target = userAccountService.getById(userId);
                    if (target != null) {
                        String subject = req.getStatus()!=null && req.getStatus()==1?"账号已启用":"账号已禁用";
                        String content = req.getStatus()!=null && req.getStatus()==1?"您的账号已被管理员启用。":"您的账号已被管理员禁用。如需申诉请联系支持。";
                        emailService.sendSimpleMail(target.getEmail(), subject, content);
                    }
                } catch (Exception ignore) {}
            }
            return success ? Result.success() : Result.error("修改失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("重置用户密码")
    @PostMapping("/{userId}/reset-password")
    @RateLimit(capacity = 20, ratePerSecond = 1.0, key = "#{ip}:/admin/account/reset-password")
    public Result<Void> resetPassword(@ApiParam("用户ID") @PathVariable String userId,
                                      @ApiParam("重置密码请求") @RequestBody ResetPasswordRequest req) {
        try {
            boolean success = userAccountService.resetPassword(userId, req.getNewPassword());
            if (success) {
                try {
                    com.example.backend.entity.UserAccount target = userAccountService.getById(userId);
                    if (target != null) {
                        emailService.sendSimpleMail(target.getEmail(), "密码重置通知", "您的登录密码已被管理员重置。如非本人操作请立即修改密码并联系支持。");
                    }
                } catch (Exception ignore) {}
            }
            return success ? Result.success() : Result.error("重置失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}


