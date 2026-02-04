package com.rdbackend.weather_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rdbackend.weather_api.dto.WeatherResponseDTO;
import com.rdbackend.weather_api.service.WeatherService;
import com.rdbackend.weather_api.dto.ForecastResponseDTO;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/current")
    public ResponseEntity<WeatherResponseDTO> getCurrentWeather(
            @RequestParam String city) {

        WeatherResponseDTO response = weatherService.getCurrentWeather(city);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/forecast")
    public ResponseEntity<ForecastResponseDTO> getForecast(
            @RequestParam String city) {

        ForecastResponseDTO response = weatherService.getForecast(city);
        return ResponseEntity.ok(response);
    }

}
