package com.example.backend.config;

import com.example.backend.util.RateLimit;
import com.example.backend.util.RedisRateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private RedisRateLimiter rateLimiter;

    @Autowired
    private HttpServletRequest request;

    @Around("@annotation(com.example.backend.util.RateLimit)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        RateLimit rl = method.getAnnotation(RateLimit.class);

        String ip = getClientIp(request);
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            String auth = request.getHeader("Authorization");
            // 尝试用JWT解析（若失败则置空）
            try {
                // 简易解析：项目已有 JwtUtil，可按需注入使用；此处保持空安全
            } catch (Exception ignored) {}
        }
        String bucketKey = rl.key()
                .replace("#{ip}", ip)
                .replace("#{method}", method.getName())
                .replace("#{userId}", userId == null ? "anon" : userId);

        boolean allowed = rateLimiter.allow(bucketKey, rl.capacity(), rl.ratePerSecond(), rl.requestedTokens());
        if (!allowed) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests");
        }
        return pjp.proceed();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            if (ip.contains(",")) return ip.split(",")[0].trim();
            return ip;
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}


