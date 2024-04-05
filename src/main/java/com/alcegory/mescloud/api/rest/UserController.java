package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.UserDto;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.security.exception.UserNotFoundException;
import com.alcegory.mescloud.service.spi.UserServiceImpl;
import com.azure.core.annotation.Delete;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findUser(@PathVariable long id) {
        UserDto userDto = userServiceImpl.getUserById(id);
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

    @PostMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto user) throws UserNotFoundException {
        UserDto userDto = userServiceImpl.updateUser(user);
        return new ResponseEntity<>(userDto, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody UserDto user) {
        userServiceImpl.deleteUser(user);
        return ResponseEntity.ok("User deleted successfully");
    }
}
