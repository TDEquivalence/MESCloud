package com.tde.mescloud.security.service;

import com.tde.mescloud.security.model.token.TokenEntity;
import com.tde.mescloud.security.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import static com.tde.mescloud.security.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String jwtToken;

        if(authHeader == null ||!authHeader.startsWith(TOKEN_PREFIX)) {
            return;
        }

        jwtToken = authHeader.substring(TOKEN_PREFIX.length());
        TokenEntity storedToken = tokenRepository.findByToken(jwtToken)
                .orElse(null);
        if(storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }
    }
}
