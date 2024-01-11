package com.dalv.bksims.models.enums;

import lombok.Getter;

@Getter
public enum Status {
    OPEN("OPEN"),
    PENDING("PENDING"),
    REJECTED("REJECTED"),
    CLOSED("CLOSED");

    private final String value;

    Status(String value) {
        this.value = value;
    }

}
