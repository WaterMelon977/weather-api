package com.rdbackend.weather_api.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.rdbackend.weather_api.security.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/auth/success")
    public void loginSuccess(
            @AuthenticationPrincipal OAuth2User user,
            HttpServletResponse response) throws Exception {

        // System.out.println("------------Here are userAttributes----------------");

        // System.out.println(user.getAttributes());

        String email = user.getAttribute("email");
        String provider = user.getAuthorities().iterator().next().getAuthority();
        String name = user.getAttribute("name");

        String picture;
        if (provider.equals("OIDC_USER")) {
            picture = user.getAttribute("picture");
        } else {
            picture = user.getAttribute("avatar_url");
        }

        // System.out.println(email);
        // System.out.println(provider);
        // System.out.println(name);
        // System.out.println(picture);

        String token = jwtService.generateToken(email, provider, name, picture);

        Cookie cookie = new Cookie("AUTH_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 hour

        response.addCookie(cookie);

        // redirect to frontend
        response.sendRedirect("http://localhost:3000");
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

    @GetMapping("auth/logout")
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("AUTH_TOKEN", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        try {
            response.sendRedirect("http://localhost:3000");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
