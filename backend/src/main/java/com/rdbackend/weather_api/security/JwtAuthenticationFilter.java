package com.rdbackend.weather_api.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecretKey key;

    public JwtAuthenticationFilter(SecretKey key) {
        this.key = key;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException, jakarta.servlet.ServletException {

        String token = extractTokenFromCookie(request);

        if (token != null) {
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String email = claims.getSubject();

                String name = claims.get("name", String.class);

                String picture = claims.get("picture", String.class);

                Map<String, Object> principal = Map.of(
                        "email", email,
                        "name", name,
                        "picture", picture);

                var auth = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        Collections.emptyList());

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                // invalid token → clear context
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null)
            return null;

        for (Cookie cookie : request.getCookies()) {
            if ("AUTH_TOKEN".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}