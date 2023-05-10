package com.tde.mescloud.service;

import com.tde.mescloud.model.entity.UserEntity;
import com.tde.mescloud.security.exception.UserNotFoundException;
import com.tde.mescloud.security.mapper.EntityDtoMapper;
import com.tde.mescloud.model.dto.UserDto;
import com.tde.mescloud.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.tde.mescloud.security.constant.UserServiceImpConstant.USER_NOT_FOUND_BY_USERNAME;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EntityDtoMapper mapper;

    public UserDto getUserById(Long id) {
        UserEntity userEntity = userRepository.findUserById(id);
        return mapper.convertToDto(userEntity);
    }

    public List<UserDto> getAllUsers() {
        List<UserEntity> userEntityList = userRepository.findAll();
        return mapper.convertToDto(userEntityList);
    }

    public UserDto updateUser(UserDto userDto) throws UserNotFoundException {
        UserEntity dbUserEntity = userRepository.findUserByUsername(userDto.getUsername());

        if(dbUserEntity == null) {
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

