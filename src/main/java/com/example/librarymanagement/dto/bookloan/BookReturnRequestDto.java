package com.example.librarymanagement.dto.bookloan;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class BookReturnRequestDto {

    @NotNull(message = "At least one loan ID is required")
    private List<Long> loanIds;
}
