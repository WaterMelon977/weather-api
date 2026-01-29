package com.rdbackend.weather_api.client;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Mono;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdbackend.weather_api.config.WeatherApiProperties;
import com.rdbackend.weather_api.dto.DailyWeatherDTO;
import com.rdbackend.weather_api.dto.ForecastResponseDTO;
import com.rdbackend.weather_api.dto.WeatherResponseDTO;

@Component
public class OpenWeatherClientImpl implements OpenWeatherClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final WeatherApiProperties props;
    private static final Logger log = LoggerFactory.getLogger(OpenWeatherClientImpl.class);

    public OpenWeatherClientImpl(WebClient webClient, ObjectMapper objectMapper, WeatherApiProperties props) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.props = props;
    }

    @Override
    public WeatherResponseDTO getCurrentWeather(String city) {
        // 1. Build the URI separately using UriComponentsBuilder
        // Note: If your WebClient has a Base URL configured, using a relative path here
        // ("/weather")
        // will merge correctly, but sysout will only print the relative path.
        // If you want the full URL in logs, use the full host here or use a WebClient
        // Filter.
        log.info("Calling OpenWeather API for city: {}", city);
        URI uri = UriComponentsBuilder.fromUriString(props.getBaseUrl())
                .path("/weather")
                .queryParam("q", city)
                .queryParam("appid", props.getKey())
                .queryParam("units", "metric")
                .build()
                .toUri();

        // System.out.println("Constructed URI: " + uri);

        // 2. Pass the built URI to the WebClient
        String response = webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono
                                .error(new RuntimeException("OpenWeather API client error: " + errorBody))))
                .onStatus(status -> status.is5xxServerError(), clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono
                                .error(new RuntimeException("OpenWeather API server error: " + errorBody))))
                .bodyToMono(String.class)
                .block();

        // System.out.println("Response from OpenWeather API for " + city + ": " +
        // response);

        return mapToWeatherResponse(response, city);

    }

    private WeatherResponseDTO mapToWeatherResponse(String response, String city) {
        try {
            JsonNode root = objectMapper.readTree(response);

            double temp = root.path("main").path("temp").asDouble();
            double feelsLike = root.path("main").path("feels_like").asDouble();
            int humidity = root.path("main").path("humidity").asInt();
            double windSpeed = root.path("wind").path("speed").asDouble();
            String condition = root.path("weather").get(0).path("description").asText();
            int visibility = root.path("visibility").asInt();
            // 1. Extract the raw long value
            long sunriseEpoch = root.path("sys").path("sunrise").asLong();
            long sunsetEpoch = root.path("sys").path("sunset").asLong();

            // 2. Convert to Instant
            Instant sunRise = Instant.ofEpochSecond(sunriseEpoch);
            Instant sunSet = Instant.ofEpochSecond(sunsetEpoch);
            double latitude = root.path("coord").path("lat").asDouble();
            double longitude = root.path("coord").path("lon").asDouble();
            String icon = root.path("weather").get(0).path("icon").asText();

            return new WeatherResponseDTO(
                    city,
                    temp,
                    feelsLike,
                    humidity,
                    windSpeed,
                    condition,
                    visibility,
                    sunRise,
                    sunSet,
                    latitude,
                    longitude,
                    icon,
                    Instant.now());

        } catch (Exception e) {
            throw new RuntimeException("Failed to map response to WeatherResponseDTO", e);
        }
    }

    @Override
    public ForecastResponseDTO getForecast(String city) {

        log.info("Calling forecast OpenWeather API for city: {}", city);
        URI uri = UriComponentsBuilder.fromUriString(props.getBaseUrl())
                .path("/forecast")
                .queryParam("q", city)
                .queryParam("appid", props.getKey())
                .queryParam("units", "metric")
                .build()
                .toUri();

        // System.out.println("Constructed URI: " + uri);

        // 2. Pass the built URI to the WebClient
        String response = webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono
                                .error(new RuntimeException("OpenWeather API client error: " + errorBody))))
                .onStatus(status -> status.is5xxServerError(), clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono
                                .error(new RuntimeException("OpenWeather API server error: " + errorBody))))
                .bodyToMono(String.class)
                .block();

        // System.out.println("Response from OpenWeather API for " + city + ": " +
        // response);

        return convertToDaily(response, city);
    }

    private ForecastResponseDTO convertToDaily(String response, String city) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode list = root.path("list");

            List<JsonNode> nodes = new ArrayList<>();
            list.forEach(nodes::add);

            // Group by Date
            Map<LocalDate, List<JsonNode>> byDate = nodes.stream()
                    .collect(Collectors.groupingBy(node -> {
                        String dtTxt = node.path("dt_txt").asText();
                        return LocalDate.parse(dtTxt.split(" ")[0]);
                    }));

            List<DailyWeatherDTO> dailyForecasts = byDate.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> {
                        LocalDate date = entry.getKey();
                        List<JsonNode> dayNodes = entry.getValue();

                        // High and Low temps for the day
                        double maxTemp = dayNodes.stream()
                                .mapToDouble(n -> n.path("main").path("temp_max").asDouble())
                                .max().orElse(0.0);

                        double minTemp = dayNodes.stream()
                                .mapToDouble(n -> n.path("main").path("temp_min").asDouble())
                                .min().orElse(0.0);

                        // Pick the weather slot closest to 12:00 PM for condition and humidity
                        JsonNode noonNode = dayNodes.stream()
                                .filter(n -> n.path("dt_txt").asText().contains("12:00:00"))
                                .findFirst()
                                .orElse(dayNodes.get(dayNodes.size() / 2));

                        int humidity = noonNode.path("main").path("humidity").asInt();
                        String condition = noonNode.path("weather").get(0).path("description").asText();
                        String icon = noonNode.path("weather").get(0).path("icon").asText();
                        String sunRise = root.path("city").path("sunrise").asText();
                        String sunSet = root.path("city").path("sunset").asText();

                        return new DailyWeatherDTO(date, maxTemp, minTemp, humidity, condition, icon, sunRise,
                                sunSet);
                    })
                    .limit(5)
                    .collect(Collectors.toList());

            return new ForecastResponseDTO(city, dailyForecasts);

        } catch (Exception e) {
            throw new RuntimeException("Failed to format 5-day forecast", e);
        }
    }

}
