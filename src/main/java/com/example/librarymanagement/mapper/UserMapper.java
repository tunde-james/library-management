package com.example.librarymanagement.mapper;

import org.springframework.lang.NonNull;
import com.example.librarymanagement.dto.user.AdminResponseDto;
import com.example.librarymanagement.dto.user.LoginResponseDto;
import com.example.librarymanagement.dto.user.RegisterRequestDto;
import com.example.librarymanagement.dto.user.RegisterResponseDto;
import com.example.librarymanagement.entity.User;

public class UserMapper {

    public static @NonNull User toEntity(RegisterRequestDto request) {

        if (request == null) {
            throw new IllegalArgumentException("RegisterRequestDto cannot be null");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return user;
    }

    public static @NonNull RegisterResponseDto toRegisterResponse(User user, String token) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }

        return RegisterResponseDto.builder().token(token).username(user.getUsername()).email(
                user.getEmail()).roles(user.getRoles()).build();
    }

    public static @NonNull AdminResponseDto toAdminResponse(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return AdminResponseDto.builder().id(user.getId()).username(user.getUsername()).email(
                user.getEmail()).roles(user.getRoles()).build();
    }

    public static @NonNull LoginResponseDto toLoginResponse(User user, String token) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }

        return LoginResponseDto.builder().token(token).username(user.getUsername()).roles(
                user.getRoles()).build();
    }
}
