package com.alcegory.mescloud.security.service;

import com.alcegory.mescloud.security.exception.UsernameExistException;
import com.alcegory.mescloud.security.model.auth.AuthenticateRequest;
import com.alcegory.mescloud.security.model.auth.AuthenticationResponse;
import com.alcegory.mescloud.security.model.auth.RegisterRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import javax.management.relation.RoleNotFoundException;

public interface AuthenticationService {

    AuthenticationResponse register(RegisterRequest request, Authentication authentication) throws UsernameExistException,
            RoleNotFoundException;

    AuthenticationResponse authenticate(AuthenticateRequest request);

    void setJwtTokenCookie(AuthenticationResponse authenticationResponse, HttpServletResponse response);
}
