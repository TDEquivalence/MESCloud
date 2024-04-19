package com.alcegory.mescloud.security.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import static com.alcegory.mescloud.security.constant.SecurityConstant.COOKIE_REFRESH_TOKEN_NAME;
import static com.alcegory.mescloud.security.constant.SecurityConstant.COOKIE_TOKEN_NAME;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final JwtTokenService jwtTokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null || !jwtTokenService.isTokenInCookie(cookies)) {
            return;
        }

        jwtTokenService.removeCookie(response, COOKIE_TOKEN_NAME);
        jwtTokenService.removeCookie(response, COOKIE_REFRESH_TOKEN_NAME);
        SecurityContextHolder.clearContext();
    }
}
