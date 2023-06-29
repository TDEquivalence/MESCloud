package com.tde.mescloud.security.constant;

public class SecurityConstant {

    private SecurityConstant() {
    }

    public static final long EXPIRATION_TIME = 432_000_000;
    public static final String TOKEN_PREFIX = "Bearer";
    public static final String JWT_TOKEN_HEADER = "jwt-token"; //creating custom header
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";

}
