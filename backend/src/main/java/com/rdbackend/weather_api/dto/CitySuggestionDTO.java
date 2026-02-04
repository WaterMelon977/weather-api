package com.rdbackend.weather_api.dto;

public record CitySuggestionDTO(
        String name,
        String country,
        double lat,
        double lon) {
}
