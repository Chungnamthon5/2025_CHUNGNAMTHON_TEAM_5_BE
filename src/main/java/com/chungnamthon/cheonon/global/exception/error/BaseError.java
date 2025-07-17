package com.chungnamthon.cheonon.global.exception.error;

import org.springframework.http.HttpStatus;

public interface BaseError {

    HttpStatus getHttpStatus();

    String getMessage();
}
