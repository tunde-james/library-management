package com.example.librarymanagement.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class BookRequestDto {

    @NotBlank(message = "Book title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Book author is required")
    @Size(max = 150, message = "Author name cannot exceed 150 characters")
    private String author;

    @NotBlank(message = "ISBN code is required")
    @Size(min = 10, max = 17, message = "ISBN must be between 10 and 17 characters")
    private String isbn;

    @NotNull(message = "Book quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;


    @NotNull(message = "Availability status is required")
    private Boolean isAvailable;
}
