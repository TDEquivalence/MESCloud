package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.*;
import com.alcegory.mescloud.model.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UserConverterImpl implements UserConverter {

    private final ModelMapper modelMapper;
    private final CountingEquipmentConverter countingEquipmentConverter;

    @Autowired
    public UserConverterImpl(ModelMapper modelMapper, CountingEquipmentConverter countingEquipmentConverter) {
        this.modelMapper = modelMapper;
        this.countingEquipmentConverter = countingEquipmentConverter;
    }

    @Override
    public UserDto convertToDtoWithRelatedEntities(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        if (userEntity != null) {
            userDto.setId(userEntity.getId());
            userDto.setUsername(userEntity.getUsername());
            userDto.setFirstName(userEntity.getFirstName());
            userDto.setLastName(userEntity.getLastName());
            // Convert company
            userDto.setCompany(convertToCompanyDto(userEntity.getCompany()));
        }
        return userDto;
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
            sectionConfigDto.setName(sectionConfigEntity.getName());
            sectionConfigDto.setFeatureList(convertToFeatureDtoList(sectionConfigEntity.getFeatureList()));
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
