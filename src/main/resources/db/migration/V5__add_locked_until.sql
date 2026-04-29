-- V5__add_locked_until.sql
ALTER TABLE users ADD COLUMN locked_until TIMESTAMP NULL;
