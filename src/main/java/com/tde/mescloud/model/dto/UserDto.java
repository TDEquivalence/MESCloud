package com.tde.mescloud.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tde.mescloud.security.role.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private Role role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "Europe/Lisbon")
    private Date createdAt;
}
