package com.tde.mescloud.api.rest;

import com.tde.mescloud.security.model.auth.AuthenticateRequest;
import com.tde.mescloud.security.model.auth.AuthenticationResponse;
import com.tde.mescloud.security.service.AuthenticationService;
import com.tde.mescloud.security.model.auth.RegisterRequest;
import com.tde.mescloud.security.exception.UsernameExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) throws UsernameExistException {
        AuthenticationResponse userRegisteredResponse = authenticationService.register(request);
        return new ResponseEntity<>(userRegisteredResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticateRequest request) {
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
        HttpHeaders headers = authenticationService.getJwtHeader(authenticationResponse);
        return new ResponseEntity<>(authenticationResponse, headers, HttpStatus.OK);
    }
}
