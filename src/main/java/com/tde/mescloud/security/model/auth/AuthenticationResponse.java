package com.tde.mescloud.security.model.auth;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonFormat;
=======
>>>>>>> aa278757c05559b51687e8f330e3f47fab062a82
import com.tde.mescloud.security.role.Role;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
<<<<<<< HEAD
    private Role role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "Europe/Lisbon")
    private Date createdAt;
=======
    private Date createdAt;
    private Role role;
>>>>>>> aa278757c05559b51687e8f330e3f47fab062a82
}
