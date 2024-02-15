package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.UserDto;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.security.exception.UserNotFoundException;
import com.alcegory.mescloud.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findUser(@PathVariable long id) {
        UserDto userDto = userService.getUserById(id);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    //TODO: remove /all to follow REST specifications
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getFilteredUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<UserDto>> getFilteredUsers(@RequestBody Filter filter) {
        List<UserDto> users = userService.getFilteredUsers(filter);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto user) throws UserNotFoundException {
        UserDto userDto = userService.updateUser(user);
        return new ResponseEntity<>(userDto, HttpStatus.ACCEPTED);
    }
}
