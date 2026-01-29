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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("error", "WEATHER_API_ERROR");
        // body.put("message", ex.getMessage());
        String msg = ex.getMessage();
        if (msg != null && msg.contains("{")) {
            Matcher matcher = Pattern.compile("\"cod\":\"(\\d+)\",\"message\":\"(.*?)\"").matcher(msg);
            if (matcher.find()) {
                body.put("error_code", matcher.group(1));
                body.put("error_message", matcher.group(2));
            }
        }
        body.put("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }
}
