package com.alcegory.mescloud.security.mapper;

import com.alcegory.mescloud.model.dto.user.UserDto;
import com.alcegory.mescloud.model.entity.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntityDtoMapper {

    private final ModelMapper mapper;

    @Autowired
    public EntityDtoMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public UserDto convertToDto(UserEntity userEntity) {
        return mapper.map(userEntity, UserDto.class);
    }

    public UserEntity convertToEntity(UserDto userDto) {
        return (userDto == null) ? null : mapper.map(userDto, UserEntity.class);
    }

    public List<UserDto> convertToDto(List<UserEntity> userEntityList) {
        return userEntityList.stream().map(this::convertToDto).toList();
    }

    public List<UserEntity> convertToEntity(List<UserDto> userDtoList) {
        return userDtoList.stream().map(this::convertToEntity).toList();
    }
}
