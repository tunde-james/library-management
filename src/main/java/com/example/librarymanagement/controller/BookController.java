package com.example.librarymanagement.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.librarymanagement.dto.book.BookRequestDto;
import com.example.librarymanagement.dto.book.BookResponseDto;
import com.example.librarymanagement.service.BookService;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<Page<BookResponseDto>> getAllBooks(
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {

        Page<BookResponseDto> books = bookService.getAllBooks(pageable);

        return ResponseEntity.ok().body(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable Long id) {

        return ResponseEntity.ok(bookService.getBookById(id));
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> addBook(
            @Valid @RequestBody BookRequestDto bookRequestDto) {

        BookResponseDto bookResponseDto = bookService.addBook(bookRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(bookResponseDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> updateBook(@PathVariable Long id,
            @Valid @RequestBody BookRequestDto bookRequestDto) {

        BookResponseDto bookResponseDto = bookService.updateBook(id, bookRequestDto);

        return ResponseEntity.ok().body(bookResponseDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {

        bookService.deleteBook(id);

        return ResponseEntity.noContent().build();
    }

}
