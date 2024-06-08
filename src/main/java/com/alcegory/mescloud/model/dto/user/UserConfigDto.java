package com.alcegory.mescloud.model.dto.user;

import com.alcegory.mescloud.model.dto.company.CompanyDto;
import com.alcegory.mescloud.security.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserConfigDto {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private Role role;
    private CompanyDto company;
}
