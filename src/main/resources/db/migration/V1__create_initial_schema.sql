-- V1__create_initial_schema.sql
-- Initial database schema for Library Management System

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    roles VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, roles),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(150) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    quantity INT NOT NULL,
    is_available BOOLEAN NOT NULL
);

CREATE TABLE book_loans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    is_returned BOOLEAN NOT NULL,
    user_id BIGINT,
    book_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);
