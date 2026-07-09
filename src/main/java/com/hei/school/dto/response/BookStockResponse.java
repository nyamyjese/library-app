package com.hei.school.dto.response;

import java.util.UUID;

public record BookStockResponse(
    UUID bookCopyId,
    String bookTitle,
    String bookCopyIsbn,
    Integer totalIn,
    Integer totalOut,
    Integer currentStock) {}
