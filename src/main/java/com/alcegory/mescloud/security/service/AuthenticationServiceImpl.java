package com.alcegory.mescloud.security.service;

import com.alcegory.mescloud.exception.RegistrationException;
import com.alcegory.mescloud.model.dto.SectionRoleMapping;
import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.model.entity.CompanyEntity;
import com.alcegory.mescloud.model.entity.UserEntity;
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
import com.alcegory.mescloud.service.CompanyService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.util.*;

import static com.alcegory.mescloud.security.model.Role.ADMIN;
import static com.alcegory.mescloud.security.utility.AuthorityUtil.checkUserAndRole;

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

    @Transactional(rollbackFor = {UsernameExistException.class, RoleNotFoundException.class})
    public AuthenticationResponse register(RegisterRequest request, Authentication authentication) throws UsernameExistException, RoleNotFoundException {
        checkUserAndRole(authentication, ADMIN);
        try {
            setUsernameByEmail(request);
            validateUsername(request);

            UserEntity user = buildUserEntity(request);
            userRepository.save(user);

            saveSectionRolesForUser(request, user);

            return userToAuthenticationResponse(user);
        } catch (UsernameExistException e) {
            throw new UsernameExistException("Username already exists");
        } catch (RoleNotFoundException e) {
            throw new RoleNotFoundException();
        } catch (Exception e) {
            throw new RegistrationException("Failed to register user");
        }
    }

    private UserEntity buildUserEntity(RegisterRequest request) {
        return UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .company(getCompany())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isActive(true)
                .isNotLocked(true)
                .createdAt(new Date())
                .build();
    }

    public void saveSectionRolesForUser(RegisterRequest request, UserEntity user) throws RoleNotFoundException {
        List<SectionRoleMapping> sectionRoles = Optional.ofNullable(request.getSectionRoles()).orElse(Collections.emptyList());

        for (SectionRoleMapping sectionRoleMapping : sectionRoles) {
            Long sectionId = sectionRoleMapping.getSectionId();
            Objects.requireNonNull(sectionId, "Section ID cannot be null");

            SectionRole sectionRole = sectionRoleMapping.getSectionRole();
            Objects.requireNonNull(sectionRole, "Section role cannot be null");

            String sectionRoleName = sectionRole.name();
            Optional<SectionRoleEntity> sectionRoleOptional = roleService.findByName(sectionRole);

            if (sectionRoleOptional.isEmpty()) {
                throw new RoleNotFoundException("Role not found: " + sectionRoleName);
            }

            SectionRoleEntity sectionRoleEntity = sectionRoleOptional.get();
            userRoleService.saveUserRole(user.getId(), sectionRoleEntity.getId(), sectionId);
        }
    }


    public UserConfigDto authenticate(AuthenticateRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserEntity user = userRepository.findUserByUsername(request.getUsername());
        String jwtToken = setJwtTokenCookie(user, response);
        revokeAllUserTokens(user);
        saveUserToken(jwtToken, user);
        removeLastInvalidUserTokens(user);
        AuthenticationResponse authenticationResponse = userToAuthenticationResponse(user);

        return userRoleService.getUserRoleAndConfigurations(authenticationResponse);
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

    public String setJwtTokenCookie(UserEntity user, HttpServletResponse response) {
        String jwtToken = jwtTokenService.generateToken(user);
        String refreshToken = jwtTokenService.generateRefreshToken(user);

        jwtTokenService.setJwtTokenCookie(response, jwtToken);
        jwtTokenService.setRefreshTokenCookie(response, refreshToken);

        return jwtToken;
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
