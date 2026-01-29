package com.rdbackend.weather_api.service;

import java.time.Instant;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.rdbackend.weather_api.client.OpenWeatherClient;
import com.rdbackend.weather_api.dto.ForecastResponseDTO;
import com.rdbackend.weather_api.dto.WeatherResponseDTO;

@Service
public class WeatherServiceImpl implements WeatherService {

    private final OpenWeatherClient client;

    public WeatherServiceImpl(OpenWeatherClient client) {
        this.client = client;
    }

    @Cacheable(value = "weather:current", key = "#city.trim().toLowerCase()")
    @Override
    public WeatherResponseDTO getCurrentWeather(String city) {

        return client.getCurrentWeather(city.trim().toLowerCase());
    }

    @Cacheable(value = "weather:forecast", key = "#city.trim().toLowerCase()")
    @Override
    public ForecastResponseDTO getForecast(String city) {
        return client.getForecast(city.trim().toLowerCase());
    }

}
