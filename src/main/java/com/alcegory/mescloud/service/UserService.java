package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.model.dto.UserDto;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.security.exception.UserNotFoundException;
import com.alcegory.mescloud.security.model.auth.AuthenticationResponse;

import java.util.List;

public interface UserService {

    UserDto getUserById(Long id);

    List<UserDto> getFilteredUsers();

    List<UserDto> getFilteredUsers(Filter filter);

    UserDto updateUser(UserDto userDto) throws UserNotFoundException;

    UserConfigDto getUserByAuth(AuthenticationResponse authenticateRequest);
}
