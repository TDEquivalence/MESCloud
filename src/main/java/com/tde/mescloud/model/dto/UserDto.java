package com.tde.mescloud.model.dto;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonFormat;
=======
>>>>>>> aa278757c05559b51687e8f330e3f47fab062a82
import com.tde.mescloud.security.role.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

<<<<<<< HEAD
import java.util.Date;

=======
>>>>>>> aa278757c05559b51687e8f330e3f47fab062a82
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
<<<<<<< HEAD
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "Europe/Lisbon")
    private Date createdAt;
=======
>>>>>>> aa278757c05559b51687e8f330e3f47fab062a82
}
