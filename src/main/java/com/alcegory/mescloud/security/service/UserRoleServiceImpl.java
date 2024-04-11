package com.alcegory.mescloud.security.service;

import com.alcegory.mescloud.model.converter.UserConverterImpl;
import com.alcegory.mescloud.model.dto.FactoryDto;
import com.alcegory.mescloud.model.dto.SectionDto;
import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.model.entity.UserEntity;
import com.alcegory.mescloud.repository.UserRoleRepository;
import com.alcegory.mescloud.security.model.UserRoleEntity;
import com.alcegory.mescloud.security.model.auth.AuthenticationResponse;
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
}


