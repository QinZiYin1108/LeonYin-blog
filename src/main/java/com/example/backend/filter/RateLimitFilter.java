package com.example.backend.filter;

import com.example.backend.util.RedisRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RedisRateLimiter rateLimiter;

    // 默认桶配置：每秒补充 5 个令牌，桶容量 50（可按接口细分）
    private static final long DEFAULT_CAPACITY = 50;
    private static final double DEFAULT_RATE = 5.0;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientIp = getClientIp(request);
        String path = request.getRequestURI();

        // 对高风险接口设置更严格的限流
        long capacity = DEFAULT_CAPACITY;
        double rate = DEFAULT_RATE;
        if (path.startsWith("/auth/login") || path.startsWith("/auth/send-code") || path.startsWith("/auth/forgot-password")) {
            capacity = 20; rate = 2.0; // 更严格
        }

        String bucketKey = clientIp + ":" + path;
        boolean allowed = rateLimiter.allow(bucketKey, capacity, rate);
        if (!allowed) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":429,\"message\":\"Too Many Requests\"}");
            return;
        }
        filterChain.doFilter(request, response);
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






