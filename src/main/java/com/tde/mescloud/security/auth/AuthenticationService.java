package com.tde.mescloud.security.auth;

import com.tde.mescloud.security.config.JwtTokenService;
import com.tde.mescloud.security.exception.UsernameExistException;
import com.tde.mescloud.security.model.User;
import com.tde.mescloud.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.tde.mescloud.security.constants.SecurityConstant.JWT_TOKEN_HEADER;
import static com.tde.mescloud.security.constants.UserServiceImpConstant.USERNAME_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    public RegisterRequest register(RegisterRequest request) throws UsernameExistException {
        setUsernameByEmail(request);
        validateUsername(request);
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isActive(true)
                .joinDate(new Date())
                .build();

        userRepository.save(user);
        var jwtToken = jwtTokenService.generateToken(user);
        return request;
    }

    public AuthenticationResponse authenticate(AuthenticateRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        //TODO: exception handling
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtTokenService.generateToken(user);
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtToken);

        return AuthenticationResponse.builder().token(jwtToken).headers(headers).build();
    }

    private RegisterRequest setUsernameByEmail(RegisterRequest request) {
        if (request.getUsername().isBlank()) {
            request.setUsername(request.getEmail());
        }
        return request;
    }

    private RegisterRequest validateUsername(RegisterRequest request) throws UsernameExistException {
        User user = userRepository.findUserByUsername(request.getUsername());
        if(user != null) {
            throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
        }
        return request;
    }

}
