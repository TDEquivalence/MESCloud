package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.UserDto;
import com.alcegory.mescloud.security.exception.UsernameExistException;
import com.alcegory.mescloud.security.model.auth.AuthenticateRequest;
import com.alcegory.mescloud.security.model.auth.AuthenticationResponse;
import com.alcegory.mescloud.security.model.auth.RegisterRequest;
import com.alcegory.mescloud.security.service.AuthenticationService;
import com.alcegory.mescloud.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) throws UsernameExistException {
        AuthenticationResponse userRegisteredResponse = authenticationService.register(request);
        return new ResponseEntity<>(userRegisteredResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody AuthenticateRequest request, HttpServletResponse response) {
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
        authenticationService.setJwtTokenCookie(authenticationResponse, response);

        UserDto userDto = userService.getUserByAuth(authenticationResponse);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}