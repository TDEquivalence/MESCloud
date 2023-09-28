package com.alcegory.mescloud.service;

import com.alcegory.mescloud.repository.UserRepository;
import com.alcegory.mescloud.model.dto.UserFilter;
import com.alcegory.mescloud.model.entity.UserEntity;
import com.alcegory.mescloud.security.exception.UserNotFoundException;
import com.alcegory.mescloud.security.mapper.EntityDtoMapper;
import com.alcegory.mescloud.model.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.alcegory.mescloud.security.constant.UserServiceImpConstant.USER_NOT_FOUND_BY_USERNAME;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EntityDtoMapper mapper;

    public UserDto getUserById(Long id) {
        UserEntity userEntity = userRepository.findUserById(id);
        return mapper.convertToDto(userEntity);
    }

    public List<UserDto> getFilteredUsers() {
        List<UserEntity> userEntityList = userRepository.findAll();
        return mapper.convertToDto(userEntityList);
    }

    public List<UserDto> getFilteredUsers(UserFilter filter) {
        List<UserEntity> userEntityList = userRepository.getFilteredUsers(filter);
        return mapper.convertToDto(userEntityList);
    }

    public UserDto updateUser(UserDto userDto) throws UserNotFoundException {
        UserEntity dbUserEntity = userRepository.findUserByUsername(userDto.getUsername());

        if (dbUserEntity == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME);
        }
        dbUserEntity = UserEntity.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .username(userDto.getUsername())
                .updatedAt(new Date())
                .build();

        userRepository.save(dbUserEntity);
        return mapper.convertToDto(dbUserEntity);
    }
}

