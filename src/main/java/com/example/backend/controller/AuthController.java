package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.LoginRequest;
import com.example.backend.vo.LoginResponse;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.dto.ForgotPasswordRequest;
import com.example.backend.util.RateLimit;
import com.example.backend.entity.UserAccount;
import com.example.backend.service.EmailService;
import com.example.backend.service.UserAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Api(tags = "用户认证模块")
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserAccountService userAccountService;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * 用户注册
     */
        @ApiOperation("用户注册")
    @PostMapping("/register")
    @RateLimit(capacity = 20, ratePerSecond = 0.5, key = "#{ip}:/auth/register")
    public Result<UserAccount> register(@ApiParam("注册请求参数") @Validated @RequestBody RegisterRequest request) {
        try {
            UserAccount userAccount = userAccountService.registerWithCode(
                request.getEmail(),
                request.getPassword(),
                request.getRegisterType(),
                request.getVerificationCode(),
                request.getNickname()
            );
            return Result.success("注册成功", userAccount);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户登录（支持多种登录方式）
     */
    @ApiOperation(value = "用户登录", notes = "支持邮箱验证码登录、邮箱密码登录两种方式")
    @PostMapping("/login")
    @RateLimit(capacity = 15, ratePerSecond = 2.0, key = "#{ip}:/auth/login")
    public Result<LoginResponse> login(@ApiParam("登录请求参数") @Validated @RequestBody LoginRequest request,
                                     HttpServletRequest httpRequest) {
        try {
            // 获取客户端IP地址
            String clientIp = getClientIpAddress(httpRequest);
            
                            LoginResponse response = userAccountService.login(request, clientIp);
            
            if (response.getNeedVerification() != null && response.getNeedVerification()) {
                // 需要验证码的情况
                return Result.success(response.getMessage(), response);
            } else {
                // 登录成功
                return Result.success("登录成功", response);
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个IP的情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        return ip;
    }
    
    /**
     * 发送邮箱验证码
     */
    @ApiOperation("发送邮箱验证码")
    @PostMapping("/send-code")
    @RateLimit(capacity = 10, ratePerSecond = 1.5, key = "#{ip}:/auth/send-code")
    public Result<String> sendEmailCode(@ApiParam("邮箱地址") @RequestParam String email) {
        try {
            emailService.sendVerificationCode(email);
            return Result.success("验证码发送成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 找回密码：验证码校验后设置新密码
     */
    @ApiOperation("找回密码")
    @PostMapping("/forgot-password")
    @RateLimit(capacity = 10, ratePerSecond = 0.5, key = "#{ip}:/auth/forgot-password")
    public Result<String> forgotPassword(@ApiParam("找回密码请求") @Validated @RequestBody ForgotPasswordRequest request) {
        try {
            // 校验验证码
            boolean ok = emailService.verifyCode(request.getEmail(), request.getVerificationCode());
            if (!ok) {
                return Result.error("验证码错误或已过期");
            }

            // 查找用户
            com.example.backend.entity.UserAccount user = userAccountService.getUserByEmail(request.getEmail());
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 新密码不能与旧密码相同
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
            if (encoder.matches(request.getNewPassword(), user.getPassword())) {
                return Result.error("新密码不能与旧密码相同");
            }

            // 重置密码
            boolean success = userAccountService.resetPassword(user.getId(), request.getNewPassword());
            return success ? Result.success("密码重置成功") : Result.error("重置失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    

} 
 
 
 