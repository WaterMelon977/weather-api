package com.rdbackend.weather_api.dto;

import java.io.Serializable;
import java.util.List;

public record ForecastResponseDTO(String city, List<DailyWeatherDTO> dailyForecasts) implements Serializable {

    public ForecastResponseDTO {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City name cannot be empty");
        }
        if (dailyForecasts == null || dailyForecasts.isEmpty()) {
            throw new IllegalArgumentException("Forecasts cannot be empty");
        }
    }

}
