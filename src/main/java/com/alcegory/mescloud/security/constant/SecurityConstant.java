package com.alcegory.mescloud.security.constant;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class SecurityConstant {

    public static final String COOKIE_TOKEN_NAME = "jwtToken";
    public static final String COOKIE_REFRESH_TOKEN_NAME = "refreshJwtToken";
    public static final int JWT_EXPIRATION = 86400;
    public static final int REFRESH_JWT_EXPIRATION = 604800;
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String FORBIDDEN_MESSAGE = "You need to login to access this page";
    public static final int ALLOWED_CLOCK_SKEW = 120;
    public static final String PATH = "/";
    private static final int EXPIRATION_PERIOD_HOURS = 24;

    private SecurityConstant() {
    }

    public static Date getExpirationDate() {
        LocalDateTime expirationDateTime = LocalDateTime.now().plusHours(EXPIRATION_PERIOD_HOURS);
        return Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


}
