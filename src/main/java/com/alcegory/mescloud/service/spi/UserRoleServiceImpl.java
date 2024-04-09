package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.converter.UserConverterImpl;
import com.alcegory.mescloud.model.dto.FactoryDto;
import com.alcegory.mescloud.model.dto.SectionDto;
import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.model.entity.UserEntity;
import com.alcegory.mescloud.repository.UserRoleRepository;
import com.alcegory.mescloud.security.model.UserRoleEntity;
import com.alcegory.mescloud.security.model.auth.AuthenticationResponse;
import com.alcegory.mescloud.service.UserRoleService;
import com.alcegory.mescloud.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {


    private static final String NULL_PARAMETER_ERROR_MSG = "One or more parameters are null.";
    private final UserRoleRepository repository;
    private final UserService userService;
    private final UserConverterImpl userConverter;

    public UserConfigDto getUserRoleAndConfigurations(AuthenticationResponse authenticationResponse) {
        Optional<UserEntity> optionalUser = Optional.ofNullable(userService.getUserByAuth(authenticationResponse));
        if (optionalUser.isEmpty()) {
            return null;
        }

        UserEntity user = optionalUser.get();
        UserConfigDto userConfig = userConverter.convertToDtoWithRelatedEntities(user);

        if (userConfig == null || userConfig.getCompany() == null || userConfig.getCompany().getFactoryList() == null) {
            return userConfig;
        }

        List<UserRoleEntity> userRoles = findByUserId(userConfig.getId());
        filterSectionsWithRoles(userRoles, userConfig.getCompany().getFactoryList());

        return userConfig;
    }

    private void filterSectionsWithRoles(List<UserRoleEntity> userRoles, List<FactoryDto> factoryList) {
        for (FactoryDto factory : factoryList) {
            if (factory.getSectionList() == null || factory.getSectionList().isEmpty()) {
                continue;
            }
            List<SectionDto> sectionsWithRoles = new ArrayList<>();
            for (SectionDto section : factory.getSectionList()) {
                Long sectionId = section.getId();
                boolean hasRole = userRoles.stream()
                        .anyMatch(role -> role.getSectionId().equals(sectionId));
                if (hasRole) {
                    sectionsWithRoles.add(section);
                }
            }
            factory.setSectionList(sectionsWithRoles);
        }
    }

    @Override
    @Transactional
    public void saveUserRole(Long userId, Long roleId, Long sectionId) {
        if (userId == null || roleId == null || sectionId == null) {
            log.error("Failed to save user role: " + NULL_PARAMETER_ERROR_MSG);
            throw new IllegalArgumentException(NULL_PARAMETER_ERROR_MSG);
        }
        repository.saveUserRole(userId, roleId, sectionId);
        log.info("User role saved successfully. User ID: {}, Role ID: {}, Section ID: {}", userId, roleId, sectionId);
    }

    @Override
    @Transactional
    public void updateUserRole(Long userRoleId, Long roleId, Long sectionId) {
        if (userRoleId == null || roleId == null || sectionId == null) {
            log.error("Failed to update user role: " + NULL_PARAMETER_ERROR_MSG);
            throw new IllegalArgumentException(NULL_PARAMETER_ERROR_MSG);
        }
        repository.updateUserRole(userRoleId, roleId, sectionId);
        log.info("User role updated successfully. User Role ID: {}, Role ID: {}, Section ID: {}", userRoleId, roleId, sectionId);
    }

    @Override
    @Transactional
    public void deleteUserRoleById(Long userRoleId) {
        if (userRoleId == null) {
            log.error("Failed to delete user role: User role ID is null.");
            throw new IllegalArgumentException("User role ID is null.");
        }
        repository.deleteUserRoleById(userRoleId);
        log.info("User role deleted successfully. User Role ID: {}", userRoleId);
    }

    @Override
    public List<UserRoleEntity> findByUser(Long userId) {
        if (userId == null) {
            log.error("Failed to find user roles: User ID is null.");
            throw new IllegalArgumentException("User ID is null.");
        }
        return repository.findByUser(userId);
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
            log.error("Failed to find user role: " + NULL_PARAMETER_ERROR_MSG);
            throw new IllegalArgumentException(NULL_PARAMETER_ERROR_MSG);
        }
        return repository.findUserRoleByUserAndSection(userId, sectionId);
    }
}
