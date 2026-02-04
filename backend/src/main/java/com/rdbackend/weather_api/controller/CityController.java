package com.rdbackend.weather_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rdbackend.weather_api.dto.CitySuggestionDTO;
import com.rdbackend.weather_api.service.WeatherService;

@RestController
public class CityController {

    private final WeatherService cityService;

    public CityController(WeatherService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/api/cities")
    public List<CitySuggestionDTO> suggest(
            @RequestParam String q) {
        return cityService.suggestCities(q);
    }

}
