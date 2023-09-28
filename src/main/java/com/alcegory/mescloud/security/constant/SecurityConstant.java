package com.alcegory.mescloud.security.constant;

public class SecurityConstant {

    private SecurityConstant() {
    }

    public static final long EXPIRATION_TIME = 432_000_000;
    public static final String COOKIE_TOKEN_NAME = "jwtToken";
    public static final String TOKEN_PREFIX = "Bearer";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String FORBIDDEN_MESSAGE = "You need to login to access this page";

}
