package com.chungnamthon.cheonon.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MeetingError implements BaseError {

    MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 모임입니다."),
    INVALID_TITLE(HttpStatus.BAD_REQUEST, "제목은 공백일 수 없습니다."),
    INVALID_DESCRIPTION(HttpStatus.BAD_REQUEST, "설명은 공백일 수 없습니다."),
    INVALID_OPENCHATURL(HttpStatus.BAD_REQUEST, "올바르지 않은 카카오 오픈채팅방 링크 형식입니다."),
    FORBIDDEN_MEETING_UPDATE(HttpStatus.FORBIDDEN, "본인의 모임 게시글만 수정할 수 있습니다."),
    FORBIDDEN_MEETING_DELETE(HttpStatus.FORBIDDEN, "본인의 모임 게시글만 삭제할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
