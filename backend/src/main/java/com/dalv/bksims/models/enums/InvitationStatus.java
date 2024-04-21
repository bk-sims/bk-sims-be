package com.dalv.bksims.models.enums;

import lombok.Getter;

@Getter
public enum InvitationStatus {
    ACCEPTED("ACCEPTED"),
    PENDING("PENDING"),
    REJECTED("REJECTED");

    private final String value;

    InvitationStatus(String value) {
        this.value = value;
    }
}
