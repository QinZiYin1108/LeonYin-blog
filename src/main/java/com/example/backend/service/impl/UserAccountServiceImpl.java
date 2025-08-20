package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.common.PageResult;
import com.example.backend.dto.LoginRequest;
import com.example.backend.vo.LoginResponse;
import com.example.backend.dto.UserPageRequest;
import com.example.backend.entity.UserAccount;
import com.example.backend.entity.UserDevice;
import com.example.backend.entity.UserLog;
import com.example.backend.entity.UserProfile;
import com.example.backend.mapper.UserAccountMapper;
import com.example.backend.mapper.UserDeviceMapper;
import com.example.backend.service.EmailService;
import com.example.backend.service.SystemConfigService;
import com.example.backend.service.UserAccountService;
import com.example.backend.service.UserLogService;
import com.example.backend.service.UserProfileService;
import com.example.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * 用户账号服务实现类
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {
    
    @Autowired
    private UserAccountMapper userAccountMapper;
    
    @Autowired
    private UserProfileService userProfileService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @Autowired
    private UserLogService userLogService;
    
    @Autowired
    private UserDeviceMapper userDeviceMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private com.example.backend.service.IpGeoService ipGeoService;
    
    @Override
    @Transactional
    public UserAccount registerWithCode(String email, String password, Integer registerType, String verificationCode, String nickname) {
        // 1. 验证邮箱验证码
        if (!emailService.verifyCode(email, verificationCode)) {
            throw new RuntimeException("验证码错误或已过期");
        }
        
        // 2. 检查邮箱是否已注册
        UserAccount existingUser = getUserByEmail(email);
        if (existingUser != null) {
            throw new RuntimeException("该邮箱已被注册");
        }
        
        // 3. 创建用户账号
        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(email);
        userAccount.setPassword(passwordEncoder.encode(password));
        userAccount.setUserType(0); // 默认普通用户
        userAccount.setStatus(1); // 正常状态
        userAccount.setRegisterType(registerType);
        userAccount.setEmailVerified(1); // 验证码验证成功，邮箱已验证

        
        // 保存账号
        userAccountMapper.insert(userAccount);
        
        // 4. 创建用户信息（包含默认头像设置）
        String finalNickname = nickname;
        if (finalNickname == null || finalNickname.trim().isEmpty()) {
            // 如果没有提供昵称，使用邮箱前缀作为默认昵称
            finalNickname = email.substring(0, email.indexOf("@"));
        }
        
        // 获取系统默认头像设置
        String defaultAvatarImageId = null;
        if (systemConfigService.isAutoAssignAvatar()) {
            defaultAvatarImageId = systemConfigService.getDefaultAvatarImageId();
            // 如果默认头像ID为空字符串，则设置为null
            if (defaultAvatarImageId != null && defaultAvatarImageId.trim().isEmpty()) {
                defaultAvatarImageId = null;
            }
        }
        
        userProfileService.createProfileWithAvatar(userAccount.getId(), finalNickname, defaultAvatarImageId);
        
        return userAccount;
    }
    
    @Override
    public LoginResponse login(LoginRequest request, String clientIp) {
        try {
            switch (request.getLoginType()) {
                case 1: // 邮箱验证码登录
                    return emailCodeLogin(request, clientIp);
                case 2: // 邮箱密码登录
                    return emailPasswordLogin(request, clientIp);
                default:
                    throw new RuntimeException("不支持的登录方式");
            }
        } catch (Exception e) {
            // 记录失败日志
            userLogService.recordLog(null, "登录失败", "邮箱: " + request.getEmail() + ", 原因: " + e.getMessage(), clientIp);
            throw e;
        }
    }
    
    /**
     * 邮箱验证码登录
     */
    private LoginResponse emailCodeLogin(LoginRequest request, String clientIp) {
        // 1. 验证验证码
        if (!emailService.verifyCode(request.getEmail(), request.getVerificationCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }
        
        // 2. 查找用户
        UserAccount userAccount = getUserByEmail(request.getEmail());
        if (userAccount == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (userAccount.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        // 3. 更新登录时间
        userAccount.setLastLoginTime(System.currentTimeMillis());
        userAccount.updateTime();
        userAccountMapper.updateById(userAccount);
        
        // 4. 记录设备登录
        recordDeviceLogin(userAccount.getId(), request.getDeviceId(), request.getDeviceInfo(), clientIp);
        
        // 5. 生成JWT Token
        String token = jwtUtil.generateToken(userAccount.getId(), userAccount.getEmail(), userAccount.getRole());
        
        // 6. 获取用户完整信息
        Object userInfo = userProfileService.getFullUserInfo(userAccount.getId());
        
        // 7. 记录登录日志（含IP归属地）
        String loc = ipGeoService.resolveLocation(clientIp);
        String detail = loc == null ? "登录成功" : ("登录成功, 地理位置:" + loc);
        userLogService.recordLog(userAccount.getId(), "邮箱验证码登录", detail, clientIp);
        
        return new LoginResponse(token, userInfo, false, "登录成功");
    }
    
    /**
     * 邮箱密码登录
     */
    private LoginResponse emailPasswordLogin(LoginRequest request, String clientIp) {
        // 1. 查找用户
        UserAccount userAccount = getUserByEmail(request.getEmail());
        if (userAccount == null) {
            throw new RuntimeException("邮箱或密码错误");
        }
        
        if (userAccount.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        // 2. 验证密码
        if (!passwordEncoder.matches(request.getPassword(), userAccount.getPassword())) {
            throw new RuntimeException("邮箱或密码错误");
        }
        
        // 3. 检查是否首次在该设备登录
        boolean isFirstTimeOnDevice = isFirstTimeOnDevice(userAccount.getId(), request.getDeviceId());
        
        if (isFirstTimeOnDevice) {
            // 首次登录需要验证码
            if (request.getVerificationCode() == null || request.getVerificationCode().trim().isEmpty()) {
                return new LoginResponse(null, null, true, "首次在该设备登录，请输入邮箱验证码");
            }
            
            // 验证验证码
            if (!emailService.verifyCode(request.getEmail(), request.getVerificationCode())) {
                throw new RuntimeException("验证码错误或已过期");
            }
        }
        
        // 4. 更新登录时间
        userAccount.setLastLoginTime(System.currentTimeMillis());
        userAccount.updateTime();
        userAccountMapper.updateById(userAccount);
        
        // 5. 记录设备登录
        recordDeviceLogin(userAccount.getId(), request.getDeviceId(), request.getDeviceInfo(), clientIp);
        
        // 6. 生成JWT Token
        String token = jwtUtil.generateToken(userAccount.getId(), userAccount.getEmail(), userAccount.getRole());
        
        // 7. 获取用户完整信息
        Object userInfo = userProfileService.getFullUserInfo(userAccount.getId());
        
        // 8. 记录登录日志（含IP归属地）
        String loc2 = ipGeoService.resolveLocation(clientIp);
        String detail2 = loc2 == null ? "登录成功" : ("登录成功, 地理位置:" + loc2);
        userLogService.recordLog(userAccount.getId(), "邮箱密码登录", detail2, clientIp);
        
        return new LoginResponse(token, userInfo, false, "登录成功");
    }
    
    /**
     * 检查是否首次在该设备登录
     */
    private boolean isFirstTimeOnDevice(String userId, String deviceId) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            return true; // 没有设备ID，认为是首次登录
        }
        
        QueryWrapper<UserDevice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("device_id", deviceId)
                   .eq("status", 1);
        
        UserDevice userDevice = userDeviceMapper.selectOne(queryWrapper);
        return userDevice == null;
    }
    
    /**
     * 记录设备登录
     */
    private void recordDeviceLogin(String userId, String deviceId, String deviceInfo, String ipAddress) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            return; // 没有设备ID，不记录
        }
        
        QueryWrapper<UserDevice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("device_id", deviceId);
        
        UserDevice userDevice = userDeviceMapper.selectOne(queryWrapper);
        
        if (userDevice == null) {
            // 新设备，创建记录
            userDevice = new UserDevice();
            userDevice.setUserId(userId);
            userDevice.setDeviceId(deviceId);
            userDevice.setDeviceInfo(deviceInfo);
            userDevice.setIpAddress(ipAddress);
            userDevice.setLastLoginTime(System.currentTimeMillis());
            userDevice.setStatus(1);
            userDeviceMapper.insert(userDevice);
        } else {
            // 更新现有设备记录
            userDevice.setDeviceInfo(deviceInfo);
            userDevice.setIpAddress(ipAddress);
            userDevice.setLastLoginTime(System.currentTimeMillis());
            userDevice.setStatus(1);
            userDevice.updateTime();
            userDeviceMapper.updateById(userDevice);
        }
    }
    
    @Override
    public UserAccount getUserByEmail(String email) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userAccountMapper.selectOne(queryWrapper);
    }
    
    @Override
    public boolean changeUserStatus(String userId, Integer status) {
        UserAccount userAccount = userAccountMapper.selectById(userId);
        if (userAccount == null) {
            return false;
        }
        
        userAccount.setStatus(status);
        userAccount.updateTime();
        return userAccountMapper.updateById(userAccount) > 0;
    }
    
    @Override
    public PageResult<UserAccount> pageUsers(UserPageRequest request) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        
        // 添加查询条件
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            queryWrapper.like("email", request.getEmail());
        }
        if (request.getUserType() != null) {
            queryWrapper.eq("user_type", request.getUserType());
        }
        if (request.getStatus() != null) {
            queryWrapper.eq("status", request.getStatus());
        }
        
        // 排序
        queryWrapper.orderByDesc("create_time");
        
        // 分页查询
        Page<UserAccount> page = new Page<>(request.getCurrent(), request.getSize());
        IPage<UserAccount> pageResult = userAccountMapper.selectPage(page, queryWrapper);
        
        return PageResult.success(pageResult.getRecords(), pageResult.getTotal(), 
                                 request.getCurrent(), request.getSize());
    }
    
    @Override
    public boolean adminUpdateUser(String userId, UserAccount userAccount) {
        UserAccount existingUser = userAccountMapper.selectById(userId);
        if (existingUser == null) {
            return false;
        }
        
        // 只允许更新某些字段
        existingUser.setUserType(userAccount.getUserType());
        existingUser.setStatus(userAccount.getStatus());
        existingUser.setEmailVerified(userAccount.getEmailVerified());

        existingUser.updateTime();
        
        return userAccountMapper.updateById(existingUser) > 0;
    }
    
    @Override
    public void recordUserLog(String userId, String operation, String details, String ip) {
        userLogService.recordLog(userId, operation, details, ip);
    }
    
    @Override
    public PageResult<Object> pageUserLogs(String userId, Integer page, Integer size) {
        PageResult<UserLog> result = userLogService.pageUserLogs(page, size, userId, null);
        // 创建新的PageResult，处理类型转换
        PageResult<Object> objectResult = new PageResult<>();
        objectResult.setRecords(result.getRecords().stream().map(log -> (Object) log).collect(Collectors.toList()));
        objectResult.setTotal(result.getTotal());
        objectResult.setCurrent((long) result.getCurrent().intValue());
        objectResult.setSize((long) result.getSize().intValue());
        return objectResult;
    }
    
    @Override
    public boolean resetPassword(String userId, String newPassword) {
        UserAccount userAccount = userAccountMapper.selectById(userId);
        if (userAccount == null) {
            return false;
        }
        
        // 加密新密码
        userAccount.setPassword(passwordEncoder.encode(newPassword));
        userAccount.updateTime();
        
        return userAccountMapper.updateById(userAccount) > 0;
    }

    @Override
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        UserAccount userAccount = userAccountMapper.selectById(userId);
        if (userAccount == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, userAccount.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }
        if (passwordEncoder.matches(newPassword, userAccount.getPassword())) {
            throw new RuntimeException("新密码不能与旧密码相同");
        }
        userAccount.setPassword(passwordEncoder.encode(newPassword));
        userAccount.updateTime();
        return userAccountMapper.updateById(userAccount) > 0;
    }
}



