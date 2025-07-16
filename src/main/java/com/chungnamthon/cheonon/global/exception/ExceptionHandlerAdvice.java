package com.chungnamthon.cheonon.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ErrorResponse> businessExceptionHandler(
            BusinessException exception, HttpServletRequest request
    ) {
        log.error("Business Error: ", exception);
        ErrorResponse errorResponse = ErrorResponse.of(
                exception.getHttpStatus(),
                exception.getMessage()
        );
        return ResponseEntity.status(exception.getHttpStatus()).body(errorResponse);
    }
}
