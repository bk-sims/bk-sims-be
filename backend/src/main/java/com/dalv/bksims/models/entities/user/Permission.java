package com.dalv.bksims.models.entities.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    LECTURER_READ("lecturer:read"),
    LECTURER_UPDATE("lecturer:update"),
    LECTURER_CREATE("lecturer:create"),
    LECTURER_DELETE("lecturer:delete"),
    STUDENT_READ("student:read"),
    STUDENT_UPDATE("student:update"),
    STUDENT_CREATE("student:create"),
    STUDENT_DELETE("student:delete");

    @Getter
    private final String permission;
}
