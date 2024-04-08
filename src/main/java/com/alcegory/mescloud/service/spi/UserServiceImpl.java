package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.converter.UserConverter;
import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.model.dto.UserDto;
import com.alcegory.mescloud.model.entity.UserEntity;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.repository.UserRepository;
import com.alcegory.mescloud.security.exception.UserNotFoundException;
import com.alcegory.mescloud.security.mapper.EntityDtoMapper;
import com.alcegory.mescloud.security.model.auth.AuthenticationResponse;
import com.alcegory.mescloud.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.alcegory.mescloud.security.constant.UserServiceImpConstant.USER_NOT_FOUND_BY_USERNAME;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityDtoMapper mapper;
    private final UserConverter userConverter;

    public UserDto getUserById(Long id) {
        UserEntity userEntity = userRepository.findUserById(id);
        return mapper.convertToDto(userEntity);
    }

    public List<UserDto> getFilteredUsers() {
        List<UserEntity> userEntityList = userRepository.findAll();
        return mapper.convertToDto(userEntityList);
    }

    public List<UserDto> getFilteredUsers(Filter filter) {
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

    public UserConfigDto getUserConfigByAuth(AuthenticationResponse authenticateRequest) {
        if (authenticateRequest == null || authenticateRequest.getUsername() == null) {
            return null;
        }

        UserEntity userEntity = userRepository.findUserByUsername(authenticateRequest.getUsername());
        return userConverter.convertToDtoWithRelatedEntities(userEntity);
    }

    public UserEntity getUserByAuth(AuthenticationResponse authenticateRequest) {
        if (authenticateRequest == null || authenticateRequest.getUsername() == null) {
            return null;
        }

        return userRepository.findUserByUsername(authenticateRequest.getUsername());
    }

    @Override
    public void deleteUser(UserDto user) {
        UserEntity userEntity = mapper.convertToEntity(user);
        userRepository.delete(userEntity);
    }
}

