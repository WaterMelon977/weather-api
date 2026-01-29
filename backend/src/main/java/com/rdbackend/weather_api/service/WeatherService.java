package com.rdbackend.weather_api.service;

import com.rdbackend.weather_api.dto.ForecastResponseDTO;
import com.rdbackend.weather_api.dto.WeatherResponseDTO;

public interface WeatherService {
    WeatherResponseDTO getCurrentWeather(String city);

    ForecastResponseDTO getForecast(String city);

}
