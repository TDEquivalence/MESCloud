package com.alcegory.mescloud.security.config;

import com.alcegory.mescloud.model.entity.UserEntity;
import com.alcegory.mescloud.repository.UserRepository;
import com.alcegory.mescloud.security.model.token.TokenEntity;
import com.alcegory.mescloud.security.model.token.TokenType;
import com.alcegory.mescloud.security.repository.TokenRepository;
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
import java.util.Optional;

import static com.alcegory.mescloud.security.constant.SecurityConstant.JWT_EXPIRATION;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String COOKIE_TOKEN_NAME = "jwtToken";
    private static final String COOKIE_REFRESH_TOKEN_NAME = "refreshJwtToken";
    private static final String INVALID_TOKEN_MESSAGE = "Invalid token";

    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

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

            String username;
            if (jwtToken != null) {
                username = jwtTokenService.extractUsername(jwtToken);
            } else {
                username = jwtTokenService.extractUsername(refreshToken);
            }


            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = loadUserByUsername(username);
                if (isTokenValid(jwtToken, userDetails)) {
                    authenticateUser(request, userDetails);
                }

                if (jwtTokenService.isTokenExpired(jwtToken)) {
                    refreshToken(request, response);
                }

                filterChain.doFilter(request, response);
            }
        } catch (JwtException ex) {
            handleInvalidToken(response);
        }
    }

    private UserDetails loadUserByUsername(String username) {
        return userDetailsService.loadUserByUsername(username);
    }

    private boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        Optional<TokenEntity> optionalToken = tokenRepository.findByToken(jwtToken);
        if (optionalToken.isPresent()) {
            TokenEntity token = optionalToken.get();
            if (!token.isExpired() && !token.isRevoked()) {
                return jwtTokenService.isTokenValid(jwtToken, userDetails);
            }
        }
        return false;
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

    private void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || !jwtTokenService.isTokenInCookie(cookies)) {
            return;
        }

        String refreshJwtToken = jwtTokenService.getJwtTokenFromCookie(cookies, COOKIE_REFRESH_TOKEN_NAME);
        String username = jwtTokenService.extractUsername(refreshJwtToken);

        if (username != null) {
            UserDetails userDetails = loadUserByUsername(username);
            if (jwtTokenService.isTokenValid(refreshJwtToken, userDetails)) {
                String accessToken = jwtTokenService.generateToken(userDetails);
                addJwtTokenToCookie(response, accessToken);
                UserEntity user = userRepository.findUserByUsername(userDetails.getUsername());
                tokenRepository.deleteAllByUserId(user.getId());
                saveUserToken(accessToken, userDetails);
            }
        }
    }

    private void saveUserToken(String jwtToken, UserDetails userDetails) {
        UserEntity user = userRepository.findUserByUsername(userDetails.getUsername());
        TokenEntity token = TokenEntity.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void handleInvalidToken(HttpServletResponse response) throws IOException {
        cleanCookies(response);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_TOKEN_MESSAGE);
    }

    private void cleanCookies(HttpServletResponse response) {
        removeCookie(response, COOKIE_TOKEN_NAME);
        removeCookie(response, COOKIE_REFRESH_TOKEN_NAME);
    }

    private void removeCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
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
