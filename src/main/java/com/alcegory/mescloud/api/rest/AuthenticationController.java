package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.security.exception.UsernameExistException;
import com.alcegory.mescloud.security.model.auth.AuthenticateRequest;
import com.alcegory.mescloud.security.model.auth.AuthenticationResponse;
import com.alcegory.mescloud.security.model.auth.RegisterRequest;
import com.alcegory.mescloud.security.service.AuthenticationService;
import com.alcegory.mescloud.security.service.UserRoleService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.relation.RoleNotFoundException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserRoleService userRoleService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request, Authentication authentication) {
        try {
            AuthenticationResponse userRegisteredResponse = authenticationService.register(request, authentication);
            return new ResponseEntity<>(userRegisteredResponse, HttpStatus.CREATED);
        } catch (RoleNotFoundException | UsernameExistException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserConfigDto> login(@RequestBody AuthenticateRequest request, HttpServletResponse response) {
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
        authenticationService.setJwtTokenCookie(authenticationResponse, response);

        UserConfigDto userDto = userRoleService.getUserRoleAndConfigurations(authenticationResponse);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}