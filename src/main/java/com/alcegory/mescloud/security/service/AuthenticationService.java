package com.alcegory.mescloud.security.service;

import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.model.entity.UserEntity;
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

    UserConfigDto authenticate(AuthenticateRequest request, HttpServletResponse response);

    String setJwtTokenCookie(UserEntity user, HttpServletResponse response);

    void handleUserTokens(UserEntity user, String jwtToken);
}
