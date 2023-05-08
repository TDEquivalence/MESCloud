package com.tde.mescloud.security.config;

import com.tde.mescloud.security.model.entity.User;
import com.tde.mescloud.security.role.Role;
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

    private String secretKey = "jwt.secretKey=[a-zA-Z0-9._]^+$Guidelines89797987forAlphabeticalArraNumeralsandOtherSymbo$";

    @Autowired
    private JwtTokenService jwtTokenService;

    @Mock
    private User user;

    @Test
    public void testGenerateToken() {
        //User
        User.builder()
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
