package com.alcegory.mescloud.security.config;

import com.alcegory.mescloud.security.service.JwtTokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.alcegory.mescloud.security.constant.SecurityConstant.JWT_EXPIRATION;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String COOKIE_TOKEN_NAME = "jwtToken";
    private static final String COOKIE_REFRESH_TOKEN_NAME = "refreshJwtToken";
    private static final String INVALID_TOKEN_MESSAGE = "Invalid token";

    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies == null || !jwtTokenService.isTokenInCookie(cookies)) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwtToken = jwtTokenService.getJwtTokenFromCookie(cookies, COOKIE_TOKEN_NAME);
            String refreshToken = jwtTokenService.getJwtTokenFromCookie(cookies, COOKIE_REFRESH_TOKEN_NAME);
            String tokenToCheck = jwtToken != null ? jwtToken : refreshToken;

            if (tokenToCheck != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                processToken(tokenToCheck, request, response);
            }

            filterChain.doFilter(request, response);

        } catch (JwtException ex) {
            handleInvalidToken(response);
        }
    }

    private void processToken(String tokenToCheck, HttpServletRequest request, HttpServletResponse response) {
        String username = jwtTokenService.extractUsername(tokenToCheck);
        if (username != null) {
            UserDetails userDetails = loadUserByUsername(username);
            if (jwtTokenService.isTokenValid(tokenToCheck, userDetails)) {
                authenticateUser(request, userDetails);
                if (jwtTokenService.isTokenRefreshable(tokenToCheck)) {
                    refreshToken(request, response, userDetails);
                }
            }
        }
    }

    private UserDetails loadUserByUsername(String username) {
        return userDetailsService.loadUserByUsername(username);
    }

    private void authenticateUser(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void refreshToken(HttpServletRequest request, HttpServletResponse response, UserDetails userDetails) {
        String accessToken = jwtTokenService.generateToken(userDetails);
        addJwtTokenToCookie(response, accessToken);
    }

    private void handleInvalidToken(HttpServletResponse response) throws IOException {
        cleanCookies(response);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_TOKEN_MESSAGE);
    }

    private void cleanCookies(HttpServletResponse response) {
        jwtTokenService.removeCookie(response, COOKIE_TOKEN_NAME);
        jwtTokenService.removeCookie(response, COOKIE_REFRESH_TOKEN_NAME);
    }

    private void addJwtTokenToCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie(COOKIE_TOKEN_NAME, accessToken);
        cookie.setMaxAge(JWT_EXPIRATION);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}

