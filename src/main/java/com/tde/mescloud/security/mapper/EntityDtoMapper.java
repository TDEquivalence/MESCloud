package com.tde.mescloud.security.mapper;

import com.tde.mescloud.model.dto.UserDto;
import com.tde.mescloud.model.entity.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntityDtoMapper {

    @Autowired
    private ModelMapper mapper;

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
