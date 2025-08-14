package com.example.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 邮箱验证Token工具类
 */
@Component
public class EmailVerificationTokenUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    // 验证token有效期（30分钟）
    private final long VERIFICATION_TOKEN_EXPIRATION = 30 * 60 * 1000L;
    
    /**
     * 生成邮箱验证token
     */
    public String generateVerificationToken(String email, Integer purpose) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + VERIFICATION_TOKEN_EXPIRATION);
        
        // 创建安全的密钥对象
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setSubject(email)
                .claim("purpose", purpose) // 0-注册，1-登录，2-重置密码
                .claim("type", "email_verification")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * 验证邮箱验证token
     */
    public boolean validateVerificationToken(String token, String email, Integer purpose) {
        try {
            // 创建安全的密钥对象
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            String tokenEmail = claims.getSubject();
            Integer tokenPurpose = claims.get("purpose", Integer.class);
            String tokenType = claims.get("type", String.class);
            
            return email.equals(tokenEmail) 
                    && purpose.equals(tokenPurpose)
                    && "email_verification".equals(tokenType)
                    && !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 从token中获取邮箱地址
     */
    public String getEmailFromToken(String token) {
        try {
            // 创建安全的密钥对象
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 从token中获取用途
     */
    public Integer getPurposeFromToken(String token) {
        try {
            // 创建安全的密钥对象
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("purpose", Integer.class);
        } catch (Exception e) {
            return null;
        }
    }
}
 
 
 