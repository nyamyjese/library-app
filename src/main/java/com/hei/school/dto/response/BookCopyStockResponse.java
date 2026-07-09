package com.hei.school.dto.response;

import java.util.UUID;

public record BookCopyStockResponse(
    UUID bookCopyId,
    String bookTitle,
    String isbn,
    long availableCopies,
    long soldCopies,
    long damagedCopies,
    long lostCopies) {}
