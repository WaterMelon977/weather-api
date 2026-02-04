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

import com.rdbackend.weather_api.entity.User;
import com.rdbackend.weather_api.security.JwtService;
import com.rdbackend.weather_api.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
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
        userService.upsertOAuthUser(email, name, picture, provider);
        // System.out.println("User: " + user2);

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
            response.sendRedirect("http://localhost:3000");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
