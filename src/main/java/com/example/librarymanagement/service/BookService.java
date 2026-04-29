package com.example.librarymanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.librarymanagement.dto.book.BookRequestDto;
import com.example.librarymanagement.dto.book.BookResponseDto;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.exception.BookAlreadyExistsException;
import com.example.librarymanagement.exception.BookNotFoundException;
import com.example.librarymanagement.mapper.BookMapper;
import com.example.librarymanagement.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public List<BookResponseDto> getAllBooks() {

        List<Book> books = bookRepository.findAll();

        List<BookResponseDto> bookResponseDtos =
                books.stream().map(book -> bookMapper.toDto(book)).toList();

        return bookResponseDtos;
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
        book.setDeleted(true);
        book.setIsAvailable(false);
        bookRepository.save(book);
    }
}
