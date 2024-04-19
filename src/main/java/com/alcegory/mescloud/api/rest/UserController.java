package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.api.rest.response.ErrorResponse;
import com.alcegory.mescloud.exception.DeleteUserException;
import com.alcegory.mescloud.exception.ForbiddenAccessException;
import com.alcegory.mescloud.exception.UserUpdateException;
import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.model.dto.UserDto;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.security.exception.UserNotFoundException;
import com.alcegory.mescloud.security.service.UserRoleService;
import com.alcegory.mescloud.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@Slf4j
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
        } catch (RoleNotFoundException | UserUpdateException | UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId, Authentication authentication) {
        try {
            userService.deleteUser(userId, authentication);
            return ResponseEntity.ok("User deleted successfully");
        } catch (ForbiddenAccessException e) {
            log.error("User is not authorized to delete: {}", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("User is not authorized to perform this action: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        } catch (DeleteUserException e) {
            log.error("Failed to delete user: {}", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("Failed to delete user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            log.error("An unexpected error occurred: {}", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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
