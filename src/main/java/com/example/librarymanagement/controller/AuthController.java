package com.example.librarymanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.example.librarymanagement.dto.user.LoginRequestDto;
import com.example.librarymanagement.dto.user.LoginResponseDto;
import com.example.librarymanagement.dto.user.RegisterRequestDto;
import com.example.librarymanagement.dto.user.RegisterResponseDto;
import com.example.librarymanagement.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> registerUser(
        @Valid @RequestBody RegisterRequestDto registerRequestDto) {

        RegisterResponseDto registerResponseDto =
            authService.registerUser(registerRequestDto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(registerResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
        @Valid @RequestBody LoginRequestDto loginRequestDto) {

        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);

        return ResponseEntity.ok().body(loginResponseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
        @RequestHeader("Authorization") String authHeader) {

        authService.logout(authHeader);

        return ResponseEntity.ok("Logged out successfully");
    }

}
