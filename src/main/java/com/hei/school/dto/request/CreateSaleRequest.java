package com.hei.school.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateSaleRequest(
    @NotNull(message = "The customer id is mandatory") UUID customerId,
    @NotNull(message = "The library id is mandatory") UUID libraryId,
    @NotNull(message = "The sale date is mandatory") Instant saleDate,
    @NotNull(message = "The sale items are mandatory")
        @NotEmpty(message = "Sale must contain at least one item")
        List<CreateSaleItemRequest> saleItems) {}
