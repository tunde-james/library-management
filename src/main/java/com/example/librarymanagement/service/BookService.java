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

    private BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookResponseDto> getAllBooks() {

        List<Book> books = bookRepository.findAll();

        List<BookResponseDto> bookResponseDtos =
            books.stream().map(book -> BookMapper.toDto(book)).toList();

        return bookResponseDtos;
    }

    public BookResponseDto getBookById(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }

        Book book = bookRepository.findById(id).orElseThrow(
            () -> new BookNotFoundException("Book not found with ID: " + id));


        return BookMapper.toDto(book);
    }

    @Transactional
    public BookResponseDto addBook(BookRequestDto bookRequestDto) {

        if (bookRepository.existsByTitle(bookRequestDto.getTitle())) {
            throw new BookAlreadyExistsException(
                "A book with this title already exists: "
                    + bookRequestDto.getTitle());
        }

        Book bookAdded =
            bookRepository.save(BookMapper.toEntity(bookRequestDto));

        return BookMapper.toDto((bookAdded));
    }

    @Transactional
    public BookResponseDto updateBook(Long id, BookRequestDto bookRequestDto) {

        if (id == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }

        Book oldBook = bookRepository.findById(id).orElseThrow(
            () -> new BookNotFoundException("Book not found with ID: " + id));

        if (bookRepository.existsByTitleAndIdNot(bookRequestDto.getTitle(),
            id)) {
            throw new BookAlreadyExistsException("A book with this title "
                + "already exists: " + bookRequestDto.getTitle());
        }

        oldBook.setTitle(bookRequestDto.getTitle());
        oldBook.setAuthor(bookRequestDto.getAuthor());
        oldBook.setIsbn(bookRequestDto.getIsbn());
        oldBook.setQuantity(bookRequestDto.getQuantity());
        oldBook.setIsAvailable(bookRequestDto.getIsAvailable());

        Book updatedBook = bookRepository.save(oldBook);

        return BookMapper.toDto(updatedBook);
    }

    public void deleteBook(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }

        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not foind with ID: " + id);
        }

        bookRepository.deleteById(id);
    }
}
