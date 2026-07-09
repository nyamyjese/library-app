package com.hei.school.dto.response;

import java.util.UUID;

public record BookStockStatusResponse(
    UUID bookId,
    String bookTitle,
    long totalCopies,
    long availableCopies,
    long soldCopies,
    long damagedCopies,
    long lostCopies) {}
