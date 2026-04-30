package com.example.librarymanagement.security;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.librarymanagement.entity.User;

public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return user.getRoles().stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {

        return user.getPassword();
    }

    @Override
    public String getUsername() {

        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        if (user.isAccountNonLocked()) {
            return true;
        }

        if (user.getLockedUntil() != null && Instant.now().isAfter(user.getLockedUntil())) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return user.isEnabled();
    }

    public User getUser() {

        return user;
    }

}
