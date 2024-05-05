package com.alcegory.mescloud.security.utility;

import com.alcegory.mescloud.exception.ForbiddenAccessException;
import com.alcegory.mescloud.model.entity.UserEntity;
import com.alcegory.mescloud.security.model.*;
import com.alcegory.mescloud.security.service.UserRoleService;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;

public class AuthorityUtil {

    public static final String NOT_AUTHORIZED_MESSAGE = "User is not authorized to perform this action";

    private AuthorityUtil() {
        //Utility class, not meant for instantiation
    }

    public static void checkUserAndRole(Authentication authentication, Role expectedRole) {
        UserEntity user = (UserEntity) authentication.getPrincipal();

        if (user.getRole() != expectedRole) {
            throw new ForbiddenAccessException(NOT_AUTHORIZED_MESSAGE);
        }
    }

    public static void checkUserAndAuthority(Authentication authentication, Authority authority) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        UserEntity user = (UserEntity) authentication.getPrincipal();
        Role role = user.getRole();
        if (role.getAuthorities().contains(authority)) {
            return;
        }

        throw new ForbiddenAccessException(NOT_AUTHORIZED_MESSAGE);
    }

    public static void checkUserAndSectionRole(Authentication authentication, UserRoleService userRoleService, SectionRole expectedRole) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserRoleEntity userRole = userRoleService.findUserRoleByUserAndSection(user.getId(), 1L);

        if (!hasExpectedSectionRole(userRole, expectedRole)) {
            throw new ForbiddenAccessException(NOT_AUTHORIZED_MESSAGE);
        }
    }

    public static void checkPermission(Authentication authentication, Authority authorityToCheck) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        if (user == null || user.getRole() == null || !user.getRole().getAuthorities().contains(authorityToCheck)) {
            throw new ForbiddenAccessException(NOT_AUTHORIZED_MESSAGE);
        }
    }

    public static boolean hasPermission(UserRoleEntity userRole, SectionAuthority permissionToCheck) {
        return userRole != null &&
                userRole.getSectionRole() != null &&
                userRole.getSectionRole().getPermissions() != null &&
                userRole.getSectionRole().getPermissions().contains(permissionToCheck.getPermission());
    }

    public static boolean hasExpectedSectionRole(UserRoleEntity userRole, SectionRole expectedRole) {
        return userRole != null &&
                userRole.getSectionRole() != null &&
                userRole.getSectionRole().getName() == expectedRole;
    }

    public static boolean hasRoleAndPermission(UserRoleEntity userRole, SectionRole expectedRole, SectionAuthority permissionToCheck) {
        return hasExpectedSectionRole(userRole, expectedRole) && hasPermission(userRole, permissionToCheck);
    }
}
