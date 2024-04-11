package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.exception.ForbiddenAccessException;
import com.alcegory.mescloud.exception.UserUpdateException;
import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.model.dto.UserDto;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.security.exception.UserNotFoundException;
import com.alcegory.mescloud.security.service.UserRoleService;
import com.alcegory.mescloud.service.spi.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userServiceImpl;
    private final UserRoleService userRoleService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findUser(@PathVariable long id) {
        UserDto userDto = userServiceImpl.getUserDtoById(id);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userServiceImpl.getFilteredUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<UserDto>> getFilteredUsers(@RequestBody Filter filter) {
        List<UserDto> users = userServiceImpl.getFilteredUsers(filter);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto user) {
        try {
            UserDto userDto = userServiceImpl.updateUser(user);
            return new ResponseEntity<>(userDto, HttpStatus.ACCEPTED);
        } catch (RoleNotFoundException | UserUpdateException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody UserDto user) {
        userServiceImpl.deleteUser(user);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/config")
    public ResponseEntity<UserConfigDto> getUserConfig(@RequestBody UserDto user, Authentication authentication) {
        try {
            UserConfigDto userConfigDto = userRoleService.getCompanyConfigAndUserAuth(user, authentication);
            return new ResponseEntity<>(userConfigDto, HttpStatus.ACCEPTED);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ForbiddenAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
