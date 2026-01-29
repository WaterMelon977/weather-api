package com.rdbackend.weather_api.dto;

import java.io.Serializable;
import java.time.Instant;

public record WeatherResponseDTO(
        String city, // city name (e.g. "Hyderabad")
        double temperature, // current temperature in °C
        double feelsLike, // "feels like" temperature in °C
        int humidity, // percentage (0–100)
        double windSpeed, // in m/s
        String condition, // main weather condition (e.g., "Clouds")

        // --- New Fields ---
        int visibility, // Visibility in meters (e.g., 6000)
        Instant sunrise, // Sunrise time (UTC)
        Instant sunset, // Sunset time (UTC)
        double latitude, // decimal degrees
        double longitude,
        String icon, // decimal degrees

        // ------------------

        Instant timestamp // when this data was received
) implements Serializable {

    public WeatherResponseDTO {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City name cannot be empty");
        }
        if (humidity < 0 || humidity > 100) {
            throw new IllegalArgumentException("Humidity must be between 0 and 100");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp is required");
        }
    }

}
