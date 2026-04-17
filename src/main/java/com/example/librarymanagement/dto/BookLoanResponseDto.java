package com.example.librarymanagement.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BookLoanResponseDto {

    private Long id;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Boolean isReturned;
    private Long userId;
    private Long bookId;
}
