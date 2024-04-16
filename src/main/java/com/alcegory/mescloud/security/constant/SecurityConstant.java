package com.alcegory.mescloud.security.constant;

public class SecurityConstant {

    public static final String COOKIE_TOKEN_NAME = "jwtToken";
    public static final String COOKIE_REFRESH_TOKEN_NAME = "refreshJwtToken";
    public static final int JWT_EXPIRATION = 86400 * 1000; // 1 day in milliseconds
    public static final int REFRESH_JWT_EXPIRATION = 604800 * 1000; // 7 days in milliseconds
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String FORBIDDEN_MESSAGE = "You need to login to access this page";
    public static final int ALLOWED_CLOCK_SKEW = 120;

    private SecurityConstant() {
    }

}
