package com.rdbackend.weather_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class WeatherApiException extends RuntimeException {
    private final HttpStatusCode status;

    public WeatherApiException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public WeatherApiException(String message, HttpStatusCode status) {
        super(message);
        this.status = status;
    }

    public WeatherApiException(String message, Throwable cause, HttpStatusCode status) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatusCode getStatus() {
        return status;
    }
}
