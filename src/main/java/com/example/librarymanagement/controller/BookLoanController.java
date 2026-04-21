package com.example.librarymanagement.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.example.librarymanagement.dto.bookloan.BookLoanRequestDto;
import com.example.librarymanagement.dto.bookloan.BookLoanResponseDto;
import com.example.librarymanagement.dto.bookloan.BookReturnRequestDto;
import com.example.librarymanagement.service.BookLoanService;

@RestController
@RequestMapping("/api/book-loans")
public class BookLoanController {

    private final BookLoanService bookLoanService;

    public BookLoanController(BookLoanService bookLoanService) {
        this.bookLoanService = bookLoanService;
    }

    @PostMapping
    public ResponseEntity<List<BookLoanResponseDto>> loanBook(
        @Valid @RequestBody BookLoanRequestDto bookLoanRequestDto) {

        List<BookLoanResponseDto> loans = bookLoanService
            .issueBooks(Objects.requireNonNull(bookLoanRequestDto));

        return ResponseEntity.status(HttpStatus.CREATED).body(loans);
    }

    @PutMapping("/return")
    public ResponseEntity<List<BookLoanResponseDto>> returnBooks(
        @Valid @RequestBody BookReturnRequestDto bookReturnRequestDto) {

        List<BookLoanResponseDto> returnedLoans = bookLoanService
            .returnBooks(Objects.requireNonNull(bookReturnRequestDto));

        return ResponseEntity.ok(returnedLoans);
    }
}
