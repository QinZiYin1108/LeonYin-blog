package com.example.backend.common;

public enum ErrorCode {
    SUCCESS(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    SERVER_ERROR(500, "Internal Server Error"),
    VALIDATION_ERROR(1001, "Validation Error"),
    BUSINESS_ERROR(1002, "Business Error"),
    RATE_LIMITED(1003, "Rate Limited"),
    SQL_ERROR(1004, "SQL Error"),
    JSON_ERROR(1005, "JSON Parse Error");

    private final int code;
    private final String defaultMessage;

    ErrorCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public int code() { return code; }
    public String msg() { return defaultMessage; }
}






