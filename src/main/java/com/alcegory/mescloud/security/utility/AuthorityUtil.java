package com.alcegory.mescloud.security.utility;

import com.alcegory.mescloud.exception.ForbiddenAccessException;
import com.alcegory.mescloud.model.entity.UserEntity;
import com.alcegory.mescloud.security.model.*;
import com.alcegory.mescloud.security.service.UserRoleService;
import org.springframework.security.core.Authentication;

public class AuthorityUtil {

    private AuthorityUtil() {
        //Utility class, not meant for instantiation
    }

    public static void checkUserAndRole(Authentication authentication, Role expectedRole) {
        UserEntity user = (UserEntity) authentication.getPrincipal();

        if (user.getRole() != expectedRole) {
            throw new ForbiddenAccessException("User is not authorized to perform this action");
        }
    }

    public static void checkUserAndSectionRole(Authentication authentication, UserRoleService userRoleService, SectionRole expectedRole) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserRoleEntity userRole = userRoleService.findUserRoleByUserAndSection(user.getId(), 1L);

        if (!hasExpectedSectionRole(userRole, expectedRole)) {
            throw new ForbiddenAccessException("User is not authorized to perform this action");
        }
    }

    public static void checkPermission(Authentication authentication, Authority authorityToCheck) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        if (user == null || user.getRole() == null || !user.getRole().getAuthorities().contains(authorityToCheck)) {
            throw new ForbiddenAccessException("User is not authorized to perform this action");
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
