package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.exception.ForbiddenAccessException;
import com.alcegory.mescloud.exception.UserUpdateException;
import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.model.dto.UserDto;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.security.exception.UserNotFoundException;
import com.alcegory.mescloud.security.service.UserRoleService;
import com.alcegory.mescloud.service.UserService;
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

    private final UserService userService;
    private final UserRoleService userRoleService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findUser(@PathVariable long id) {
        UserDto userDto = userService.getUserDtoById(id);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserConfigDto>> getAllUsers(Authentication authentication) {
        List<UserConfigDto> users = userRoleService.getAllCompanyConfigAndUserAuth(authentication);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<UserDto>> getFilteredUsers(@RequestBody Filter filter) {
        List<UserDto> users = userService.getDtoUsers(filter);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto user) {
        try {
            UserDto userDto = userService.updateUser(user);
            return new ResponseEntity<>(userDto, HttpStatus.ACCEPTED);
        } catch (RoleNotFoundException | UserUpdateException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody UserDto user) {
        userService.deleteUser(user);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/{id}/config")
    public ResponseEntity<UserConfigDto> getUserConfig(@PathVariable long id, Authentication authentication) {
        try {
            UserConfigDto userConfigDto = userRoleService.getCompanyConfigAndUserAuth(id, authentication);
            return new ResponseEntity<>(userConfigDto, HttpStatus.ACCEPTED);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ForbiddenAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
