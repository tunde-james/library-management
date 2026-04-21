package com.example.librarymanagement.mapper;

import java.time.LocalDate;

import org.springframework.lang.NonNull;

import com.example.librarymanagement.dto.bookloan.BookLoanRequestDto;
import com.example.librarymanagement.dto.bookloan.BookLoanResponseDto;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.BookLoans;
import com.example.librarymanagement.entity.User;

public class BookLoanMapper {

    public static @NonNull BookLoanResponseDto toDto(BookLoans bookLoan) {

        if (bookLoan == null) {
            throw new IllegalArgumentException("BookLoan cannot be null");
        }

        BookLoanResponseDto dto = new BookLoanResponseDto();

        dto.setId(bookLoan.getId());
        dto.setIssueDate(bookLoan.getIssueDate());
        dto.setDueDate(bookLoan.getDueDate());
        dto.setReturnDate(bookLoan.getReturnDate());
        dto.setIsReturned(bookLoan.getIsReturned());
        dto.setUserId(bookLoan.getUser().getId());
        dto.setBookId(bookLoan.getBook().getId());

        return dto;
    }

    public static @NonNull BookLoans toEntity(BookLoanRequestDto request,
        User user, Book book) {

        if (request == null)
            throw new IllegalArgumentException("Request cannot be null");
        if (user == null)
            throw new IllegalArgumentException("User cannot be null");
        if (book == null)
            throw new IllegalArgumentException("Book cannot be null");

        BookLoans loan = new BookLoans();
        loan.setUser(user);
        loan.setBook(book);
        loan.setIssueDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(request.getDueDays()));
        loan.setIsReturned(false);

        return loan;
    }
}
