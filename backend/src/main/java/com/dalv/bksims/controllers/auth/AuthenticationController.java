package com.dalv.bksims.controllers.auth;

import com.dalv.bksims.exceptions.AuthException;
import com.dalv.bksims.models.dtos.auth.AuthenticationRequest;
import com.dalv.bksims.models.dtos.auth.AuthenticationResponse;
import com.dalv.bksims.models.dtos.auth.RegisterRequest;
import com.dalv.bksims.models.entities.user.User;
import com.dalv.bksims.services.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) throws AuthException {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping("/validate-token")
    public ResponseEntity<User> validateToken(
            HttpServletRequest request
    ) throws IOException {
        return ResponseEntity.ok(authenticationService.validateToken(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }
}
