package com.alcegory.mescloud.security.service;

import com.alcegory.mescloud.model.dto.user.UserConfigDto;
import com.alcegory.mescloud.security.exception.UserNotFoundException;
import com.alcegory.mescloud.security.model.SectionAuthority;
import com.alcegory.mescloud.security.model.UserRoleEntity;
import com.alcegory.mescloud.security.model.auth.AuthenticationResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserRoleService {

    void saveSectionUserRole(Long userId, Long roleId, Long sectionId);

    void updateSectionUserRole(Long userId, Long roleId, Long sectionId);

    void deleteSectionUserRolesByUserId(Long userRoleId);

    List<UserRoleEntity> findByUser(Long userId);

    List<UserRoleEntity> findByUserId(Long userId);

    UserRoleEntity findUserRoleByUserAndSection(Long userId, Long sectionId);

    UserConfigDto getUserRoleAndConfigurations(AuthenticationResponse authenticationResponse);

    UserConfigDto getCompanyConfigAndUserAuth(long userId, Authentication authentication) throws UserNotFoundException;

    List<UserConfigDto> getAllCompanyConfigAndUserAuth(Authentication authentication);

    void checkSectionAuthority(Authentication authentication, Long sectionId, SectionAuthority authority);

    void checkSectionAuthority(Long userId, Long sectionId, SectionAuthority authority);
}
