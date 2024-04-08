package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.repository.UserRoleRepository;
import com.alcegory.mescloud.security.model.UserRoleEntity;
import com.alcegory.mescloud.service.UserRoleService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {


    private static final String NULL_PARAMETER_ERROR_MSG = "One or more parameters are null.";
    private final UserRoleRepository repository;

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
