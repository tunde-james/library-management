package com.example.librarymanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.librarymanagement.dto.book.BookRequestDto;
import com.example.librarymanagement.dto.book.BookResponseDto;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.exception.BookAlreadyExistsException;
import com.example.librarymanagement.exception.BookNotFoundException;
import com.example.librarymanagement.mapper.BookMapper;
import com.example.librarymanagement.repository.BookLoanRepository;
import com.example.librarymanagement.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookLoanRepository bookLoanRepository;

    public BookService(BookRepository bookRepository, BookMapper bookMapper,
            BookLoanRepository bookLoanRepository) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.bookLoanRepository = bookLoanRepository;
    }

    public Page<BookResponseDto> getAllBooks(Pageable pageable) {

        Page<Book> books = bookRepository.findAll(pageable);

        return books.map(book -> bookMapper.toDto(book));


    }

    public BookResponseDto getBookById(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));


        return bookMapper.toDto(book);
    }

    @Transactional
    public BookResponseDto addBook(BookRequestDto bookRequestDto) {

        if (bookRepository.existsByTitle(bookRequestDto.getTitle())) {
            throw new BookAlreadyExistsException(
                    "A book with this title already exists: " + bookRequestDto.getTitle());
        }

        Book bookAdded = bookRepository.save(bookMapper.toEntity(bookRequestDto));

        return bookMapper.toDto((bookAdded));
    }

    @Transactional
    public BookResponseDto updateBook(Long id, BookRequestDto bookRequestDto) {

        if (id == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }

        Book oldBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));

        if (bookRepository.existsByTitleAndIdNot(bookRequestDto.getTitle(), id)) {
            throw new BookAlreadyExistsException(
                    "A book with this title " + "already exists: " + bookRequestDto.getTitle());
        }

        bookMapper.updateEntityFromDto(bookRequestDto, oldBook);

        Book updatedBook = bookRepository.save(oldBook);

        return bookMapper.toDto(updatedBook);
    }

    public void deleteBook(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));

        long activeLoans = bookLoanRepository.countByUserIdAndIsReturnedFalse(id);

        if (activeLoans > 0) {
            throw new IllegalStateException(
                    "Cannot delete book '" + book.getTitle() + "' - it has " + activeLoans
                            + " active loan(s). " + "All copies must be returned first.");
        }

        book.setDeleted(true);
        book.setIsAvailable(false);
        bookRepository.save(book);
    }
}
