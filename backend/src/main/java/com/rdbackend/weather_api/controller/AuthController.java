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

    @Value("${app.cookie.secure:false}")
    private boolean isProd;

    public AuthController() {
    }

    @GetMapping("/auth/me")
    public ResponseEntity<?> me(Authentication authentication) {

        // Check if authentication is null
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not authenticated"));
        }

        try {
            // Attempt to cast the principal to a Map
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) authentication.getPrincipal();

            // If successful, return the user map with a 200 OK status
            return ResponseEntity.ok(user);

        } catch (ClassCastException e) {
            // If casting fails, return 500 with error details
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Invalid authentication principal",
                            "type", authentication.getPrincipal().getClass().getName()));
        }
    }

    @GetMapping("/auth/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. Invalidate Session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 2. Clear AUTH_TOKEN with proper SameSite attribute
        String clearAuthCookie = String.format(
                "AUTH_TOKEN=; Path=/; HttpOnly; Max-Age=0; %s SameSite=%s",
                isProd ? "Secure;" : "",
                isProd ? "None" : "Lax");
        response.addHeader("Set-Cookie", clearAuthCookie);

        // 3. Clear JSESSIONID
        String clearSessionCookie = String.format(
                "JSESSIONID=; Path=/; HttpOnly; Max-Age=0; %s SameSite=%s",
                isProd ? "Secure;" : "",
                isProd ? "None" : "Lax");
        response.addHeader("Set-Cookie", clearSessionCookie);

        try {
            response.sendRedirect(frontendUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
