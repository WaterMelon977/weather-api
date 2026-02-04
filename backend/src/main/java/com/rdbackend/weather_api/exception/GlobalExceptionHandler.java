package com.rdbackend.weather_api.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(WeatherApiException.class)
    public ResponseEntity<Map<String, Object>> handleWeatherApiException(WeatherApiException ex) {
        log.error("Weather API Exception: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Weather API Error");
        body.put("message", ex.getMessage());
        body.put("timestamp", Instant.now());
        body.put("status", ex.getStatus().value());

        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        log.error("Unhandled runtime exception", ex);

        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getClass().getSimpleName());
        body.put("message", ex.getMessage());
        String msg = ex.getMessage();
        if (msg != null && msg.contains("{")) {
            Matcher matcher = Pattern.compile("\"cod\":\"(\\d+)\",\"message\":\"(.*?)\"").matcher(msg);
            if (matcher.find()) {
                body.put("error_code", matcher.group(1));
                body.put("error_message", matcher.group(2));
            }
        }
        body.put("timestamp", Instant.now());
        body.put("StatusCode", HttpStatus.BAD_GATEWAY.value());

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }
}
