package com.ipin.identity.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    USER_NOT_FOUND(1001, "User not found"),
    USER_EXISTED(1002, "User existed"),
    INVALID_USERNAME(1003, "Username must be at least {min} characters"),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters"),
    INVALID_KEY(1005, "Uncategorized error"),
    USER_NOT_EXISTED(1006, "User not existed"),
    UNAUTHENTICATED(1007, "Unauthenticated"),
    INVALID_DOB(1008, "Your age must be at least {min}");

    int code;
    String message;
}
