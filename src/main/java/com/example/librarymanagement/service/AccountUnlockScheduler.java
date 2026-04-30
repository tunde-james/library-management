package com.example.librarymanagement.service;

import java.time.Instant;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.librarymanagement.entity.User;
import com.example.librarymanagement.repository.UserRepository;

@Component
public class AccountUnlockScheduler {

    private static final Logger log = LoggerFactory.getLogger(AccountUnlockScheduler.class);

    private final UserRepository userRepository;

    public AccountUnlockScheduler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 300_000)
    @Transactional
    public void unlockExpiredAccounts() {

        List<User> lockedUsers =
                userRepository.findByAccountNonLockedFalseAndLockedUntilBefore(Instant.now());

        for (User user : lockedUsers) {
            user.setAccountNonLocked(true);
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);

            userRepository.save(user);

            log.info("Auto-unlocked account {}", user.getUsername());
        }

        if (!lockedUsers.isEmpty()) {
            log.info("Unlocked {} expires account(s)", lockedUsers.size());
        }
    }
}
