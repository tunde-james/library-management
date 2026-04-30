package com.example.librarymanagement.entity;

import java.time.Instant;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;

import org.hibernate.annotations.SQLRestriction;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@SQLRestriction("deleted = false")
public class User extends BaseEntity {

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean accountNonLocked = true;

    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        User user = (User) o;
        return getId() != null && getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
