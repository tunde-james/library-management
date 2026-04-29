package com.example.librarymanagement.service;

import java.time.Instant;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.JwtException;

import com.example.librarymanagement.dto.user.AdminResponseDto;
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

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static long LOCK_DURATION_MINUTES = 30;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
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
    public RegisterResponseDto registerUser(RegisterRequestDto registerRequestDto) {

        User savedUser = createUserWithRole(registerRequestDto, "ROLE_USER");

        UserPrincipal userPrincipal = new UserPrincipal(savedUser);
        String token = jwtService.generateToken(userPrincipal);

        return UserMapper.toRegisterResponse(savedUser, token);

    }

    @Transactional
    public AdminResponseDto createAdmin(RegisterRequestDto registerRequestDto) {

        User savedUser = createUserWithRole(registerRequestDto, "ROLE_ADMIN");

        return UserMapper.toAdminResponse(savedUser);
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        unlockIfExpired(loginRequestDto.getUsername());

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequestDto.getUsername(), loginRequestDto.getPassword()));

        } catch (BadCredentialsException ex) {
            handleFailedLogin(loginRequestDto.getUsername());
            throw ex;
        }

        handleSuccessfulLogin(loginRequestDto.getUsername());

        UserPrincipal userPrincipal = (UserPrincipal) customUserDetailsService
                .loadUserByUsername(loginRequestDto.getUsername());

        String token = jwtService.generateToken(userPrincipal);

        return UserMapper.toLoginResponse(userPrincipal.getUser(), token);
    }

    public void logout(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Invalid authorization header");
        }

        String token = authHeader.substring(7);

        try {
            jwtService.extractUsername(token);
        } catch (JwtException ex) {
            throw new BadCredentialsException("Invalid or expired token");
        }

        if (tokenBlacklistService.isBlacklisted(token)) {
            return;
        }

        tokenBlacklistService.blacklistToken(token);
    }

    private User createUserWithRole(RegisterRequestDto request, String role) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException(
                    "A user with this username already exists: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "A user with this email already exists: " + request.getEmail());
        }

        User user = UserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(role));

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UsernameAlreadyExistsException(
                        "A user with this username already exists: " + request.getUsername());
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException(
                        "A user with this email already exists: " + request.getEmail());
            }

            throw new DataIntegrityViolationException(
                    "Registration failed due to a conflict: " + ex.getMessage());
        }
    }

    @Transactional
    public void handleFailedLogin(String username) {

        userRepository.findByUsername(username).ifPresent(user -> {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setAccountNonLocked(false);
                user.setLockedUntil(Instant.now().plusSeconds(LOCK_DURATION_MINUTES * 60));
            }

            userRepository.save(user);
        });
    }

    @Transactional
    public void handleSuccessfulLogin(String username) {

        userRepository.findByUsername(username).ifPresent(user -> {
            user.setFailedLoginAttempts(0);
            user.setAccountNonLocked(true);
            user.setLockedUntil(null);
            userRepository.save(user);
        });
    }

    @Transactional
    public void unlockIfExpired(String username) {

        userRepository.findByUsername(username).ifPresent(user -> {
            if (!user.isAccountNonLocked() && user.getLockedUntil() != null
                    && Instant.now().isAfter(user.getLockedUntil())) {

                user.setAccountNonLocked(true);
                user.setFailedLoginAttempts(0);
                user.setLockedUntil(null);
                userRepository.save(user);
            }
        });
    }

}
