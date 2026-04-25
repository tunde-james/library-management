package com.example.librarymanagement.dto.user;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminResponseDto {

    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
}
