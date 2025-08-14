package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.common.PageResult;
import com.example.backend.entity.EmailVerification;
import com.example.backend.entity.User;
import com.example.backend.mapper.EmailVerificationMapper;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.UserService;
import com.example.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Random;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private EmailVerificationMapper emailVerificationMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public User register(String email, String password, String nickname, Integer registerType) {
        // 检查邮箱是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User existUser = userMapper.selectOne(queryWrapper);
        if (existUser != null) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setRegisterType(registerType);
        user.setUserType(0); // 普通用户
        user.setStatus(1); // 正常状态
        user.setEmailVerified(0); // 未验证
        user.setRealNameVerified(0); // 未实名认证
        
        if (StringUtils.hasText(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }
        
        userMapper.insert(user);
        return user;
    }
    
    @Override
    public String login(String email, String password) {
        // 查找用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User user = userMapper.selectOne(queryWrapper);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 生成JWT token
        return jwtUtil.generateToken(user.getId(), user.getEmail(), user.getUserType());
    }
    
    @Override
    public void sendEmailCode(String email, Integer purpose) {
        // 生成6位验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        
        // 保存验证码到数据库
        EmailVerification verification = new EmailVerification();
        verification.setEmail(email);
        verification.setCode(code);
        verification.setPurpose(purpose);
        emailVerificationMapper.insert(verification);
        
        // 发送邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Leon's Blog 验证码");
        message.setText("您的验证码是：" + code + "，5分钟内有效，请勿泄露给他人。");
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("邮件发送失败：" + e.getMessage());
        }
    }
    
    @Override
    public boolean verifyEmailCode(String email, String code, Integer purpose) {
        QueryWrapper<EmailVerification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email)
                   .eq("code", code)
                   .eq("purpose", purpose)
                   .eq("used", 0)
                   .gt("expire_time", System.currentTimeMillis());
        
        EmailVerification verification = emailVerificationMapper.selectOne(queryWrapper);
        if (verification == null) {
            return false;
        }
        
        // 标记为已使用
        verification.setUsed(1);
        verification.updateTime();
        emailVerificationMapper.updateById(verification);
        
        return true;
    }
    
    @Override
    public User getUserByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userMapper.selectOne(queryWrapper);
    }
    
    @Override
    public boolean updateUserInfo(String userId, User user) {
        User existUser = userMapper.selectById(userId);
        if (existUser == null) {
            return false;
        }
        
        user.setId(userId);
        user.updateTime();
        return userMapper.updateById(user) > 0;
    }
    
    @Override
    public boolean realNameVerification(String userId, String realName, String idCard) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        
        user.setRealName(realName);
        user.setIdCard(idCard);
        user.setRealNameVerified(1);
        user.updateTime();
        
        return userMapper.updateById(user) > 0;
    }
    
    @Override
    public PageResult<User> pageUsers(Integer current, Integer size, String keyword) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("nickname", keyword)
                       .or()
                       .like("email", keyword);
        }
        
        queryWrapper.orderByDesc("create_time");
        
        IPage<User> page = new Page<>(current, size);
        IPage<User> result = userMapper.selectPage(page, queryWrapper);
        
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    public boolean changeUserStatus(String userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        
        user.setStatus(status);
        user.updateTime();
        
        return userMapper.updateById(user) > 0;
    }
} 