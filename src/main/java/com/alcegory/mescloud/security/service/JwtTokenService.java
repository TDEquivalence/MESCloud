package com.alcegory.mescloud.security.service;

import com.alcegory.mescloud.security.constant.SecurityConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.alcegory.mescloud.security.constant.SecurityConstant.JWT_EXPIRATION;
import static com.alcegory.mescloud.security.constant.SecurityConstant.REFRESH_JWT_EXPIRATION;

@Service
public class JwtTokenService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, JWT_EXPIRATION);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, REFRESH_JWT_EXPIRATION);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long tokenExpiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts
                .parserBuilder()
                .setAllowedClockSkewSeconds(60)
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getJwtTokenFromCookie(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(SecurityConstant.COOKIE_TOKEN_NAME)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public String getRefreshJwtTokenFromCookie(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(SecurityConstant.COOKIE_REFRESH_TOKEN_NAME)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public boolean isTokenInCookie(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(SecurityConstant.COOKIE_TOKEN_NAME)) {
                    return true;
                }
            }
        }
        return false;
    }
}
