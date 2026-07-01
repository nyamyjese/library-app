package com.hei.school.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public record UpdateSaleRequest(
    @NotNull(message = "The sale date is mandatory") Instant saleDate,
    @NotNull(message = "The sale items are mandatory")
        @NotEmpty(message = "Sale must contain at least one item")
        List<CreateSaleItemRequest> saleItems) {}
