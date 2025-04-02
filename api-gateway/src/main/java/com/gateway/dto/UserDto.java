package com.gateway.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String fullname;
    private String username;
    private String email;
}
