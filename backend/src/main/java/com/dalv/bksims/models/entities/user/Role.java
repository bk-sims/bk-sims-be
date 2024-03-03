package com.dalv.bksims.models.entities.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dalv.bksims.models.entities.user.Permission.ADMIN_CREATE;
import static com.dalv.bksims.models.entities.user.Permission.ADMIN_DELETE;
import static com.dalv.bksims.models.entities.user.Permission.ADMIN_READ;
import static com.dalv.bksims.models.entities.user.Permission.ADMIN_UPDATE;
import static com.dalv.bksims.models.entities.user.Permission.LECTURER_CREATE;
import static com.dalv.bksims.models.entities.user.Permission.LECTURER_DELETE;
import static com.dalv.bksims.models.entities.user.Permission.LECTURER_READ;
import static com.dalv.bksims.models.entities.user.Permission.LECTURER_UPDATE;
import static com.dalv.bksims.models.entities.user.Permission.STUDENT_CREATE;
import static com.dalv.bksims.models.entities.user.Permission.STUDENT_DELETE;
import static com.dalv.bksims.models.entities.user.Permission.STUDENT_READ;
import static com.dalv.bksims.models.entities.user.Permission.STUDENT_UPDATE;

@RequiredArgsConstructor
public enum Role {

    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE,
                    LECTURER_READ,
                    LECTURER_UPDATE,
                    LECTURER_DELETE,
                    LECTURER_CREATE
            )
    ),
    LECTURER(
            Set.of(
                    LECTURER_READ,
                    LECTURER_UPDATE,
                    LECTURER_DELETE,
                    LECTURER_CREATE
            )
    ),

    STUDENT(
            Set.of(
                    STUDENT_READ,
                    STUDENT_UPDATE,
                    STUDENT_DELETE,
                    STUDENT_CREATE
            )
    );

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
