package com.rdbackend.weather_api.dto;

import java.io.Serializable;
import java.time.LocalDate;

public record DailyWeatherDTO(
                LocalDate date,
                double maxTemp,
                double minTemp,
                int humidity,
                String condition,
                String icon,
                String sunRise,
                String sunSet) implements Serializable {
}
