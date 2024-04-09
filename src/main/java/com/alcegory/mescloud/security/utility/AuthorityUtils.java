package com.alcegory.mescloud.security.utility;

import com.alcegory.mescloud.security.model.SectionAuthority;
import com.alcegory.mescloud.security.model.SectionRole;
import com.alcegory.mescloud.security.model.UserRoleEntity;

public class AuthorityUtils {

    public AuthorityUtils() {
    }

    public static boolean hasPermission(UserRoleEntity userRole, SectionAuthority permissionToCheck) {
        if (userRole == null || userRole.getSectionRole() == null || userRole.getSectionRole().getPermissions() == null) {
            return false;
        }

        return userRole.getSectionRole().getPermissions().contains(permissionToCheck.getPermission());
    }

    public static boolean hasExpectedRole(UserRoleEntity userRole, SectionRole expectedRole) {
        return userRole != null && userRole.getSectionRole() != null && userRole.getSectionRole().getName() == expectedRole;
    }
}
