package com.example.librarymanagement.mapper;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.lang.NonNull;

import com.example.librarymanagement.dto.bookloan.BookLoanRequestDto;
import com.example.librarymanagement.dto.bookloan.BookLoanResponseDto;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.BookLoans;
import com.example.librarymanagement.entity.User;

public class BookLoanMapper {

    private static final double DAILY_LATE_FEE = 200;

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

        LocalDate checkDate =
                bookLoan.getReturnDate() != null ? bookLoan.getReturnDate() : LocalDate.now();

        if (checkDate.isAfter(bookLoan.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(bookLoan.getDueDate(), checkDate);

            dto.setOverdue(true);
            dto.setDaysOverdue(daysLate);
            dto.setLateFee(daysLate * DAILY_LATE_FEE);
        } else {
            dto.setOverdue(false);
            dto.setDaysOverdue(0);
            dto.setLateFee(0.0);
        }

        dto.setCurrency("NGN");

        return dto;
    }

    public static @NonNull BookLoans toEntity(BookLoanRequestDto request, User user, Book book) {

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
