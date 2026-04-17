package com.example.librarymanagement.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class BookLoanRequestDto {

    // @NotNull(message = "User ID is required")
    // private Long userId;

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @NotNull(message = "Issue date is required")
    @FutureOrPresent(message = "Issue date cannot be in the past")
    private LocalDate issueDate;

    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDate dueDate;
}
