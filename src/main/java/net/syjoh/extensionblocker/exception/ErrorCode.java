package net.syjoh.extensionblocker.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "E001", "1자 이상 입력해주세요"),
    MAX_INPUT_REACHED(HttpStatus.BAD_REQUEST, "E002", "20자 이내로 입력해주세요"),
    EXTENSION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "E003", "이미 있는 확장자입니다."),
    MAX_CUSTOM_EXTENSIONS_REACHED(HttpStatus.BAD_REQUEST, "E004", "커스텀 확장자는 200개까지 가능합니다."),
    EXTENSION_NOT_FOUND(HttpStatus.NOT_FOUND, "E005", "해당 확장자를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E999", "Internal server error");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
