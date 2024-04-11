package com.alcegory.mescloud.utility;

import com.alcegory.mescloud.model.dto.FactoryDto;
import com.alcegory.mescloud.model.dto.SectionDto;
import com.alcegory.mescloud.security.model.UserRoleEntity;

import java.util.ArrayList;
import java.util.List;

public class SectionConfigUtil {

    private SectionConfigUtil() {
        //Utility class, not meant for instantiation
    }

    public static void filterSectionsWithRoles(List<UserRoleEntity> userRoles, List<FactoryDto> factoryList) {
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
}
