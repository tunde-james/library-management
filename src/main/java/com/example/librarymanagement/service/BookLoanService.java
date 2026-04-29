package com.example.librarymanagement.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.librarymanagement.dto.bookloan.BookLoanRequestDto;
import com.example.librarymanagement.dto.bookloan.BookLoanResponseDto;
import com.example.librarymanagement.dto.bookloan.BookReturnRequestDto;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.BookLoans;
import com.example.librarymanagement.entity.User;
import com.example.librarymanagement.exception.BookNotAvailableException;
import com.example.librarymanagement.exception.BookNotFoundException;
import com.example.librarymanagement.exception.LoanNotFoundException;
import com.example.librarymanagement.exception.UserNotFoundException;
import com.example.librarymanagement.mapper.BookLoanMapper;
import com.example.librarymanagement.repository.BookLoanRepository;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.UserRepository;

@Service
public class BookLoanService {

    private final BookLoanRepository bookLoanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookLoanService(BookLoanRepository bookLoanRepository, BookRepository bookRepository,
            UserRepository userRepository) {
        this.bookLoanRepository = bookLoanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public List<BookLoanResponseDto> issueBooks(@NonNull BookLoanRequestDto request) {

        Long validUserId = Objects.requireNonNull(request.getUserId(), "User ID cannot be null");

        User user = userRepository.findById(validUserId).orElseThrow(
                () -> new UserNotFoundException("User not found with ID: " + validUserId));

        List<Long> sortedBookIds = request.getBookIds().stream().sorted().toList();

        List<BookLoanResponseDto> issuedLoans = new ArrayList<>();

        for (Long bookId : sortedBookIds) {

            Long validBookId = Objects.requireNonNull(bookId, "Book ID cannot be null");

            Book book = bookRepository.findByIdForUpdate(validBookId).orElseThrow(
                    () -> new BookNotFoundException("Book not found with ID: " + bookId));

            if (!book.getIsAvailable() || book.getQuantity() < 1) {
                throw new BookNotAvailableException("Book is not available: " + book.getTitle());
            }

            BookLoans loan = BookLoanMapper.toEntity(request, user, book);

            book.setQuantity(book.getQuantity() - 1);
            if (book.getQuantity() == 0) {
                book.setIsAvailable(false);
            }

            bookRepository.save(book);

            BookLoans savedLoans = bookLoanRepository.save(loan);
            issuedLoans.add(BookLoanMapper.toDto(savedLoans));

        }

        return issuedLoans;
    }

    @Transactional
    public List<BookLoanResponseDto> returnBooks(@NonNull BookReturnRequestDto request) {

        List<Long> sortedLoanIds = request.getLoanIds().stream().sorted().toList();

        List<BookLoanResponseDto> returnedBooks = new ArrayList<>();

        for (Long loanId : sortedLoanIds) {

            Long validLoanId = Objects.requireNonNull(loanId, "Loan ID cannot be null");

            BookLoans loan = bookLoanRepository.findByIdForUpdate(validLoanId).orElseThrow(
                    () -> new LoanNotFoundException("Loan not found with ID: " + validLoanId));

            if (Boolean.TRUE.equals(loan.getIsReturned())) {
                throw new IllegalArgumentException(
                        "Book already returned for loan ID: " + validLoanId);
            }

            loan.setIsReturned(true);
            loan.setReturnDate(LocalDate.now());

            Book book = loan.getBook();
            book.setQuantity(book.getQuantity() + 1);
            book.setIsAvailable((true));

            bookRepository.save(book);

            BookLoans savedLoan = bookLoanRepository.save(loan);
            returnedBooks.add(BookLoanMapper.toDto(savedLoan));
        }

        return returnedBooks;
    }

}
