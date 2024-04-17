package com.alcegory.mescloud.security.service;

import com.alcegory.mescloud.exception.ForbiddenAccessException;
import com.alcegory.mescloud.model.converter.UserConverterImpl;
import com.alcegory.mescloud.model.dto.SectionDto;
import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.model.entity.UserEntity;
import com.alcegory.mescloud.security.exception.UserNotFoundException;
import com.alcegory.mescloud.security.model.SectionAuthority;
import com.alcegory.mescloud.security.model.SectionRoleEntity;
import com.alcegory.mescloud.security.model.UserRoleEntity;
import com.alcegory.mescloud.security.model.auth.AuthenticationResponse;
import com.alcegory.mescloud.security.repository.UserRoleRepository;
import com.alcegory.mescloud.service.UserService;
import com.alcegory.mescloud.utility.SectionConfigUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.alcegory.mescloud.security.model.Authority.ADMIN_READ;
import static com.alcegory.mescloud.security.utility.AuthorityUtil.checkPermission;

@Slf4j
@Service
@AllArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private static final String NULL_PARAMETER_ERROR_MSG = "One or more parameters are null.";

    private final UserRoleRepository repository;
    private final UserService userService;
    private final UserConverterImpl userConverter;

    @Override
    public UserConfigDto getUserRoleAndConfigurations(AuthenticationResponse authenticationResponse) {
        UserEntity user = userService.getUserByAuth(authenticationResponse);
        if (user == null) {
            return null;
        }

        UserConfigDto userConfig = userConverter.convertToDtoWithRelatedEntities(user);
        if (userConfig == null || userConfig.getCompany() == null || userConfig.getCompany().getFactoryList() == null) {
            return userConfig;
        }

        List<UserRoleEntity> userRoles = findByUserId(userConfig.getId());
        SectionConfigUtil.filterSectionsWithRoles(userRoles, userConfig.getCompany().getFactoryList());

        return userConfig;
    }

    @Override
    public UserConfigDto getCompanyConfigAndUserAuth(long userId, Authentication authentication) throws UserNotFoundException {
        checkPermission(authentication, ADMIN_READ);
        Optional<UserEntity> userEntityOptional = Optional.ofNullable(userService.getUserById(userId));

        if (userEntityOptional.isEmpty()) {
            throw new UserNotFoundException("User not found for username: " + userId);
        }

        UserConfigDto authenticateUserConfig = userService.getUserConfigByAuth(authentication);
        UserConfigDto userToUpdateConfig = userService.getUserConfigByAuth(userEntityOptional.get());

        UserConfigDto mergedUsers = mergeUserConfigs(authenticateUserConfig, userToUpdateConfig);
        List<UserRoleEntity> userRoles = findByUserId(mergedUsers.getId());

        List<SectionDto> sections = mergedUsers.getCompany().getFactoryList().stream()
                .flatMap(factory -> factory.getSectionList().stream())
                .toList();

        mapUserRolesToSections(userRoles, sections);
        return mergedUsers;
    }

    public List<UserConfigDto> getAllCompanyConfigAndUserAuth(Authentication authentication) {
        checkPermission(authentication, ADMIN_READ);
        List<UserEntity> users = userService.getUsers();

        List<UserConfigDto> mergedUserConfigs = new ArrayList<>();
        UserConfigDto authenticateUserConfig = userService.getUserConfigByAuth(authentication);

        for (UserEntity user : users) {
            UserConfigDto userToUpdateConfig = userService.getUserConfigByAuth(user);
            UserConfigDto mergedUsers = mergeUserConfigs(authenticateUserConfig, userToUpdateConfig);
            List<UserRoleEntity> userRoles = findByUserId(mergedUsers.getId());

            List<SectionDto> sections = mergedUsers.getCompany().getFactoryList().stream()
                    .flatMap(factory -> factory.getSectionList().stream())
                    .toList();

            mapUserRolesToSections(userRoles, sections);

            mergedUserConfigs.add(mergedUsers);
        }

        return mergedUserConfigs;
    }

    public void mapUserRolesToSections(List<UserRoleEntity> userRoles, List<SectionDto> sections) {
        Map<Long, SectionRoleEntity> sectionRoleMap = new HashMap<>();
        for (UserRoleEntity userRole : userRoles) {
            sectionRoleMap.put(userRole.getSectionId(), userRole.getSectionRole());
        }

        for (SectionDto section : sections) {
            Long sectionId = section.getId();
            SectionRoleEntity sectionRole = sectionRoleMap.get(sectionId);
            if (sectionRole != null) {
                section.setSectionRole(sectionRole.getName());
            }
        }
    }

    private UserConfigDto mergeUserConfigs(UserConfigDto authenticateUserConfig, UserConfigDto userToUpdateConfig) {
        UserConfigDto mergedConfig = new UserConfigDto();

        mergedConfig.setId(userToUpdateConfig.getId());
        mergedConfig.setUsername(userToUpdateConfig.getUsername());
        mergedConfig.setFirstName(userToUpdateConfig.getFirstName());
        mergedConfig.setLastName(userToUpdateConfig.getLastName());
        mergedConfig.setRole(userToUpdateConfig.getRole());
        mergedConfig.setCompany(userToUpdateConfig.getCompany());

        if (userToUpdateConfig.getId() == null) {
            mergedConfig.setId(authenticateUserConfig.getId());
        }
        if (userToUpdateConfig.getUsername() == null) {
            mergedConfig.setUsername(authenticateUserConfig.getUsername());
        }
        if (userToUpdateConfig.getFirstName() == null) {
            mergedConfig.setFirstName(authenticateUserConfig.getFirstName());
        }
        if (userToUpdateConfig.getLastName() == null) {
            mergedConfig.setLastName(authenticateUserConfig.getLastName());
        }
        if (userToUpdateConfig.getRole() == null) {
            mergedConfig.setRole(authenticateUserConfig.getRole());
        }
        if (userToUpdateConfig.getCompany() == null) {
            mergedConfig.setCompany(authenticateUserConfig.getCompany());
        }

        return mergedConfig;
    }

    @Override
    @Transactional
    public void saveUserRole(Long userId, Long roleId, Long sectionId) {
        if (userId == null || roleId == null || sectionId == null) {
            log.error("Failed to save user role: {}", NULL_PARAMETER_ERROR_MSG);
            throw new IllegalArgumentException(NULL_PARAMETER_ERROR_MSG);
        }
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setSectionId(sectionId);
        repository.save(userRole);
        log.info("User role saved successfully. User ID: {}, Role ID: {}, Section ID: {}", userId, roleId, sectionId);
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, Long newRoleId, Long newSectionId) {
        if (userId == null || newRoleId == null || newSectionId == null) {
            log.error("Failed to update user role: {}", NULL_PARAMETER_ERROR_MSG);
            throw new IllegalArgumentException(NULL_PARAMETER_ERROR_MSG);
        }

        List<UserRoleEntity> userRoles = repository.findByUserId(userId);
        if (userRoles.isEmpty()) {
            log.error("Failed to update user role: User roles for User ID {} not found.", userId);
            throw new IllegalArgumentException("User roles not found.");
        }

        for (UserRoleEntity userRole : userRoles) {
            userRole.setRoleId(newRoleId);
            userRole.setSectionId(newSectionId);
            repository.save(userRole);
        }

        log.info("User roles updated successfully for User ID {}. New Role ID: {}, New Section ID: {}", userId, newRoleId, newSectionId);
    }

    @Override
    @Transactional
    public void deleteUserRolesByUserId(Long userId) {
        if (userId == null) {
            log.error("Failed to delete user roles: User ID is null.");
            throw new IllegalArgumentException("User ID is null.");
        }
        repository.deleteByUserId(userId);
        log.info("User roles deleted successfully for User ID: {}", userId);
    }

    @Override
    public List<UserRoleEntity> findByUser(Long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public List<UserRoleEntity> findByUserId(Long userId) {
        if (userId == null) {
            log.error("Failed to find user roles: User ID is null.");
            throw new IllegalArgumentException("User ID is null.");
        }
        return repository.findByUserId(userId);
    }

    @Override
    public UserRoleEntity findUserRoleByUserAndSection(Long userId, Long sectionId) {
        if (userId == null || sectionId == null) {
            log.error("Failed to find user role: {}", NULL_PARAMETER_ERROR_MSG);
            throw new IllegalArgumentException(NULL_PARAMETER_ERROR_MSG);
        }
        return repository.findByUserIdAndSectionId(userId, sectionId);
    }

    public void checkSectionAuthority(Authentication authentication, Long sectionId, SectionAuthority authority) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        checkSectionAuthority(user.getId(), sectionId, authority);
    }

    public void checkSectionAuthority(Long userId, Long sectionId, SectionAuthority authority) {
        UserRoleEntity userRole = findUserRoleByUserAndSection(userId, sectionId);
        if (userRole != null) {
            SectionRoleEntity roleEntity = userRole.getSectionRole();
            if (!roleEntity.getPermissions().contains(authority.getPermission())) {
                throw new ForbiddenAccessException("User is not authorized to perform this action");
            }
        } else {
            throw new ForbiddenAccessException("User is not authorized to perform this action");
        }
    }
}


