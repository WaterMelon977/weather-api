package com.rdbackend.weather_api.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
public class AuthController {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public AuthController() {
    }

    @GetMapping("/auth/me")
    public ResponseEntity<?> me(Authentication authentication) {

        try {
            // Attempt to cast the principal to a Map
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) authentication.getPrincipal();

            // If successful, return the user map with a 200 OK status
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            // If casting fails or authentication is null, return 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User profile not found");
        }
    }

    @GetMapping("/auth/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. Invalidate Session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 2. Clear AUTH_TOKEN
        Cookie jwtCookie = new Cookie("AUTH_TOKEN", "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        // 3. Clear JSESSIONID
        Cookie sessionCookie = new Cookie("JSESSIONID", "");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(false);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(0);
        response.addCookie(sessionCookie);

        try {
            response.sendRedirect(frontendUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
