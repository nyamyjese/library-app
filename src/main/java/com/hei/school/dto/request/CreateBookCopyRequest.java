package com.hei.school.dto.request;

import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateBookCopyRequest(
    @NotNull(message = "The book id is mandatory") UUID bookId,
    @NotNull(message = "The library id is mandatory") UUID libraryId,
    @NotNull(message = "The format is mandatory") BookCopyFormat format,
    @NotBlank(message = "The ISBN is mandatory")
        @Size(min = 13, max = 13, message = "ISBN must be exactly 13 characters")
        String isbn,
    @NotNull(message = "The selling price is mandatory")
        @Positive(message = "Selling price must be positive")
        BigDecimal sellingPrice,
    @NotNull(message = "The status is mandatory") BookCopyStatus status) {}
