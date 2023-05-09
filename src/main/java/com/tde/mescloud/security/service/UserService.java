package com.tde.mescloud.security.service;

import com.tde.mescloud.security.exception.UserNotFoundException;
import com.tde.mescloud.security.mapper.EntityDtoMapper;
import com.tde.mescloud.security.model.dto.UserDto;
import com.tde.mescloud.security.model.entity.User;
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
        User user = userRepository.findUserById(id);
        return mapper.convertToDto(user);
    }

    public List<UserDto> getAllUsers() {
        List<User> userList = userRepository.findAll();
        return mapper.convertToDto(userList);
    }

    public UserDto updateUser(UserDto userDto) throws UserNotFoundException {
        User dbUser = userRepository.findUserByUsername(userDto.getUsername());

        if(dbUser == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME);
        }
        dbUser = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .updatedAt(new Date())
                .build();

        userRepository.save(dbUser);
        return mapper.convertToDto(dbUser);
    }
}

