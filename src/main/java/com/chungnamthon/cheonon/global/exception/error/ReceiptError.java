package com.chungnamthon.cheonon.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReceiptError implements BaseError {

    INVALID_CITY(HttpStatus.BAD_REQUEST, "천안 지역 영수증이 아닙니다."),
    INVALID_CARD_TYPE(HttpStatus.BAD_REQUEST, "영수증에 코나 카드 결제 내역이 없습니다."),
    MERCHANT_NOT_FOUND(HttpStatus.NOT_FOUND, "OCR 결과에서 가맹점을 찾을 수 없습니다."),
    USER_LOCATION_REQUIRED(HttpStatus.BAD_REQUEST, "현재 위치 정보가 필요합니다."),
    MERCHANT_LOCATION_MISSING(HttpStatus.BAD_REQUEST, "가맹점의 위치 정보가 등록되지 않았습니다."),
    TOO_FAR_FROM_MERCHANT(HttpStatus.BAD_REQUEST, "가맹점으로부터 너무 멀리 떨어져 있습니다."),
    INVALID_TIME_FORMAT(HttpStatus.BAD_REQUEST, "영수증에서 올바른 시간 형식을 찾을 수 없습니다."),
    TIME_NOT_FOUND(HttpStatus.BAD_REQUEST, "영수증에서 방문 시간을 찾을 수 없습니다."),
    INVALID_HOUR(HttpStatus.BAD_REQUEST, "유효하지 않은 시간입니다."),
    INVALID_MINUTE(HttpStatus.BAD_REQUEST, "유효하지 않은 분입니다."),
    INVALID_SECOND(HttpStatus.BAD_REQUEST, "유효하지 않은 초입니다."),
    PREVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 미리보기 영수증이 존재하지 않습니다."),
    RECEIPT_ALREADY_CONFIRMED(HttpStatus.CONFLICT, "이미 해당 영수증으로 인증이 완료되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
