package com.tde.mescloud.security.service;

import com.tde.mescloud.model.entity.UserEntity;
import com.tde.mescloud.security.config.JwtTokenService;
import com.tde.mescloud.security.exception.UsernameExistException;
import com.tde.mescloud.security.mapper.EntityDtoMapper;
import com.tde.mescloud.security.model.auth.AuthenticateRequest;
import com.tde.mescloud.security.model.auth.AuthenticationResponse;
import com.tde.mescloud.security.model.auth.RegisterRequest;
import com.tde.mescloud.security.repository.UserRepository;
import com.tde.mescloud.security.role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.tde.mescloud.security.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static com.tde.mescloud.security.constant.UserServiceImpConstant.USERNAME_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final EntityDtoMapper mapper;

    public AuthenticationResponse register(RegisterRequest request) throws UsernameExistException {
        setUsernameByEmail(request);
        validateUsername(request);
        var user = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(getRoleEnumName(request.getRole()))
                .isActive(true)
                .isNotLocked(true)
                .createdAt(new Date())
                .build();

        userRepository.save(user);
        return userToAuthenticationResponse(user);
    }

    public AuthenticationResponse authenticate(AuthenticateRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserEntity userEntity = userRepository.findUserByUsername(request.getUsername());
        return userToAuthenticationResponse(userEntity);
    }

    private void setUsernameByEmail(RegisterRequest request) {
        if (request.getUsername().isBlank()) {
            request.setUsername(request.getEmail());
        }
    }

    private void validateUsername(RegisterRequest request) throws UsernameExistException {
        UserEntity userEntity = userRepository.findUserByUsername(request.getUsername());
        if(userEntity != null) {
            throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
        }
    }

    public HttpHeaders getJwtHeader(AuthenticationResponse authenticationResponse) {
        UserEntity userEntity = userRepository.findUserByUsername(authenticationResponse.getUsername());
        String jwtToken = jwtTokenService.generateToken(userEntity);
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtToken);

        return headers;
    }

    private AuthenticationResponse userToAuthenticationResponse(UserEntity userEntity) {
        return AuthenticationResponse.builder()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .username(userEntity.getUsername())
                .build();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
