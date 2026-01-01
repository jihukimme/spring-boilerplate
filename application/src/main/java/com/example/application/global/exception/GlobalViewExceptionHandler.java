package com.example.application.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class GlobalViewExceptionHandler {

    // ViewController에서 발생하는 예외를 전역적으로 처리해 반환 (ViewController에서 예외를 던져야 함)
    @ExceptionHandler(CustomException.class)
    public String handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        HttpStatus status = errorCode.getHttpStatus();

        log.warn("View Controller 예외 발생: {} (Status: {})", errorCode.getMessage(), status);

        // 1. 404 Not Found 관련
        if (status == HttpStatus.NOT_FOUND) {
            return "error/404";
        }

        // 2. 400 Bad Request 및 기타 4xx 관련
        if (status.is4xxClientError()) {
            return "error/400";
        }

        // 3. 그 외 (5xx 등)
        return "error/500";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        log.error("View Controller 시스템 오류", e);

        return "error/500";
    }
}