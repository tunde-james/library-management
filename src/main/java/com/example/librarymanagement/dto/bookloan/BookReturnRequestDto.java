package com.example.librarymanagement.dto.bookloan;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class BookReturnRequestDto {

    @NotEmpty(message = "At least one loan ID is required")
    private List<Long> loanIds;
}
