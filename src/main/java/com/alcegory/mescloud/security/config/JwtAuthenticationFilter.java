package com.alcegory.mescloud.security.config;

import com.alcegory.mescloud.security.repository.TokenRepository;
import com.alcegory.mescloud.security.service.JwtTokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.alcegory.mescloud.security.constant.SecurityConstant.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            Cookie[] cookies = request.getCookies();
            final String jwtToken;
            final String username;

            if (cookies == null || !jwtTokenService.isTokenInCookie(cookies)) {
                filterChain.doFilter(request, response);
                return;
            }

            jwtToken = jwtTokenService.getJwtTokenFromCookie(cookies);
            username = jwtTokenService.extractUsername(jwtToken);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                boolean isTokenValid = tokenRepository.findByToken(jwtToken)
                        .map(token -> !token.isExpired() && !token.isRevoked())
                        .orElse(false);
                if (jwtTokenService.isTokenValid(jwtToken, userDetails) && isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

                if (jwtTokenService.isTokenExpired(jwtToken)) {
                    refreshToken(request, response);
                }

                filterChain.doFilter(request, response);
            }
        } catch (JwtException ex) {
            cleanCookies(response);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        }
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        final String refreshJwtToken;
        final String username;

        if (cookies == null || !jwtTokenService.isTokenInCookie(cookies)) {
            return;
        }

        refreshJwtToken = jwtTokenService.getRefreshJwtTokenFromCookie(cookies);
        username = jwtTokenService.extractUsername(refreshJwtToken);

        if (username != null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtTokenService.isTokenValid(refreshJwtToken, userDetails)) {
                String accessToken = jwtTokenService.generateToken(userDetails);
                Cookie cookie = new Cookie(COOKIE_TOKEN_NAME, accessToken);
                cookie.setMaxAge(JWT_EXPIRATION);
                cookie.setPath("/");
                cookie.setSecure(true);
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
            }
        }
    }

    public void cleanCookies(HttpServletResponse response) {
        Cookie jwtTokenCookie = new Cookie(COOKIE_TOKEN_NAME, null);
        jwtTokenCookie.setMaxAge(0);
        jwtTokenCookie.setPath("/");
        response.addCookie(jwtTokenCookie);

        Cookie refreshTokenCookie = new Cookie(COOKIE_REFRESH_TOKEN_NAME, null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);
    }
}
