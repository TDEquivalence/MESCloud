package com.alcegory.mescloud.service.user;

import com.alcegory.mescloud.model.dto.user.UserConfigDto;
import com.alcegory.mescloud.model.dto.user.UserDto;
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

    List<UserDto> getDtoUsers();

    List<UserEntity> getUsers();

    List<UserDto> getDtoUsers(Filter filter);

    UserDto updateUser(UserDto userDto) throws UserNotFoundException, RoleNotFoundException;

    UserConfigDto getUserConfigByAuth(Authentication authentication);

    UserConfigDto getUserConfigByAuth(UserEntity user);

    UserEntity getUserByAuth(AuthenticationResponse authenticateRequest);

    void deleteUser(Long requestById, Authentication authentication);

    Optional<UserEntity> findByUsername(String username);
}
