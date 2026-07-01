package com.hei.school.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record SaleItemResponse(
    UUID id,
    UUID bookCopyId,
    String bookTitle,
    String bookCopyIsbn,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice) {}
