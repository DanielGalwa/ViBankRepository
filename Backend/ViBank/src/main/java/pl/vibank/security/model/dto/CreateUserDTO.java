package pl.vibank.security.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserDTO {

    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;
}
