package com.rdbackend.weather_api.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

        private final JwtService jwtService;
        private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

        @Value("${app.frontend-url}")
        private String frontendUrl;

        public SecurityConfig(JwtService jwtService, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
                this.jwtService = jwtService;
                this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/weather/forecast/**").authenticated()
                                                .requestMatchers("/auth/**").permitAll()
                                                .requestMatchers("/api/**").permitAll()
                                                .requestMatchers(
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**")
                                                .permitAll()
                                                .anyRequest().permitAll())
                                .oauth2Login(oauth -> oauth
                                                .successHandler(oAuth2LoginSuccessHandler))
                                .addFilterBefore(
                                                new JwtAuthenticationFilter(jwtService.getKey()),
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Support multiple origins (comma-separated)
                String[] origins = frontendUrl.split(",");

                // Log the configured origins for debugging
                System.out.println("🔧 CORS Configuration:");
                System.out.println("   Raw FRONTEND_URL: " + frontendUrl);
                System.out.println("   Parsed Origins: " + Arrays.toString(origins));

                configuration.setAllowedOrigins(Arrays.asList(origins));

                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                return source;
        }

}
