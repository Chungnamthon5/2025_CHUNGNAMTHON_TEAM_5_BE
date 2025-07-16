package com.chungnamthon.cheonon.global.exception;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponse(HttpStatus httpStatus, String message, LocalDateTime timeStamp) {

    @Builder
    public ErrorResponse {
    }

    public static ErrorResponse of(HttpStatus httpStatus, String message) {
        return ErrorResponse.builder()
                .httpStatus(httpStatus)
                .message(message)
                .timeStamp(LocalDateTime.now())
                .build();
    }
}
