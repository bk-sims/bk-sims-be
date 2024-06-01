package com.dalv.bksims.models.enums;

import lombok.Getter;

@Getter
public enum CourseProposalStatus {
    APPROVED("APPROVED"),
    PENDING("PENDING"),
    REJECTED("REJECTED");

    private final String value;

    CourseProposalStatus(String value) {
        this.value = value;
    }
}
