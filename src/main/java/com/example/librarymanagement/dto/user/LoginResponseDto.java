package com.example.librarymanagement.dto.user;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {

    private String token;
    private String username;
    private Set<String> roles;
}
