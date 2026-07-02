package com.hei.school.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ArrivalResponse(
    UUID id,
    UUID bookCopyId,
    String bookTitle,
    String bookCopyIsbn,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice,
    Instant arrivalDate) {}
