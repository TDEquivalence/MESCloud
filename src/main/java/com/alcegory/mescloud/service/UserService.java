package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.model.dto.UserDto;
import com.alcegory.mescloud.model.entity.UserEntity;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.security.exception.UserNotFoundException;
import com.alcegory.mescloud.security.model.auth.AuthenticationResponse;
import org.springframework.security.core.Authentication;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    UserEntity getUserById(Long id);

    UserDto getUserDtoById(Long id);

    List<UserDto> getFilteredUsers();

    List<UserDto> getFilteredUsers(Filter filter);

    UserDto updateUser(UserDto userDto) throws UserNotFoundException, RoleNotFoundException;

    UserConfigDto getUserConfigByAuth(Authentication authentication);

    UserConfigDto getUserConfigByAuth(UserEntity user);

    UserEntity getUserByAuth(AuthenticationResponse authenticateRequest);

    void deleteUser(UserDto user);

    Optional<UserEntity> findByUsername(String username);
}
