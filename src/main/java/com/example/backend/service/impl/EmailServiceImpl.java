package com.example.backend.service.impl;

import com.example.backend.dto.EmailVerificationResponse;
import com.example.backend.service.EmailService;
import com.example.backend.util.EmailVerificationTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 邮箱服务实现类
 */
@Service
public class EmailServiceImpl implements EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private EmailVerificationTokenUtil emailVerificationTokenUtil;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${blog.verification.code-length:6}")
    private int codeLength;
    
    @Value("${blog.verification.expire-minutes:5}")
    private long expireMinutes;
    
    private static final String VERIFICATION_CODE_PREFIX = "verification_code:";
    
    @Override
    public boolean sendVerificationCode(String email) {
        try {
            // 生成验证码
            String code = generateCode();
            System.out.println("生成验证码: " + code + " for email: " + email);
            
            // 存储到Redis中，设置过期时间
            String key = VERIFICATION_CODE_PREFIX + email;
            System.out.println("Redis key: " + key);
            redisTemplate.opsForValue().set(key, code, expireMinutes, TimeUnit.MINUTES);
            
            // 验证是否成功存储
            String storedCode = redisTemplate.opsForValue().get(key);
            System.out.println("存储到Redis的验证码: " + storedCode);
            
            // 创建邮件模板上下文
            Context context = new Context();
            context.setVariable("verificationCode", code);
            context.setVariable("expireMinutes", expireMinutes);
            
            // 处理HTML模板
            String htmlContent = templateEngine.process("email-verification", context);
            
            // 发送HTML邮件
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("【LeonYin个人博客】邮箱验证码");
            helper.setText(htmlContent, true); // true表示发送HTML格式
            
            mailSender.send(mimeMessage);
            System.out.println("邮件发送成功到: " + email);
            return true;
        } catch (MessagingException e) {
            System.err.println("邮件发送失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("验证码发送过程出错: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean verifyCode(String email, String code) {
        try {
            String key = VERIFICATION_CODE_PREFIX + email;
            System.out.println("验证验证码 - email: " + email + ", inputCode: " + code);
            System.out.println("查找Redis key: " + key);
            
            String storedCode = redisTemplate.opsForValue().get(key);
            System.out.println("Redis中存储的验证码: " + storedCode);
            
            if (storedCode != null && storedCode.equals(code)) {
                System.out.println("验证码验证成功");
                // 验证成功后删除验证码
                redisTemplate.delete(key);
                return true;
            }
            System.out.println("验证码验证失败 - 输入: " + code + ", 存储: " + storedCode);
            return false;
        } catch (Exception e) {
            System.err.println("验证码验证过程出错: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public EmailVerificationResponse verifyCodeAndGenerateToken(String email, String code, Integer purpose) {
        try {
            String key = VERIFICATION_CODE_PREFIX + email;
            System.out.println("生成token验证 - email: " + email + ", inputCode: " + code + ", purpose: " + purpose);
            System.out.println("查找Redis key: " + key);
            
            String storedCode = redisTemplate.opsForValue().get(key);
            System.out.println("Redis中存储的验证码: " + storedCode);
            
            if (storedCode != null && storedCode.equals(code)) {
                System.out.println("验证码验证成功，生成token");
                // 验证成功后删除验证码
                redisTemplate.delete(key);
                
                // 生成邮箱验证token
                String verificationToken = emailVerificationTokenUtil.generateVerificationToken(email, purpose);
                
                return new EmailVerificationResponse(verificationToken, email, purpose, 30);
            } else {
                System.out.println("验证码验证失败 - 输入: " + code + ", 存储: " + storedCode);
                throw new RuntimeException("验证码错误或已过期");
            }
        } catch (Exception e) {
            System.err.println("生成token过程出错: " + e.getMessage());
            throw new RuntimeException("验证码验证失败：" + e.getMessage());
        }
    }
    
    /**
     * 生成验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        try {
            // 使用简单的HTML模板美化
            String html = "<div style='font-family:Arial,Helvetica,sans-serif;padding:16px;background:#f6f8fa'>"
                    + "<div style='max-width:560px;margin:0 auto;background:#fff;border:1px solid #eee;border-radius:8px;overflow:hidden'>"
                    + "<div style='background:#1f6feb;color:#fff;padding:12px 16px;font-size:16px'>" + subject + "</div>"
                    + "<div style='padding:16px;color:#333;line-height:1.6'>" + content.replaceAll("\n","<br/>") + "</div>"
                    + "<div style='padding:12px 16px;color:#999;font-size:12px;border-top:1px solid #eee'>此邮件为系统通知，请勿直接回复。</div>"
                    + "</div></div>";

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("发送通知邮件失败: " + e.getMessage());
        }
    }
} 
 
 
 