package com.rdbackend.weather_api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(WeatherApiProperties.class)
public class WebClientConfig {
    private static final Logger log = LoggerFactory.getLogger(WebClientConfig.class);

    @Bean
    public WebClient webClient(WeatherApiProperties props) {
        log.info("Creating WebClient instance with base URL: {}", props.getBaseUrl());
        return WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .build();
    }
}
