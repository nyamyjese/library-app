package com.hei.school.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;

public record UpdateArrivalRequest(
    @NotNull(message = "The quantity is mandatory") @Positive(message = "Quantity must be positive")
        Integer quantity,
    @NotNull(message = "The unit price is mandatory")
        @Positive(message = "Unit price must be positive")
        BigDecimal unitPrice,
    @NotNull(message = "The arrival date is mandatory") Instant arrivalDate) {}
