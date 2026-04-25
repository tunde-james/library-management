package com.example.librarymanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.example.librarymanagement.dto.user.AdminResponseDto;
import com.example.librarymanagement.dto.user.RegisterRequestDto;
import com.example.librarymanagement.service.AuthService;


@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AdminResponseDto> createAdmin(
        @Valid @RequestBody RegisterRequestDto registerRequestDto) {

        AdminResponseDto adminResponseDto =
            authService.createAdmin(registerRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(adminResponseDto);

    }
}
