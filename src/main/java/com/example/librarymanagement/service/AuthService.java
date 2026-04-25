package com.example.librarymanagement.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.librarymanagement.dto.user.LoginRequestDto;
import com.example.librarymanagement.dto.user.LoginResponseDto;
import com.example.librarymanagement.dto.user.RegisterRequestDto;
import com.example.librarymanagement.dto.user.RegisterResponseDto;
import com.example.librarymanagement.entity.User;
import com.example.librarymanagement.exception.EmailAlreadyExistsException;
import com.example.librarymanagement.exception.UsernameAlreadyExistsException;
import com.example.librarymanagement.jwt.JwtService;
import com.example.librarymanagement.mapper.UserMapper;
import com.example.librarymanagement.repository.UserRepository;
import com.example.librarymanagement.security.UserPrincipal;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthService(UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager, JwtService jwtService,
        CustomUserDetailsService customUserDetailsService,
        TokenBlacklistService tokenBlacklistService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Transactional
    public RegisterResponseDto registerUser(
        RegisterRequestDto registerRequestDto) {

        if (userRepository.existsByUsername(registerRequestDto.getUsername())) {
            throw new UsernameAlreadyExistsException(
                "A user with this username already exists: "
                    + registerRequestDto.getUsername());
        }

        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException(
                "A user with this email already exists: "
                    + registerRequestDto.getEmail());
        }

        User user = UserMapper.toEntity(registerRequestDto);

        user
            .setPassword(
                passwordEncoder.encode(registerRequestDto.getPassword()));

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        UserPrincipal userPrincipal = new UserPrincipal(savedUser);
        String token = jwtService.generateToken(userPrincipal);

        return UserMapper.toRegisterResponse(savedUser, token);

    }

    @Transactional
    public RegisterResponseDto createAdmin(
        RegisterRequestDto registerRequestDto) {

        if (userRepository.existsByUsername(registerRequestDto.getUsername())) {
            throw new UsernameAlreadyExistsException(
                "A user with this username already exists: "
                    + registerRequestDto.getUsername());
        }

        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException(
                "A user with this email already exists: "
                    + registerRequestDto.getEmail());
        }

        User user = UserMapper.toEntity(registerRequestDto);

        user
            .setPassword(
                passwordEncoder.encode(registerRequestDto.getPassword()));

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        UserPrincipal userPrincipal = new UserPrincipal(savedUser);
        String token = jwtService.generateToken(userPrincipal);

        return UserMapper.toRegisterResponse(savedUser, token);
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(
                loginRequestDto.getUsername(), loginRequestDto.getPassword()));

        UserPrincipal userPrincipal = (UserPrincipal) customUserDetailsService
            .loadUserByUsername(loginRequestDto.getUsername());

        String token = jwtService.generateToken(userPrincipal);

        return UserMapper.toLoginResponse(userPrincipal.getUser(), token);
    }

    public void logout(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        String token = authHeader.substring(7);
        tokenBlacklistService.blacklistTokens(token);
    }

}
