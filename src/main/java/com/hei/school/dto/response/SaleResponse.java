package com.hei.school.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SaleResponse(
    UUID id,
    UUID customerId,
    String customerFirstName,
    String customerLastName,
    UUID libraryId,
    String libraryName,
    Instant saleDate,
    BigDecimal totalAmount,
    List<SaleItemResponse> saleItems,
    Instant createdAt,
    Instant updatedAt) {}
