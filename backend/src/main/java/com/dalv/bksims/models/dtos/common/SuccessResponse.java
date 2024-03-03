package com.dalv.bksims.models.dtos.common;

import lombok.Getter;

@Getter
public class SuccessResponse {
    private final int status;
    private final String message;

    public SuccessResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
