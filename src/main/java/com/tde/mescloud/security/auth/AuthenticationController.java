package com.tde.mescloud.security.auth;

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
    public ResponseEntity<RegisterRequest> register(@RequestBody RegisterRequest request) throws UsernameExistException {
        RegisterRequest registerRequest = authenticationService.register(request);
        return new ResponseEntity<>(registerRequest, HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticateRequest request) {
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
        HttpHeaders headers = authenticationResponse.getHeaders();
        return new ResponseEntity<>(authenticationResponse, headers, HttpStatus.OK);
    }
}
