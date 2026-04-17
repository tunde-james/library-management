package com.example.librarymanagement.dto;

import lombok.Data;

@Data
public class BookResponseDto {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Integer quantity;
    private Boolean isAvailable;
}
