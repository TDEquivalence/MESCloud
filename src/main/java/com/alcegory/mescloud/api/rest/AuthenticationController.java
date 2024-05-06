package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.api.rest.response.ErrorResponse;
import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.security.constant.HttpResponseConstant;
import com.alcegory.mescloud.security.exception.UsernameExistException;
import com.alcegory.mescloud.security.model.auth.AuthenticateRequest;
import com.alcegory.mescloud.security.model.auth.AuthenticationResponse;
import com.alcegory.mescloud.security.model.auth.RegisterRequest;
import com.alcegory.mescloud.security.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request, Authentication authentication) {
        try {
            AuthenticationResponse userRegisteredResponse = authenticationService.register(request, authentication);
            return new ResponseEntity<>(userRegisteredResponse, HttpStatus.CREATED);
        } catch (RoleNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse(HttpResponseConstant.NOT_ENOUGH_PERMISSION), HttpStatus.NOT_FOUND);
        } catch (UsernameExistException e) {
            return new ResponseEntity<>(new ErrorResponse(HttpResponseConstant.USERNAME_ALREADY_EXISTS), HttpStatus.CONFLICT);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(new ErrorResponse(HttpResponseConstant.ACCOUNT_DISABLED), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserConfigDto> login(@RequestBody AuthenticateRequest request, HttpServletResponse response) {
        try {
            UserConfigDto authenticationResponse = authenticationService.authenticate(request, response);
            return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}