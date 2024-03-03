package com.dalv.bksims.exceptions;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private final int status;
    private final String message;

    public AuthException(int status, String message) {
        this.status = status;
        this.message = message;
    }

}
