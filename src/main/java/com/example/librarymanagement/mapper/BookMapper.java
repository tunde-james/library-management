package com.example.librarymanagement.mapper;

import org.springframework.lang.NonNull;

import com.example.librarymanagement.dto.book.BookRequestDto;
import com.example.librarymanagement.dto.book.BookResponseDto;
import com.example.librarymanagement.entity.Book;

public class BookMapper {

    public @NonNull static BookResponseDto toDto(Book book) {

        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        BookResponseDto bookDto = new BookResponseDto();

        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setQuantity(book.getQuantity());
        bookDto.setIsAvailable(book.getIsAvailable());

        return bookDto;
    }

    public static @NonNull Book toEntity(BookRequestDto bookRequestDto) {

        if (bookRequestDto == null) {
            throw new IllegalArgumentException("BookRequestDto cannot be null");
        }

        Book book = new Book();

        book.setTitle(bookRequestDto.getTitle());
        book.setAuthor(bookRequestDto.getAuthor());
        book.setIsbn(bookRequestDto.getIsbn());
        book.setQuantity(bookRequestDto.getQuantity());
        book.setIsAvailable(bookRequestDto.getIsAvailable());

        return book;
    }
}
