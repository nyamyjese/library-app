package com.hei.school.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateSaleItemRequest(
        @NotNull(message = "The book copy id is mandatory") UUID bookCopyId,
        @NotNull(message = "The quantity is mandatory") @Positive(message = "Quantity must be positive")
        Integer quantity,
        @NotNull(message = "The unit price is mandatory")
        @Positive(message = "Unit price must be positive")
        BigDecimal unitPrice) {}
