package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.dto.UserDto;
import com.tde.mescloud.model.dto.UserWinnow;
import com.tde.mescloud.security.exception.UserNotFoundException;
import com.tde.mescloud.service.UserService;
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

    @PostMapping("/winnow")
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestBody UserWinnow winnow) {
        List<UserDto> users = userService.getAllUsers(winnow);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto user) throws UserNotFoundException {
        UserDto userDto = userService.updateUser(user);
        return new ResponseEntity<>(userDto, HttpStatus.ACCEPTED);
    }
}
