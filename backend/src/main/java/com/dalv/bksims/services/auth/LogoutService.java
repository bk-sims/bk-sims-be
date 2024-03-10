package com.dalv.bksims.services.auth;

import com.dalv.bksims.models.entities.auth.Token;
import com.dalv.bksims.models.repositories.auth.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenRepository tokenRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String token;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException();
        }

        token = authHeader.substring(7);

        Token storedToken = tokenRepository.findByToken(token)
                .orElse(null);

        if (storedToken == null) {
            throw new RuntimeException();
        }

        storedToken.setExpired(true);
        storedToken.setRevoked(true);
        tokenRepository.save(storedToken);
        SecurityContextHolder.clearContext();
    }
}
