package com.tde.mescloud.security.service;

import com.tde.mescloud.security.model.token.TokenEntity;
import com.tde.mescloud.security.repository.TokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import static com.tde.mescloud.security.constant.SecurityConstant.COOKIE_TOKEN_NAME;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final JwtTokenService jwtTokenService;
    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Cookie[] cookies = request.getCookies();
        final String jwtToken;

        if(cookies == null || !jwtTokenService.isTokenInCookie(cookies)) {
            return;
        }

        jwtToken  = jwtTokenService.getJwtTokenFromCookie(cookies);
        removeCookie(request, response);
        TokenEntity storedToken = tokenRepository.findByToken(jwtToken)
                .orElse(null);
        if(storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }
    }

    public void removeCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_TOKEN_NAME)) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
    }
}
