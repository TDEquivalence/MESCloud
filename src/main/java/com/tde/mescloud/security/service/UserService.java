package com.tde.mescloud.security.service;

import com.tde.mescloud.security.mapper.EntityDtoMapper;
import com.tde.mescloud.security.model.dto.UserDto;
import com.tde.mescloud.security.model.entity.User;
import com.tde.mescloud.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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


}
