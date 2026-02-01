package com.rdbackend.weather_api.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        // Step 1: extract client identifier (IP for now)
        String clientIp = extractClientIp(request);
        System.out.println("RateLimitInterceptor hit for IP: " + clientIp);

        // TEMP: allow all requests for now
        return true;
    }

    private String extractClientIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}