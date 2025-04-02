package MicroServices.UserService.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String fullname;
    private String username;
    private String email;
    private String password;
}
