-- V5__add_locked_until.sql
ALTER TABLE users ADD COLUMN locked_until TIMESTAMP NULL;

CREATE INDEX idx_users_lock_status
    ON users(account_non_locked, locked_until);
