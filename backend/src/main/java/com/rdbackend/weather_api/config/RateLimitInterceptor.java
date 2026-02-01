package com.rdbackend.weather_api.config;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;

    public RateLimitInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String clientIp = extractClientIp(request);
        String key = "rate:ip:" + clientIp;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            // first request → set TTL
            redisTemplate.expire(key, Duration.ofSeconds(60));
        }

        if (count != null && count > 10) {
            response.setStatus(429);
            response.getWriter().write("Too many requests. Try again later.");
            return false;
        }

        return true;
    }

    private String extractClientIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}