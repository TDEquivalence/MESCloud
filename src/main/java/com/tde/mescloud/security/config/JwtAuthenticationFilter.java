package com.tde.mescloud.security.config;

import com.tde.mescloud.security.repository.TokenRepository;
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
import java.util.Arrays;
import java.util.Optional;

import static com.tde.mescloud.security.constant.SecurityConstant.TOKEN_PREFIX;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String jwtToken = getJwtTokenFromCookie(request);
        final String username;

        if(jwtToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        username = jwtTokenService.extractUsername(jwtToken);

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            boolean isTokenValid = tokenRepository.findByToken(jwtToken)
                    .map(token -> !token.isExpired() && !token.isRevoked())
                    .orElse(false);
            if(jwtTokenService.isTokenValid(jwtToken, userDetails) && isTokenValid) {
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
            filterChain.doFilter(request, response);
        }
    }

    public String getJwtTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> jwtTokenCookie = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("jwtToken"))
                    .findFirst();
            if (jwtTokenCookie.isPresent()) {
                String cookieValue = jwtTokenCookie.get().getValue();
                return extractJwtTokenValue(cookieValue);
            }
        }
        return null;
    }

    public String extractJwtTokenValue(String jwtTokenCookie) {
        String[] cookieParts = jwtTokenCookie.split(";");

        for (String cookiePart : cookieParts) {
            String trimmedCookiePart = cookiePart.trim();
            if (trimmedCookiePart.startsWith("jwtToken=")) {
                return trimmedCookiePart.substring("jwtToken=".length());
            }
        }
        return null;
    }
}
