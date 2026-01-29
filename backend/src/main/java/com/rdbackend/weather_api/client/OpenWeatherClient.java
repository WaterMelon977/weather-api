package com.rdbackend.weather_api.client;

import com.rdbackend.weather_api.dto.ForecastResponseDTO;
import com.rdbackend.weather_api.dto.WeatherResponseDTO;

public interface OpenWeatherClient {

    public WeatherResponseDTO getCurrentWeather(String city);

    public ForecastResponseDTO getForecast(String city);

}
