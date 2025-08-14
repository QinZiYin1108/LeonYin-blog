package com.example.backend.filter;

import com.example.backend.util.JwtUtil;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String token = getTokenFromRequest(request);
        
        System.out.println("JWT Filter - Request: " + request.getRequestURI());
        System.out.println("JWT Filter - Token: " + (token != null ? "Present" : "Missing"));
        
        if (StringUtils.hasText(token)) {
            System.out.println("JWT Filter - Token validation: " + jwtUtil.validateToken(token));
            
            if (jwtUtil.validateToken(token)) {
                try {
                    String email = jwtUtil.getUsernameFromToken(token);
                    String userId = jwtUtil.getUserIdFromToken(token);
                    Integer userType = jwtUtil.getUserTypeFromToken(token);
                    
                    System.out.println("JWT Filter - User ID: " + userId + ", Email: " + email + ", UserType: " + userType);
                    
                    // 创建认证对象
                    String role = userType == 1 ? "ROLE_ADMIN" : "ROLE_USER";
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.singletonList(authority));
                    
                    // 设置用户信息到认证对象中
                    authentication.setDetails(new UserDetails(userId, email, userType));
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    System.out.println("JWT Filter - Authentication set successfully for user: " + userId);
                    
                } catch (Exception e) {
                    System.out.println("JWT Filter - Authentication failed: " + e.getMessage());
                    logger.error("JWT认证失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    /**
     * 用户详情类
     */
    public static class UserDetails {
        private String userId;
        private String email;
        private Integer userType;
        
        public UserDetails(String userId, String email, Integer userType) {
            this.userId = userId;
            this.email = email;
            this.userType = userType;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public String getEmail() {
            return email;
        }
        
        public Integer getUserType() {
            return userType;
        }
    }
} 
 