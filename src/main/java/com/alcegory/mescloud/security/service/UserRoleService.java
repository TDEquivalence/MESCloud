package com.alcegory.mescloud.security.service;

import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.security.exception.UserNotFoundException;
import com.alcegory.mescloud.security.model.UserRoleEntity;
import com.alcegory.mescloud.security.model.auth.AuthenticationResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserRoleService {

    void saveUserRole(Long userId, Long roleId, Long sectionId);

    void updateUserRole(Long userId, Long roleId, Long sectionId);

    void deleteUserRolesByUserId(Long userRoleId);

    List<UserRoleEntity> findByUser(Long userId);

    List<UserRoleEntity> findByUserId(Long userId);

    UserRoleEntity findUserRoleByUserAndSection(Long userId, Long sectionId);

    UserConfigDto getUserRoleAndConfigurations(AuthenticationResponse authenticationResponse);

    UserConfigDto getCompanyConfigAndUserAuth(long userId, Authentication authentication) throws UserNotFoundException;
}
