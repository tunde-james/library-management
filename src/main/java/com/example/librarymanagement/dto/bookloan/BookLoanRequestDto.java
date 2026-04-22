package com.example.librarymanagement.dto.bookloan;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class BookLoanRequestDto {

    @NotEmpty(message = "At least one book ID is required")
    private List<Long> bookIds;

    @NotNull(message = "Due days is required")
    @Min(value = 1, message = "Due days must be at least 1")
    private Integer dueDays;
}
