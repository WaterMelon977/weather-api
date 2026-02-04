package com.rdbackend.weather_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openweathermap")
public class WeatherApiProperties {

    private String baseUrl;
    private String key;
    private String baseUrl2;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBaseUrl2() {
        return baseUrl2;
    }

    public void setBaseUrl2(String baseUrl2) {
        this.baseUrl2 = baseUrl2;
    }

}
