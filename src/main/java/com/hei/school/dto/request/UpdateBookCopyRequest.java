package com.hei.school.dto.request;

import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record UpdateBookCopyRequest(
    @NotNull(message = "The format is mandatory") BookCopyFormat format,
    @NotNull(message = "The selling price is mandatory")
        @Positive(message = "Selling price must be positive")
        BigDecimal sellingPrice,
    @NotNull(message = "The status is mandatory") BookCopyStatus status) {}
