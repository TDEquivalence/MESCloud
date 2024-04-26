package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.dto.UserDto;
import com.alcegory.mescloud.model.entity.UserEntity;
import com.alcegory.mescloud.repository.UserRepository;
import com.alcegory.mescloud.security.mapper.EntityDtoMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityDtoMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetUserDtoById() {
        when(userRepository.findUserById(anyLong())).thenReturn(new UserEntity());
        when(mapper.convertToDto(any(UserEntity.class))).thenReturn(new UserDto());

        UserDto userDto = userService.getUserDtoById(1L);

        assertNotNull(userDto);
    }

    @Test
    void testGetUserById() {
        when(userRepository.findUserById(anyLong())).thenReturn(new UserEntity());

        UserEntity userEntity = userService.getUserById(1L);

        assertNotNull(userEntity);
    }

    @Test
    void testGetUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(new UserEntity()));

        List<UserEntity> userEntities = userService.getUsers();

        assertFalse(userEntities.isEmpty());
    }
}
