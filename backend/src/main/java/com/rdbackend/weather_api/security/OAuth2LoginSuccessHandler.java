package com.rdbackend.weather_api.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.rdbackend.weather_api.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserService userService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.cookie.secure:false}")
    private boolean isProd;

    public OAuth2LoginSuccessHandler(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth2 user not found");
            return;
        }

        OAuth2User user = (OAuth2User) authentication.getPrincipal();

        // Extract user details
        String email = user.getAttribute("email");
        String name = user.getAttribute("name");
        String provider = user.getAuthorities().iterator().next().getAuthority();

        // Handle different OAuth2 providers (Google uses "picture", GitHub uses
        // "avatar_url")
        String picture;
        if (provider.equals("OIDC_USER")) {
            picture = user.getAttribute("picture");
        } else {
            picture = user.getAttribute("avatar_url");
        }

        // Save or update user in database
        userService.upsertOAuthUser(email, name, picture, provider);

        // Generate JWT token
        String token = jwtService.generateToken(email, provider, name, picture);

        // Create HttpOnly cookie with JWT
        Cookie cookie = new Cookie("AUTH_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(isProd); // true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 hour

        response.addCookie(cookie);

        // Redirect to frontend - frontend will then call /auth/me to get user details
        String targetUrl = frontendUrl.split(",")[0]; // Use first URL if multiple are configured
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
