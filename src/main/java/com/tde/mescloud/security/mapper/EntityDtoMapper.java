package com.tde.mescloud.security.mapper;

import com.tde.mescloud.security.model.dto.UserDto;
import com.tde.mescloud.security.model.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityDtoMapper {

    @Autowired
    private ModelMapper mapper;

    public UserDto convertToDto(User user) {
        return mapper.map(user, UserDto.class);
    }

    public User convertToEntity(UserDto userDto) {
        return (userDto == null) ? null : mapper.map(userDto, User.class);
    }

    public List<UserDto> convertToDto(List<User> userList) {
        return userList.stream().map(this::convertToDto).toList();
    }

    public List<User> convertToEntity(List<UserDto> userDtoList) {
        return userDtoList.stream().map(this::convertToEntity).toList();
    }
}
