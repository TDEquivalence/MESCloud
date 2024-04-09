package com.alcegory.mescloud.security.service;

import com.alcegory.mescloud.model.entity.CompanyEntity;
import com.alcegory.mescloud.repository.UserRepository;
import com.alcegory.mescloud.security.constant.UserServiceImpConstant;
import com.alcegory.mescloud.security.exception.UsernameExistException;
import com.alcegory.mescloud.security.model.SectionRole;
import com.alcegory.mescloud.security.model.SectionRoleEntity;
import com.alcegory.mescloud.security.model.auth.AuthenticateRequest;
import com.alcegory.mescloud.security.model.auth.AuthenticationResponse;
import com.alcegory.mescloud.security.model.auth.RegisterRequest;
import com.alcegory.mescloud.security.model.token.TokenEntity;
import com.alcegory.mescloud.security.model.token.TokenType;
import com.alcegory.mescloud.security.repository.TokenRepository;
import com.alcegory.mescloud.model.entity.UserEntity;
import com.alcegory.mescloud.security.model.Role;
import com.alcegory.mescloud.service.CompanyService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final CompanyService companyService;
    private final UserRoleService userRoleService;
    private final RoleService roleService;

    public AuthenticationResponse register(RegisterRequest request) throws UsernameExistException, RoleNotFoundException {
        setUsernameByEmail(request);
        validateUsername(request);
        UserEntity user = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .company(getCompany())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(getRoleEnumName(request.getRole()))
                .isActive(true)
                .isNotLocked(true)
                .createdAt(new Date())
                .build();

        userRepository.save(user);
        saveSectionRole(request, user);
        return userToAuthenticationResponse(user);
    }

    public void saveSectionRole(RegisterRequest request, UserEntity user) throws RoleNotFoundException {
        Optional<SectionRoleEntity> sectionRoleOptional =
                roleService.findByName(getSectionRoleEnumName(request.getSectionRole()));

        if (sectionRoleOptional.isEmpty()) {
            throw new RoleNotFoundException("Role not found: " + request.getSectionRole());
        }

        SectionRoleEntity sectionRole = sectionRoleOptional.get();
        userRoleService.saveUserRole(user.getId(), sectionRole.getId(), 1L);
    }

    public AuthenticationResponse authenticate(AuthenticateRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserEntity user = userRepository.findUserByUsername(request.getUsername());
        String jwtToken = jwtTokenService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(jwtToken, user);
        removeLastInvalidUserTokens(user);
        return userToAuthenticationResponse(user);
    }

    private void setUsernameByEmail(RegisterRequest request) {
        if (request.getUsername().isBlank()) {
            request.setUsername(request.getEmail());
        }
    }

    private void validateUsername(RegisterRequest request) throws UsernameExistException {
        UserEntity userEntity = userRepository.findUserByUsername(request.getUsername());
        if (userEntity != null) {
            throw new UsernameExistException(UserServiceImpConstant.USERNAME_ALREADY_EXISTS);
        }
    }

    public void setJwtTokenCookie(AuthenticationResponse authenticationResponse, HttpServletResponse response) {
        UserEntity user = userRepository.findUserByUsername(authenticationResponse.getUsername());
        String jwtToken = jwtTokenService.generateToken(user);
        Cookie cookie = new Cookie("jwtToken", jwtToken);
        int cookieMaxAgeInSeconds = 86400; // 86400 seconds = 1 day
        cookie.setMaxAge(cookieMaxAgeInSeconds);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    private AuthenticationResponse userToAuthenticationResponse(UserEntity userEntity) {
        return AuthenticationResponse.builder()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .username(userEntity.getUsername())
                .role(userEntity.getRole())
                .build();
    }

    private Role getRoleEnumName(Role role) {
        return Role.valueOf(role.name());
    }

    private SectionRole getSectionRoleEnumName(SectionRole sectionRole) {
        return SectionRole.valueOf(sectionRole.name());
    }

    private void saveUserToken(String jwtToken, UserEntity user) {
        TokenEntity token = TokenEntity.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(UserEntity user) {
        List<TokenEntity> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void removeLastInvalidUserTokens(UserEntity user) {
        List<TokenEntity> invalidUserTokens = tokenRepository.findAllInvalidTokenByUser(user.getId());
        if (!invalidUserTokens.isEmpty()) {
            invalidUserTokens.forEach(tokenRepository::delete);
        }
    }

    private CompanyEntity getCompany() {
        return companyService.getCompanyById(1L);
    }
}
