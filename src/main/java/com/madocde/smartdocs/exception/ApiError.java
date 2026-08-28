package com.madocde.smartdocs.exception;

import java.time.OffsetDateTime;

public class ApiError {
    private OffsetDateTime time;
    private int status;
    private String error;
    private String message;

    public ApiError(OffsetDateTime time, int status, String error, String message) {
        this.time = time;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
