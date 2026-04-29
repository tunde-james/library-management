-- V3__add_indexes_and_user_fields.sql
-- Add indexes for performance and new user fields

CREATE INDEX idx_book_loans_user_id ON book_loans(user_id);
CREATE INDEX idx_book_loans_book_id ON book_loans(book_id);
CREATE INDEX idx_book_loans_is_returned ON book_loans(is_returned);

-- New user fields for account security
ALTER TABLE users ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE users ADD COLUMN account_non_locked BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE users ADD COLUMN failed_login_attempts INT NOT NULL DEFAULT 0;
