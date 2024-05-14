package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.company.*;
import com.alcegory.mescloud.model.entity.*;
import com.alcegory.mescloud.model.dto.user.UserConfigDto;
import com.alcegory.mescloud.model.entity.company.CompanyEntity;
import com.alcegory.mescloud.model.entity.company.FactoryEntity;
import com.alcegory.mescloud.model.entity.company.SectionConfigEntity;
import com.alcegory.mescloud.model.entity.company.SectionEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
public class UserConverterImpl implements UserConverter {

    @Override
    public UserConfigDto convertToDtoWithRelatedEntities(UserEntity userEntity) {
        UserConfigDto userConfigDto = new UserConfigDto();
        if (userEntity != null) {
            userConfigDto.setId(userEntity.getId());
            userConfigDto.setFirstName(userEntity.getFirstName());
            userConfigDto.setLastName(userEntity.getLastName());
            userConfigDto.setUsername(userEntity.getUsername());
            userConfigDto.setRole(userEntity.getRole());
            userConfigDto.setCompany(convertToCompanyDto(userEntity.getCompany()));
        }

        return userConfigDto;
    }

    private CompanyDto convertToCompanyDto(CompanyEntity companyEntity) {
        CompanyDto companyDto = new CompanyDto();
        if (companyEntity != null) {
            companyDto.setId(companyEntity.getId());
            companyDto.setName(companyEntity.getName());
            // Convert factoryList
            companyDto.setFactoryList(convertToFactoryDtoList(companyEntity.getFactoryList()));
        }
        return companyDto;
    }

    private List<FactoryDto> convertToFactoryDtoList(List<FactoryEntity> factoryEntities) {
        if (factoryEntities == null) {
            return Collections.emptyList();
        }
        return factoryEntities.stream()
                .map(this::convertToFactoryDto)
                .toList();
    }

    private FactoryDto convertToFactoryDto(FactoryEntity factoryEntity) {
        FactoryDto factoryDto = new FactoryDto();
        if (factoryEntity != null) {
            factoryDto.setId(factoryEntity.getId());
            factoryDto.setName(factoryEntity.getName());
            // Convert sectionList
            factoryDto.setSectionList(convertToSectionDtoList(factoryEntity.getSectionList()));
        }
        return factoryDto;
    }

    private List<SectionDto> convertToSectionDtoList(List<SectionEntity> sectionEntities) {
        if (sectionEntities == null) {
            return Collections.emptyList();
        }
        return sectionEntities.stream()
                .map(this::convertToSectionDto)
                .distinct() // Ensure distinct sections
                .toList();
    }

    private SectionDto convertToSectionDto(SectionEntity sectionEntity) {
        SectionDto sectionDto = new SectionDto();
        if (sectionEntity != null) {
            sectionDto.setId(sectionEntity.getId());
            sectionDto.setName(sectionEntity.getName());
            // Convert all associated section configs
            sectionDto.setSectionConfigList(convertToSectionConfigDtoList(sectionEntity.getSectionConfigList()));
        }
        return sectionDto;
    }

    private List<SectionConfigDto> convertToSectionConfigDtoList(List<SectionConfigEntity> sectionConfigEntities) {
        if (sectionConfigEntities == null) {
            return Collections.emptyList();
        }
        return sectionConfigEntities.stream()
                .map(this::convertToSectionConfigDto)
                .toList();
    }

    private List<FeatureDto> convertToFeatureDtoList(List<FeatureEntity> featureEntities) {
        if (featureEntities == null) {
            return Collections.emptyList();
        }
        return featureEntities.stream()
                .map(this::convertToFeatureDto)
                .toList();
    }

    private SectionConfigDto convertToSectionConfigDto(SectionConfigEntity sectionConfigEntity) {
        SectionConfigDto sectionConfigDto = new SectionConfigDto();
        if (sectionConfigEntity != null) {
            sectionConfigDto.setId(sectionConfigEntity.getId());
            sectionConfigDto.setLabel(sectionConfigEntity.getLabel());
            sectionConfigDto.setFeatureList(convertToFeatureDtoList(sectionConfigEntity.getFeatureList()));
            sectionConfigDto.setOrder(sectionConfigEntity.getOrder());
        }
        return sectionConfigDto;
    }

    private FeatureDto convertToFeatureDto(FeatureEntity featureEntity) {
        FeatureDto featureDto = new FeatureDto();
        if (featureEntity != null) {
            featureDto.setId(featureEntity.getId());
            featureDto.setName(featureEntity.getName());
            // Set other FeatureDto properties as needed
        }
        return featureDto;
    }
}
