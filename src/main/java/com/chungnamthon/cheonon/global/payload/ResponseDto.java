package com.chungnamthon.cheonon.global.payload;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ResponseDto<T> {

    private final LocalDateTime timeStamp;
    private final String message;
    private final T data;

    protected ResponseDto(LocalDateTime timeStamp, String message, T data) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseDto<T> of(String message) {
        return new ResponseDto<>(LocalDateTime.now(), message, null);
    }

    public static <T> ResponseDto<T> of(T body) {
        return new ResponseDto<>(LocalDateTime.now(), null, body);
    }

    public static <T> ResponseDto<T> of(T body, String message) {
        return new ResponseDto<>(LocalDateTime.now(), message, body);
    }
}
