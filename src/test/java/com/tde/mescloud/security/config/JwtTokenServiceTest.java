package com.tde.mescloud.security.config;

import com.tde.mescloud.model.entity.UserEntity;
import com.tde.mescloud.security.role.Role;
import com.tde.mescloud.security.service.JwtTokenService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtTokenServiceTest {
    
    @Autowired
    private JwtTokenService jwtTokenService;

    @Mock
    private UserEntity user;

    @Test
    public void testGenerateToken() {
        //User
        UserEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .password("1234")
                .username("doe")
                .email("doe@example.com")
                .role(Role.ADMIN)
                .build();
        // Act
        String token = jwtTokenService.generateToken(new HashMap<>(), user);
        // Assert
        Assert.assertNotNull(token);
        verify(user).getUsername();
    }
}
