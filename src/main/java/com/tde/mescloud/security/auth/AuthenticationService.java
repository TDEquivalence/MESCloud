package com.tde.mescloud.security.auth;

import com.tde.mescloud.security.config.JwtTokenService;
import com.tde.mescloud.security.model.User;
import com.tde.mescloud.security.repository.UserRepository;
import com.tde.mescloud.security.role.Role;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .isActive(true)
                .joinDate(new Date())
                .build();

        setUsernameByEmail(user);
        userRepository.save(user);
        var jwtToken = jwtTokenService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticateRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        //TODO: exception handling
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtTokenService.generateToken(user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public User setUsernameByEmail(User user) {
        if (user.getUsername() == null) {
            user.setUsername(user.getEmail());
        }
        return user;
    }

}
